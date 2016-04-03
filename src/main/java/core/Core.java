package core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Core {
    static String gitPathName = "C:\\Git\\Jenkins\\mqm_for_jenkins_git";

    public static Git getGit() throws IOException {
        Git git = new Git(getRepository());
        return git;
    }

    public static Repository getRepository() throws IOException {
        File gitDir = new File(gitPathName + "\\.git");
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        Repository repo = null;
            repo = builder.build();
        return repo;
    }

    public static void fetchWithTags(Git git) throws GitAPIException {
        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    public static String[] getAllTags(Git git){
        Map<String, Ref> tags = git.getRepository().getTags();
        Collection<Ref> values = tags.values();
        String[] names = values.stream().map(tempRef -> tempRef.getName()).collect(Collectors.toList()).toArray(new String[values.size()]);
        return names;
    }

    public static Collection<RevTag> getAllTagsWithDate(Git git) throws IOException {
        Repository repository = git.getRepository();
        Map<String, Ref> tags = repository.getTags();
        Collection<Ref> values = tags.values();
        RevWalk revWalk = new RevWalk(repository);
        Set<RevTag> revTags = new TreeSet<>(new DateComparator<RevTag>());
        for (Ref tag : values) {
            ObjectId objectId = tag.getObjectId();
            int objectType = revWalk.parseAny(objectId).getType();
            if (objectType == 4) {
                RevTag revTag = revWalk.parseTag(objectId);
                revTags.add(revTag);
            }
        }
        return revTags;
    }

    public static void deleteTags(Git git, String... tagsRefFullName) throws GitAPIException {
        git.tagDelete().setTags(tagsRefFullName).call();

        RefSpec[] refsForRemoteDelete = new RefSpec[tagsRefFullName.length];
        for (int i = 0; i < tagsRefFullName.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(":");
            sb.append(tagsRefFullName[i]);
            refsForRemoteDelete[i] = new RefSpec(sb.toString());
        }
        Iterable<PushResult> call = git.push().setRefSpecs(refsForRemoteDelete).call();
    }
}
