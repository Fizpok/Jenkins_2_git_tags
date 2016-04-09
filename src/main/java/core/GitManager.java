package core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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
    String gitPathName;
    Logger logger;
    private static GitManager gitManager;

    public static VCSManager getInstance() throws IOException {
        if (gitManager == null) {
            return new GitManager();
        } else {
            return gitManager;
        }
    }

    public static VCSManager getInstance(String gitPathName) throws IOException {
        if (gitManager == null) {
            return new GitManager(gitPathName);
        } else {
            return gitManager;
        }
    }

    private GitManager() throws IOException {
        this("C:\\Git\\Jenkins\\mqm_for_jenkins_git");
    }

    private GitManager(String gitPathName) throws IOException {
        this.gitPathName = gitPathName;
        repo = setRepository();
        git = new Git(repo);
        logger = LoggerFactory.getLogger(GitManager.class);
        logger.info("Constructor");
        logger.debug("Constructor");
    }

    @Override
    public void createTag(String commitRevision, String tagName) {

    }

    @Override
    public void deleteTags(String namePrefix, int numberTagsToLeft) throws IOException, GitAPIException {
        if (numberTagsToLeft > 0) {
            //  fetchWithTags();
            ArrayList<RevTag> allAnnotatedTagsByDate = getAllAnnotatedTagsByDate();

            Map<String, List<RevTag>> tagsByNamePrefix = tagsByNamePrefix(allAnnotatedTagsByDate);
            List<RevTag> totalTags = tagsByNamePrefix.get(namePrefix);
            totalTags.sort(new TagsByDateComparator<RevTag>());
            totalTags.stream().forEach(tempTag -> System.out.println(tempTag.getName() + ":" + tempTag.getTaggerIdent().getWhen()));
            System.out.println("---------");
            int size = totalTags.size();

            List<RevTag> revTagsToDelete;
            if (size > numberTagsToLeft) {
                revTagsToDelete = totalTags.subList(0, size - numberTagsToLeft);
                String[] tagsToDeleteName = revTagsToDelete.stream().map(tempTag -> tempTag.getTagName()).toArray(String[]::new);
                deleteTags(tagsToDeleteName);
                revTagsToDelete.stream().forEach(tempTag -> System.out.println(tempTag.getName() + ":" + tempTag.getTaggerIdent().getWhen()));
            }

            int rere = 434;
        }
    }

    @Override
    public boolean isValidRefName(String name) {
        return Repository.isValidRefName(name);
    }

    private Repository setRepository() throws IOException {
        File gitDir = new File(gitPathName + File.separator + ".git");
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        Repository repo = builder.build();
        return repo;
    }

    private void fetchWithTags() throws GitAPIException {
        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    private String[] getAllTags() {
        Map<String, Ref> tags = repo.getTags();
        Collection<Ref> values = tags.values();
        String[] names = values.stream().map(tempRef -> tempRef.getName()).toArray(String[]::new);
        return names;
    }

    private void deleteTags(String... tagsRefFullName) throws GitAPIException {

        //try {
        git.tagDelete().setTags(tagsRefFullName).call();
        //} catch (RefNotFoundException e) {
        //e.printStackTrace();
        //}

        RefSpec[] refsForRemoteDelete = new RefSpec[tagsRefFullName.length];
        for (int i = 0; i < tagsRefFullName.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(":");
            sb.append(tagsRefFullName[i]);
            refsForRemoteDelete[i] = new RefSpec(sb.toString());
        }
        git.push().setRefSpecs(refsForRemoteDelete).call();
    }

    private ArrayList<RevTag> getAllAnnotatedTagsByDate() throws IOException {
        Repository repository = git.getRepository();
        Map<String, Ref> tags = repository.getTags();
        Collection<Ref> values = tags.values();
        RevWalk revWalk = new RevWalk(repository);
        ArrayList<RevTag> revTags = new ArrayList<>();
        for (Ref tag : values) {
            ObjectId objectId = tag.getObjectId();
            int objectType = revWalk.parseAny(objectId).getType();
            if (objectType == 4) {
                RevTag revTag = revWalk.parseTag(objectId);
                revTags.add(revTag);
            }
        }

        //return revTags.toArray(new RevTag[revTags.size()]);
        return revTags;
    }


    private Map<String, List<RevTag>> tagsByNamePrefix(Collection<RevTag> tags) {
        Map<String, List<RevTag>> collect = tags.stream().collect(Collectors.groupingBy(tempTag -> tempTag.getTagName().substring(0, tempTag.getTagName().lastIndexOf("_"))));
        return collect;
    }

}
