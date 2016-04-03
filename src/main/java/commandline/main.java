package commandline;

import core.Core;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.transport.PushResult;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {
    //static String gitPathName = "C:\\Git\\PNP_QA";
    static String commitId = "8d6c5e73b47e69539a06ccf17bba4b168685cd22";
    static String tagPrefixFull = "Full_build";
    static String tagPrefixQuick = "Quick_build";
    static String tagPrefixNightly = "Nightly_build";

    static String buildNumber = "12345";


    public static void main(String[] args) throws GitAPIException, IOException {
        Git git = Core.getGit();
        Core.fetchWithTags(git);
        ttt(git);
        Collection<RevTag> allTagsWithDate = Core.getAllTagsWithDate(git);
        String[] allTags = Core.getAllTags(git);
   //     Core.deleteTags(git, allTags);
    }


    public static void ttt(Git git) throws GitAPIException {

        ObjectId objectId = ObjectId.fromString(commitId);

        Iterable<RevCommit> commits = git.log().call();
        RevObject revObject = null;
        for (RevCommit commit : commits) {
            if (commit.getId().compareTo(objectId) == 0) {
                System.out.println("tempCommit = " + commit);
                revObject = commit;
                break;
            }
        }
        TagCommand tagCommand = git.tag().setObjectId(revObject);
        tagCommand.setName("test_tag_" + buildNumber + "_" + tagPrefixFull + "_" + time());
        tagCommand.setAnnotated(true);
        Ref tagCommandCall = tagCommand.call();

        Iterable<PushResult> call = git.push().setPushTags().call();


        int wewew = 232;
    }

    private static String time() {
        Date date = new Date();
//        DateFormat df = new SimpleDateFormat("yyyyMMdd");
//
//        String s = date.toString();
//        return s;

        return String.valueOf(date.getTime());
    }


}
