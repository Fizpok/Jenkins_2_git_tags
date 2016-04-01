package commandline;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {
    static String gitPathName = "C:\\Git\\Jenkins\\mqm_for_jenkins_git";
    //static String gitPathName = "C:\\Git\\PNP_QA";
    static String commitId = "8d6c5e73b47e69539a06ccf17bba4b168685cd22";

    public static void main(String[] args) throws GitAPIException {
        String tagPrefixFull = "Full: build #";
        String tagPrefixQuick = "Quick: build #";
        String tagPrefixNightly = "Nightly: build #";

        String buildNumber = "12345";

        ttt();

    }


    public static void ttt() throws GitAPIException {
        Git git = getGit();
        ObjectId objectId = ObjectId.fromString(commitId);

        Iterable<RevCommit> commits = git.log().call();
        final RevObject[] id = new RevObject[1];
        commits.forEach(tempCommit -> {
            if (tempCommit.getId().compareTo(objectId) == 0) {
                System.out.println("tempCommit = " + tempCommit);
                id[0] = tempCommit;
                int rere=344;
            }

        });


        git.tag().setObjectId(id[0]);
    }

    public static void tst() {
        Repository repo = getRepository();
        Map<String, Ref> allRefs = repo.getAllRefs();
        Map<String, Ref> tags = repo.getTags();
    }

    private static Git getGit() {
        return new Git(getRepository());
    }

    private static Repository getRepository() {
        File gitDir = new File(gitPathName + "\\.git");
        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment()// scan environment GIT_* variables
                .findGitDir();// scan up the file system tree
        Repository repo = null;
        try {
            repo = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repo;
    }

    public static void porcelanApiTst() {
        Repository repo = getRepository();

        Git git = new Git(repo);
        ListTagCommand listTagCommand = git.tagList();
        List<Ref> refTagsList;
        try {
            Iterable<RevCommit> revCommits = git.log().call();
            refTagsList = listTagCommand.call();
            int dfdfd = 343;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        int rer = 4343;
    }
}
