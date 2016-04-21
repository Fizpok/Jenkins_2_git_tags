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
import java.util.*;
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
    Queue<String> tagsToRemoteRemove;

    public static VCSManager getInstance() throws VCSFatalRepositoryException, VCSRemouteConnectionException, UnknownGitException {
        return getInstance("C:\\Git\\Jenkins\\mqm_for_jenkins_git");
    }

    public static VCSManager getInstance(String gitPathName) throws VCSFatalRepositoryException, VCSRemouteConnectionException, UnknownGitException {
        if (gitManager == null) {
            gitManager = new GitManager(gitPathName);
        }
        gitManager.fetchWithTags();
        return gitManager;
    }

    private GitManager(String gitPathName) throws VCSFatalRepositoryException {
        this.gitPathName = gitPathName;
        setRepository();
        git = new Git(repo);
        logger = LoggerFactory.getLogger(GitManager.class);
    }

    @Override
    public void createTag(String tagName, String commitRevision) throws VCSCommitNotFoundException, UnknownGitException, VCSInvalidTagNameException, VCSFatalRepositoryException, VCSRemouteConnectionException {
        ObjectId objectId = ObjectId.fromString(commitRevision);

        Iterable<RevCommit> commits = null;
        try {
            commits = git.log().call();
        } catch (NoHeadException e) {
            throw new VCSFatalRepositoryException("Can't get commits", e);
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }
        RevObject commitRevObject = null;

        for (RevCommit commit : commits) {
            if (commit.getId().compareTo(objectId) == 0) {
                logger.info("Commit "+commit.getId()+" found");
                commitRevObject = commit;
                break;
            }
        }
        if (commitRevObject == null) {
            throw new VCSCommitNotFoundException("Commit " + commitRevision + " not found");
        }

        TagCommand tagCommand = git.tag().setObjectId(commitRevObject);
        tagCommand.setName(tagName);
        tagCommand.setAnnotated(true);
        try {
            Ref tagCommandCall = tagCommand.call();
            logger.info("Created tag " + tagName + " on commit " + commitRevObject.getId());
        } catch (RefAlreadyExistsException e) {
            throw new VCSInvalidTagNameException("The tag " + tagName + " already exists", e);

        } catch (InvalidTagNameException e) {
            throw new VCSInvalidTagNameException(e);
        } catch (ConcurrentRefUpdateException e) {
            throw new VCSRemouteConnectionException("Can't create tag in local repo", e);
        } catch (NoHeadException e) {
            throw new VCSFatalRepositoryException("Can't create tag in local repo", e);
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }

        try {
            git.push().setPushTags().call();
            logger.info("Push for tag \""+tagName+"\" SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Can't push tag", e);
        } catch (TransportException e) {
            throw new VCSRemouteConnectionException("Can't push tag", e);
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }
    }

    @Override
    public void deleteTags(String namePrefix, int numberTagsToLeft) throws VCSTagNotFoundException, UnknownGitException, VCSRemouteConnectionException, VCSFatalRepositoryException {
        if (numberTagsToLeft > 0) {
            //  fetchWithTags();
            ArrayList<RevTag> allAnnotatedTags = getAllAnnotatedTags();

            Map<String, List<RevTag>> tagsByNamePrefix = tagsByNamePrefix(allAnnotatedTags);
            List<RevTag> tagsToDelete = tagsByNamePrefix.get(namePrefix);
            if (tagsToDelete == null || tagsToDelete.size() == 0) {
                throw new VCSTagNotFoundException("Tags with prefix \"" + namePrefix + "\" not found");
            }
            tagsToDelete.sort(new TagsByDateComparator<RevTag>());
            //TODO-EVG logger
            tagsToDelete.stream().forEach(tempTag -> System.out.println(tempTag.getName() + ":" + tempTag.getTaggerIdent().getWhen()));
            int size = tagsToDelete.size();

            List<RevTag> revTagsToDelete;
            if (size > numberTagsToLeft) {
                revTagsToDelete = tagsToDelete.subList(0, size - numberTagsToLeft);
                String[] tagsToDeleteName = revTagsToDelete.stream().map(tempTag -> tempTag.getTagName()).toArray(String[]::new);
                deleteTags(tagsToDeleteName);
                revTagsToDelete.stream().forEach(tempTag -> System.out.println(tempTag.getName() + ":" + tempTag.getTaggerIdent().getWhen()));
            }
        }
    }

    private void setRepository() throws VCSFatalRepositoryException {
        File gitDir = new File(gitPathName + File.separator + ".git");
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        try {
            repo = builder.build();
        } catch (IOException e) {
            throw new VCSFatalRepositoryException("Can't create local repo", e);
        }
    }

    private void fetchWithTags() throws VCSFatalRepositoryException, VCSRemouteConnectionException, UnknownGitException {
        try {
            FetchCommand fetch = git.fetch();
            //fetch.setProgressMonitor(progressMonitor);
            fetch.setTagOpt(TagOpt.FETCH_TAGS).call();
            logger.info("Fetch SUCCESS");
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Wrong remote repo", e);
        } catch (TransportException e) {
            throw new VCSRemouteConnectionException("Connection error: couldn't fetch", e);
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }
    }

    private String[] getAllTags() {
        Map<String, Ref> tags = repo.getTags();
        Collection<Ref> values = tags.values();
        String[] names = values.stream().map(tempRef -> tempRef.getName()).toArray(String[]::new);
        return names;
    }

    private void deleteTags(String... tagsRefFullNames) throws UnknownGitException, VCSFatalRepositoryException, VCSRemouteConnectionException {

        try {
            git.tagDelete().setTags(tagsRefFullNames).call();
            logger.info("Delete local tags "+tagsRefFullNames+" SUCCESS");
        } catch (RefNotFoundException e) {
            logger.warn("One of tags ("+tagsRefFullNames+") not found");
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }

        for (String tag:tagsRefFullNames){
            tagsToRemoteRemove.offer(tag);
        }
        removeTags();

    }

    private void removeTags() throws VCSFatalRepositoryException, UnknownGitException {
        String tag=tagsToRemoteRemove.poll();
        try {
            removeTagsFromRemoteGit(tag);
        } catch (VCSRemouteConnectionException e) {
            tagsToRemoteRemove.offer(tag);
        }
    }

    private void removeTagsFromRemoteGit(String... tagsRefFullNames) throws VCSFatalRepositoryException, VCSRemouteConnectionException, UnknownGitException {
        RefSpec[] refsForRemoteDelete = new RefSpec[tagsRefFullNames.length];
        for (int i = 0; i < tagsRefFullNames.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(":refs/tags/");
            sb.append(tagsRefFullNames[i]);
            refsForRemoteDelete[i] = new RefSpec(sb.toString());
        }
        try {
            git.push().setRefSpecs(refsForRemoteDelete).call();
        } catch (InvalidRemoteException e) {
            throw new VCSFatalRepositoryException("Wrong remote repo", e);
        } catch (TransportException e) {
            throw new VCSRemouteConnectionException("Connection error: couldn't delete", e);
        } catch (GitAPIException e) {
            throw new UnknownGitException(e);
        }
    }

    private ArrayList<RevTag> getAllAnnotatedTags() throws UnknownGitException, VCSTagNotFoundException {
        Map<String, Ref> tags = repo.getTags();
        Collection<Ref> values = tags.values();
        RevWalk revWalk = new RevWalk(repo);
        ArrayList<RevTag> revTags = new ArrayList<>();

        for (Ref tag : values) {
            ObjectId objectId = tag.getObjectId();
            int objectType = 0;
            try {
                objectType = revWalk.parseAny(objectId).getType();
                if (objectType == 4) {
                    RevTag revTag = revWalk.parseTag(objectId);
                    revTags.add(revTag);
                }
            } catch (MissingObjectException e) {
                throw new VCSTagNotFoundException(e);
            } catch (IOException e) {
                throw new UnknownGitException(e);
            }
        }
        revWalk.close();
        return revTags;
    }


    private Map<String, List<RevTag>> tagsByNamePrefix(Collection<RevTag> tags) {
        Map<String, List<RevTag>> collect = tags.stream()
                .filter(temTag -> temTag.getTagName().contains("#"))
                .collect(Collectors.groupingBy(tempTag ->
                                tempTag.getTagName().substring(0, tempTag.getTagName().lastIndexOf("#")))
                );
        return collect;
    }

}
