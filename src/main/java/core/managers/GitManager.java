package core.managers;

import core.comparators.git.IdentByPersonComparator;
import core.comparators.git.TagsByDateComparator;
import exceptions.*;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;
import utils.Validator;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class GitManager implements VcsManager {

    private Git git;
    private Repository repo;
    private Logger logger;
    private static GitManager gitManager;
    private File gitDir;
    private PersonIdent personIdent;
    private String remoteRepoUri;
    //ProgressMonitor progressMonitor;

    /**
     * @param gitPathName Path to local repository, must be parent folder for .git or the .git folder
     * @return Singleton instance of GitManager
     * @throws VcsRemoteConnectionException
     * @throws VcsUnknownException
     * @throws VcsWrongRemoteRepoException
     * @throws VcsWrongLocalRepoException
     */
    public static VcsManager getInstance(String gitPathName) throws VcsRemoteConnectionException, VcsUnknownException, VcsWrongLocalRepoException, VcsWrongRemoteRepoException {
        if (gitManager == null) {
            gitManager = createManager(gitPathName);
        }
        gitManager.fetchWithTags();
        return gitManager;
    }

    public static VcsManager getInstance(File gitPath) throws VcsRemoteConnectionException, VcsUnknownException, VcsWrongLocalRepoException, VcsWrongRemoteRepoException {
        if (gitManager == null) {
            gitManager = createManager(gitPath);
        }
        gitManager.fetchWithTags();
        return gitManager;
    }

    private static GitManager createManager(String gitPathName) throws VcsWrongLocalRepoException {
        return createManager(new File(gitPathName));
    }

    private static GitManager createManager(File gitPath) throws VcsWrongLocalRepoException {
        GitManager result = new GitManager();
        result.logger = LoggerFactory.getLogger(GitManager.class);
        if (gitPath.exists()) {
            if (gitPath.getName().equals(".git")) {
                if (gitPath.isDirectory()) {
                    result.gitDir = gitPath;
                } else {
                    throw new VcsWrongLocalRepoException("Wrong local repo folder ", gitPath);
                }
            } else {
                File[] files = gitPath.listFiles();
                Optional<File> first = Arrays.stream(files).filter(tempFile -> tempFile.isDirectory() && tempFile.getName().equals(".git")).findFirst();
                try {
                    result.gitDir = first.get();
                } catch (Throwable e) {
                    throw new VcsWrongLocalRepoException("Wrong local repo folder ", gitPath);
                }
            }
        } else {
            throw new VcsWrongLocalRepoException("Wrong local repo folder ", gitPath);
        }
        result.logger.debug("Git local repo folder is: \"" + result.gitDir.getAbsolutePath() + "\"");
        result.setRepository();
        result.personIdent = new PersonIdent(result.repo);
        result.git = new Git(result.repo);
        result.remoteRepoUri = result.repo.getConfig().getString("remote", "origin", "url");
        return result;

    }

    private void setRepository() throws VcsWrongLocalRepoException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        try {
            repo = builder.build();
        } catch (IOException e) {
            throw new VcsWrongLocalRepoException("Can't create local repo in folder", gitDir, e);
        }
    }

    private void fetchWithTags() throws VcsRemoteConnectionException, VcsUnknownException, VcsWrongRemoteRepoException {
        try {
            FetchCommand fetch = git.fetch();
            //fetch.setProgressMonitor(progressMonitor);
            fetch.setTagOpt(TagOpt.FETCH_TAGS).call();
            logger.debug("Fetch from " + remoteRepoUri + " SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VcsWrongRemoteRepoException("Wrong remote repo: " + remoteRepoUri, e);
        } catch (TransportException e) {
            throw new VcsRemoteConnectionException("Connection error: can't connect to " + remoteRepoUri, e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    private GitManager() {
    }

    @Override
    public void createTag(String tagName, String commitRevision) throws VcsCommitNotFoundException, VcsUnknownException, VcsInvalidTagNameException, VcsRepositoryException, VcsRemoteConnectionException {
        ObjectId objectId = ObjectId.fromString(commitRevision);

        Iterable<RevCommit> commits;
        try {
            commits = git.log().call();
        } catch (NoHeadException e) {
            throw new VcsRepositoryException("Can't get commits", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
        RevObject commitRevObject = null;

        for (RevCommit commit : commits) {
            if (commit.getId().compareTo(objectId) == 0) {
                logger.debug(String.format("Commit %s from %s found.", commit.getName(), Utils.convert(commit.getCommitTime())));
                commitRevObject = commit;
                break;
            }
        }
        if (commitRevObject == null) {
            throw new VcsCommitNotFoundException("Commit " + commitRevision + " not found");
        }
        //Create tag in local repo
        TagCommand tagCommand = git.tag().setObjectId(commitRevObject);
        tagCommand.setName(tagName);
        tagCommand.setAnnotated(true);
        try {
            tagCommand.call();
            logger.info("Created tag \"" + tagName + "\" on commit " + commitRevObject.getName());
        } catch (RefAlreadyExistsException e) {
            throw new VcsInvalidTagNameException("The tag " + tagName + " already exists", e);
        } catch (InvalidTagNameException e) {
            throw new VcsInvalidTagNameException("Wrong tags name: " + tagName, e);
        } catch (NoHeadException | ConcurrentRefUpdateException e) {
            throw new VcsRepositoryException("Can't create tag in local repo, please fix you local repo and try again", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }

        //Push all tags from local repo to remote
        try {
            git.push().setPushTags().call();
            logger.debug("Push for tag \"" + tagName + "\" SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VcsRepositoryException("Can't push tag", e);
        } catch (TransportException e) {
            throw new VcsRemoteConnectionException("Can't push tag", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    private String[] getAllTags() {
        Map<String, Ref> tags = repo.getTags();
        Collection<Ref> values = tags.values();
        String[] names = values.stream().map(tempRef -> tempRef.getName()).toArray(String[]::new);
        return names;
    }

    @Override
    public void deleteTags(String namePrefix, int numberTagsToLeft) throws VcsTagNotFoundException, VcsUnknownException, VcsRemoteConnectionException, VcsWrongRemoteRepoException {
        if (numberTagsToLeft > 0) {
            //  fetchWithTags();
            List<RevTag> tagsToDelete = tagsToDelete(namePrefix);
            if (tagsToDelete == null || tagsToDelete.size() == 0) {
                throw new VcsTagNotFoundException("Tags with prefix \"" + namePrefix + "\" not found");
            }
//            String messageFormat="Tag matches to \"%s\" is %s(%s) %s";
//            if (logger.isDebugEnabled()) {
//                logger.debug("--------------");
//                tagsToDelete.stream().forEach(tempTag -> logger.debug(String.format(messageFormat, namePrefix, tempTag.getTagName(), tempTag.getName(), Utils.convert(tempTag.getTaggerIdent().getWhen()))));
//                logger.debug("--------------");
//            }
            int size = tagsToDelete.size();

            List<RevTag> revTagsToDelete;
            if (size > numberTagsToLeft) {
                tagsToDelete.sort(new TagsByDateComparator<RevTag>());
                revTagsToDelete = tagsToDelete.subList(0, size - numberTagsToLeft);
                String[] tagsToDeleteName = revTagsToDelete.stream().map(tempTag -> tempTag.getTagName()).toArray(String[]::new);
                deleteLocalAndRemouteTags(tagsToDeleteName);
                if (logger.isDebugEnabled()) {
                    logger.debug("--------------");
                    revTagsToDelete.stream().forEach(tempTag -> logger.debug(String.format("Tag to delete: %s from %s", tempTag.getName(), Utils.convert(tempTag.getTaggerIdent().getWhen()))));
                    logger.debug("--------------");
                }
            }
        }
    }

    private void deleteLocalAndRemouteTags(String... tagsRefFullNames) throws VcsUnknownException, VcsRemoteConnectionException, VcsWrongRemoteRepoException {
        try {
            git.tagDelete().setTags(tagsRefFullNames).call();
            logger.debug("Delete local tags SUCCESS");
            removeTagsFromRemoteGit(tagsRefFullNames);
        } catch (RefNotFoundException e) {
            logger.warn("One of tags not found");
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    private void removeTagsFromRemoteGit(String... tagsRefFullNames) throws VcsRemoteConnectionException, VcsUnknownException, VcsWrongRemoteRepoException {
        RefSpec[] refsForRemoteDelete = new RefSpec[tagsRefFullNames.length];
        for (int i = 0; i < tagsRefFullNames.length; i++) {
            refsForRemoteDelete[i] = new RefSpec(":refs/tags/" + tagsRefFullNames[i]);
        }
        try {
            git.push().setRefSpecs(refsForRemoteDelete).call();
            logger.debug("Delete tags from remote repo " + remoteRepoUri + " SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VcsWrongRemoteRepoException("Wrong remote repo: " + remoteRepoUri, e);
        } catch (TransportException e) {
            throw new VcsRemoteConnectionException("Connection error: couldn't connect to remote repo for delete", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    private List<RevTag> tagsToDelete(String namePrefix) throws VcsUnknownException, VcsTagNotFoundException {
        Collection<Ref> refTags = repo.getTags().values();
        RevWalk revWalk = new RevWalk(repo);
        ArrayList<RevTag> resultRevTags = new ArrayList<>();
        int maxTagNameLength = refTags.stream().max((tempTag1, tempTag2) -> Integer.compare(tempTag1.getName().length(), tempTag2.getName().length())).get().getName().length() - 10;
        for (Ref refObject : refTags) {
            ObjectId objectId = refObject.getObjectId();
            int objectType = 0;
            try {
                objectType = revWalk.parseAny(objectId).getType();
                if (objectType == 4) {
                    RevTag revTag = revWalk.parseTag(objectId);
                    String commitId = revTag.getObject().getId().getName().substring(0, 8);
                    String message = String.format("Found annotated tag %-" + maxTagNameLength + "s from %s on commit %s", revTag.getTagName(), Utils.convert(revTag.getTaggerIdent().getWhen()), commitId);

                    int personCompare = new IdentByPersonComparator().compare(personIdent, revTag.getTaggerIdent());
                    if (personCompare == 0 && revTag.getTagName().startsWith(namePrefix)) {
                        resultRevTags.add(revTag);
                        message += "*";
                    }
                    logger.debug(message);
                }
            } catch (MissingObjectException e) {
                throw new VcsTagNotFoundException(e);
            } catch (IOException e) {
                throw new VcsUnknownException(e);
            }
        }
        logger.info(String.format("Found %d tags from tagger %s(%s) starts from %s", resultRevTags.size(), personIdent.getName(), personIdent.getEmailAddress(), namePrefix));
        revWalk.close();
        return resultRevTags;
    }

//    private List<RevTag> tagsToDelete(Collection<RevTag> allTags, String namePrefix) {
//        List<RevTag> collect = allTags.stream()
//                .filter(temTag -> equalIdent(temTag.getTaggerIdent()) && temTag.getName().startsWith(namePrefix))
//                .collect(Collectors.toList());
//        return collect;
//    }

    @Override
    public boolean isValidCommitRev(String commitRev) {
        return Validator.isValidGitCommitRev(commitRev);
    }

    @Override
    public boolean isValidTagName(String namePrefix) {
        return Validator.isValidGitRefName(namePrefix);
    }

}
