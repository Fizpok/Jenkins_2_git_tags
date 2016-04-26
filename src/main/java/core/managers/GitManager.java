package core.managers;

import core.TagsByDateComparator;
import exceptions.*;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class GitManager implements VCSManager {

    private Git git;
    private Repository repo;
    private String gitPathName;
    private Logger logger;
    private static GitManager gitManager;
    //ProgressMonitor progressMonitor;


    public static VCSManager getInstance() throws VCSFatalRepositoryException, VCSRemoteConnectionException, VcsUnknownException {
        return getInstance("C:\\Git\\Jenkins\\mqm_for_jenkins_git");
    }

    public static VCSManager getInstance(String gitPathName) throws VCSFatalRepositoryException, VCSRemoteConnectionException, VcsUnknownException {
        if (gitManager == null) {
            gitManager = new GitManager(gitPathName);
        }
        gitManager.fetchWithTags();
        return gitManager;
    }

    private GitManager(String gitPathName) throws VCSFatalRepositoryException {
        if (gitPathName.endsWith(".git")) {
            this.gitPathName = gitPathName;
        } else {
            this.gitPathName = gitPathName + File.separator + ".git";
        }
        setRepository();
        git = new Git(repo);
        logger = LoggerFactory.getLogger(GitManager.class);
    }

    @Override
    public void createTag(String tagName, String commitRevision) throws VCSCommitNotFoundException, VcsUnknownException, VCSInvalidTagNameException, VCSFatalRepositoryException, VCSRemoteConnectionException {
        ObjectId objectId = ObjectId.fromString(commitRevision);

        Iterable<RevCommit> commits;
        try {
            commits = git.log().call();
        } catch (NoHeadException e) {
            throw new VCSFatalRepositoryException("Can't get commits", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
        RevObject commitRevObject = null;

        for (RevCommit commit : commits) {
            if (commit.getId().compareTo(objectId) == 0) {
                StringBuilder sb = new StringBuilder().append("Commit ")
                        .append(commit.getName())
                        .append(" from ")
                        .append(commit.getCommitTime())
                        .append(" found.");
                logger.debug(sb.toString());
                commitRevObject = commit;
                break;
            }
        }
        if (commitRevObject == null) {
            throw new VCSCommitNotFoundException("Commit " + commitRevision + " not found");
        }
        //Create tag in local repo
        TagCommand tagCommand = git.tag().setObjectId(commitRevObject);
        tagCommand.setName(tagName);
        tagCommand.setAnnotated(true);
        try {
            tagCommand.call();
            logger.info("Created tag \"" + tagName + "\" on commit " + commitRevObject.getName());
        } catch (RefAlreadyExistsException e) {
            throw new VCSInvalidTagNameException("The tag " + tagName + " already exists", e);
        } catch (InvalidTagNameException e) {
            throw new VCSInvalidTagNameException("Wrong tags name: " + tagName, e);
        } catch (NoHeadException | ConcurrentRefUpdateException e) {
            throw new VCSFatalRepositoryException("Can't create tag in local repo, please fix you local repo and try again", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }

        //Push all tags from local repo to remote
        try {
            git.push().setPushTags().call();
            logger.debug("Push for tag \"" + tagName + "\" SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Can't push tag", e);
        } catch (TransportException e) {
            throw new VCSRemoteConnectionException("Can't push tag", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    @Override
    public void deleteTags(String namePrefix, int numberTagsToLeft) throws VCSTagNotFoundException, VcsUnknownException, VCSRemoteConnectionException, VCSFatalRepositoryException {
        if (numberTagsToLeft > 0) {
            //  fetchWithTags();
            List<RevTag> allAnnotatedTags = getAllAnnotatedTags();

            Map<String, List<RevTag>> tagsByNamePrefix = tagsByNamePrefix(allAnnotatedTags);
            List<RevTag> tagsToDelete = tagsByNamePrefix.get(namePrefix);
            if (tagsToDelete == null || tagsToDelete.size() == 0) {
                throw new VCSTagNotFoundException("Tags with prefix \"" + namePrefix + "\" not found");
            }
            tagsToDelete.sort(new TagsByDateComparator<RevTag>());
            //TODO-EVG logger
            if (logger.isDebugEnabled()) {
                logger.debug("--------------");
                tagsToDelete.stream().forEach(tempTag -> logger.debug("Tag matches to \"" + namePrefix + "\" is " + tempTag.getTagName() + "(" + tempTag.getName() + ") " + tempTag.getTaggerIdent().getWhen()));
                logger.debug("--------------");
            }
            int size = tagsToDelete.size();

            List<RevTag> revTagsToDelete;
            if (size > numberTagsToLeft) {
                revTagsToDelete = tagsToDelete.subList(0, size - numberTagsToLeft);
                String[] tagsToDeleteName = revTagsToDelete.stream().map(tempTag -> tempTag.getTagName()).toArray(String[]::new);
                deleteTags(tagsToDeleteName);
                if (logger.isDebugEnabled()) {
                    logger.debug("--------------");
                    revTagsToDelete.stream().forEach(tempTag -> logger.debug("Tag to delete: " + tempTag.getName() + " from " + tempTag.getTaggerIdent().getWhen()));
                    logger.debug("--------------");
                }
            }
        }
    }

    private void setRepository() throws VCSFatalRepositoryException {
        File gitDir = new File(gitPathName);
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        try {
            repo = builder.build();
        } catch (IOException e) {
            throw new VCSFatalRepositoryException("Can't create local repo", e);
        }
    }

    private void fetchWithTags() throws VCSFatalRepositoryException, VCSRemoteConnectionException, VcsUnknownException {
        try {
            FetchCommand fetch = git.fetch();
            //fetch.setProgressMonitor(progressMonitor);
            fetch.setTagOpt(TagOpt.FETCH_TAGS).call();
            logger.info("Fetch SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Wrong remote repo", e);
        } catch (TransportException e) {
            throw new VCSRemoteConnectionException("Connection error: couldn't fetch", e);
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

    private void deleteTags(String... tagsRefFullNames) throws VcsUnknownException, VCSFatalRepositoryException, VCSRemoteConnectionException {
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

    private void removeTagsFromRemoteGit(String... tagsRefFullNames) throws VCSFatalRepositoryException, VCSRemoteConnectionException, VcsUnknownException {
        RefSpec[] refsForRemoteDelete = new RefSpec[tagsRefFullNames.length];
        for (int i = 0; i < tagsRefFullNames.length; i++) {
            refsForRemoteDelete[i] = new RefSpec(":refs/tags/" + tagsRefFullNames[i]);
        }
        try {
            git.push().setRefSpecs(refsForRemoteDelete).call();
            logger.debug("Delete tags from remote repo SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Wrong remote repo", e);
        } catch (TransportException e) {
            throw new VCSRemoteConnectionException("Connection error: couldn't connect to remote repo for delete", e);
        } catch (GitAPIException e) {
            throw new VcsUnknownException(e);
        }
    }

    private List<RevTag> getAllAnnotatedTags() throws VcsUnknownException, VCSTagNotFoundException {
        Map<String, Ref> tags = repo.getTags();
        Collection<Ref> values = tags.values();
        RevWalk revWalk = new RevWalk(repo);
        ArrayList<RevTag> resultRevTags = new ArrayList<>();

        for (Ref tag : values) {
            ObjectId objectId = tag.getObjectId();
            int objectType = 0;
            try {
                objectType = revWalk.parseAny(objectId).getType();
                if (objectType == 4) {
                    RevTag revTag = revWalk.parseTag(objectId);
                    resultRevTags.add(revTag);
                    logger.debug("Annotated tag " + revTag.getTagName() + " retrieve successful ");
                }
            } catch (MissingObjectException e) {
                throw new VCSTagNotFoundException(e);
            } catch (IOException e) {
                throw new VcsUnknownException(e);
            }
        }
        revWalk.close();
        return resultRevTags;
    }

    private Map<String, List<RevTag>> tagsByNamePrefix(Collection<RevTag> tags) {
        Map<String, List<RevTag>> collect = tags.stream()
                .filter(temTag -> temTag.getTagName().contains("#"))
                .collect(Collectors.groupingBy(tempTag ->
                                tempTag.getTagName().substring(0, tempTag.getTagName().lastIndexOf("#")))
                );
        return collect;
    }

    @Override
    public boolean isValidCommitRev(String commitRev) {
        return commitRev.matches("^[0-9a-fA-F]{40}$");
    }

    @Override
    public boolean isValidRefName(String namePrefix) {
        boolean isValid;
        if (namePrefix == null || namePrefix.isEmpty()) {
            isValid = false;
        } else {
            isValid = true;
            isValid &= namePrefix.matches("^[^@/][\\d\\w!@#$%&_\\.]+[^\\.]$");
            isValid &= !namePrefix.matches(".*\\.lock$");
            isValid &= !namePrefix.matches("\\.\\.");
            isValid &= !namePrefix.matches("\\\\");
            isValid &= !namePrefix.matches("^/");
            isValid &= !namePrefix.matches("/$");
            isValid &= !namePrefix.matches("@\\{");
        }
        return isValid;
    }

}
