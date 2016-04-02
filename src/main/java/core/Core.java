package core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
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

    public static Collection<Ref> getAllTagsWithDate(Git git) throws GitAPIException, IOException {
        Map<String, Ref> tags = git.getRepository().getTags();
        Collection<Ref> values = tags.values();
        RevWalk revWalk = new RevWalk(git.getRepository());
        for (Ref tag : values) {
            ObjectId objectId = tag.getObjectId();

            //int type = revWalk.parseTag(objectId).getType();
            //Date when = revWalk.parseTag(objectId).getTaggerIdent().getWhen();
            int type = revWalk.parseAny(objectId).getType();
            //int type = revWalk.lookupTag(objectId).getType();
            if (type == 4) {
                Date when = revWalk.parseTag(objectId).getTaggerIdent().getWhen();
                int wqwq = 3232;
            }
        }
        return values;
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
