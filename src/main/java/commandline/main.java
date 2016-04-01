package commandline;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.TagOpt;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {
    static String gitPathName = "C:\\Git\\Jenkins\\mqm_for_jenkins_git";
    //static String gitPathName = "C:\\Git\\PNP_QA";
    static String commitId = "8d6c5e73b47e69539a06ccf17bba4b168685cd22";
    static String tagPrefixFull = "Full_build";
    static String tagPrefixQuick = "Quick_build";
    static String tagPrefixNightly = "Nightly_build";

    static String buildNumber = "12345";


    public static void main(String[] args) throws GitAPIException {
        Git git = getGit();
        fetchWithTags(git);
        ttt(git);
        getAllTags(git);
    }


    public static void ttt(Git git) throws GitAPIException {

        ObjectId objectId = ObjectId.fromString(commitId);

        Iterable<RevCommit> commits = git.log().call();
        RevObject id = null;
        for (RevCommit commit : commits) {
            if (commit.getId().compareTo(objectId) == 0) {
                System.out.println("tempCommit = " + commit);
                id = commit;
                break;
            }
        }
        TagCommand tagCommand = git.tag().setObjectId(id);
        tagCommand.setName("test_tag_" + buildNumber + "_" + tagPrefixFull + "_" + time());
        tagCommand.setAnnotated(false);
        tagCommand.call();

        Iterable<PushResult> call = git.push().setPushTags().call();


        int wewew=232;
    }

    public static void tst() {
        Repository repo = getRepository();
        Map<String, Ref> allRefs = repo.getAllRefs();
        Map<String, Ref> tags = repo.getTags();
    }

    private static Git getGit() {
        Git git = new Git(getRepository());

        return git;
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
    private static String time(){
        Date date = new Date();
//        DateFormat df = new SimpleDateFormat("yyyyMMdd");
//
//        String s = date.toString();
//        return s;

        return String.valueOf(date.getTime());
    }



    public static void fetchWithTags(Git git) throws GitAPIException {
        git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    public static void getAllTags(Git git) throws GitAPIException {
        Map<String, Ref> tags = git.getRepository().getTags();
        int rere=54;
    }
}
