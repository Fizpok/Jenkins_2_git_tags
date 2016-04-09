package commandline;

import core.GitManager;
import core.VCSManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.transport.PushResult;

import java.io.IOException;
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
    private final static int defaultCountToDelete = 5;

    public static void main(String[] args) throws GitAPIException, IOException {
        boolean isCorrectArgs = false;
        int deleteCountAndFlag = 0;
        String namePrefix;
        String buildNumber;
        String commitRev;
        String tagName;
/*
        Arrays.stream(args).forEach(String::trim);
        if (args.length == 1 && (args[0].equals("/?") || args[0].equalsIgnoreCase("/help"))) {
            printHelp();
        } else {
            if (args.length < 3 || args.length > 5) {
                isCorrectArgs = false;
            } else {
                namePrefix = args[0];
                buildNumber = args[1];
                commitRev = args[2];
                if (Repository.isValidRefName(namePrefix)) {
                    if (buildNumber.isEmpty() || !Validator.isUnsignInt(buildNumber)) {
                        isCorrectArgs = false;
                    } else {
                        if (Validator.isCorrectCommitRev(commitRev)) {
                            isCorrectArgs = true;
                            tagName = new StringBuilder(namePrefix).append('_').append(buildNumber).toString();
                            if (args.length > 3) {
                                if (args[3].equals("-d")) {
                                    if (args.length == 5) {
                                        if (Validator.isUnsignInt(args[4])) {
                                            deleteCountAndFlag = Integer.parseInt(args[4]);
                                        } else {
                                            isCorrectArgs = false;
                                        }
                                    } else {
                                        deleteCountAndFlag = defaultCountToDelete;
                                    }
                                } else {
                                    isCorrectArgs = false;
                                }
                            } else { //args.length=3 must have parameters
                                deleteCountAndFlag = 0;
                            }
                        } else {
                            isCorrectArgs = false;
                        }
                    }
                }
            }
        }
        if (isCorrectArgs) {
        */
        if (true) {
            //   ttt(git);
            VCSManager gitManager = new GitManager();
            gitManager.deleteTags("tagB_test", 2);

            //     Core.deleteTags(git, allTags);}
        } else {
            printError();
        }
    }

    private static void printHelp() {
        System.out.println("j2gt tag_name_prefix build_number Commit_revision [-d [n]]");
    }

    private static void printError() {
        System.out.println("error");
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
