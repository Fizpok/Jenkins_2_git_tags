package commandline;

import core.managers.GitManager;
import core.managers.VCSManager;
import exceptions.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import utils.Validator;

import java.util.Date;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    private final static int defaultCountToDelete = 5;

    //
    //j2gt tag_name_prefix build_number Commit_revision [-d [n]] //3-5
    //j2gt tag_name_prefix -d [n]                               //2-3
    public static void main(String[] argsStr) throws VCSFatalRepositoryException, VCSCommitNotFoundException, UnknownGitException, VCSInvalidTagNameException, VCSRemouteConnectionException, VCSTagNotFoundException, CmdLineException {

        Args args = new Args();
        CmdLineParser parser = new CmdLineParser(args);

        parser.parseArgument(argsStr);

        VCSManager gitManager = GitManager.getInstance();
        if (Validator.isValidRefName(args.getNamePrefix())) {
            //create commit
            if (args.getBuildNumber() != 0 && args.getCommitRev() != null) {
                if (args.getBuildNumber() > 0 && Validator.isValidCommitRev(args.getCommitRev())) {
                    String tagName = args.getNamePrefix() + "#" + args.getBuildNumber();
                    gitManager.createTag(tagName, args.getCommitRev());
                } else {
                    printError();
                }
            }
            if (args.getTagsToLeft() > -1) {
                gitManager.deleteTags(args.getNamePrefix(), args.getTagsToLeft());
            }
        } else {
            printError();
        }


        int erer = 4;
    }

    private static void printHelp() {
        System.out.println("j2gt tag_name_prefix build_number Commit_revision [-d [n]]");
    }

    private static void printError() {
        System.out.println("error");
    }


//    public static void ttt(Git git) throws GitAPIException {
//
//        ObjectId objectId = ObjectId.fromString(commitId);
//
//        Iterable<RevCommit> commits = git.log().call();
//        RevObject revObject = null;
//        for (RevCommit commit : commits) {
//            if (commit.getId().compareTo(objectId) == 0) {
//                System.out.println("tempCommit = " + commit);
//                revObject = commit;
//                break;
//            }
//        }
//        TagCommand tagCommand = git.tag().setObjectId(revObject);
//        tagCommand.setName("test_tag_" + buildNumber + "_" + tagPrefixFull + "_" + time());
//        tagCommand.setAnnotated(true);
//        Ref tagCommandCall = tagCommand.call();
//
//        Iterable<PushResult> call = git.push().setPushTags().call();
//
//
//        int wewew = 232;
//    }

    private static String time() {
        Date date = new Date();
//        DateFormat df = new SimpleDateFormat("yyyyMMdd");
//
//        String s = date.toString();
//        return s;

        return String.valueOf(date.getTime());
    }


}
