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
import utils.Validator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    private final static int defaultCountToDelete = 5;
    //
    //j2gt tag_name_prefix build_number Commit_revision [-d [n]] //3-5
    //j2gt tag_name_prefix -d [n]                               //2-3
    public static void main(String[] args) throws GitAPIException, IOException {
        boolean isCorrectArgs = false;
        int deleteCountAndFlag = 0;
        String namePrefix;
        String buildNumber;
        String commitRev;
        String tagName;

        Arrays.stream(args).forEach(String::trim);
        if (args.length == 1 && (args[0].equals("/?") || args[0].equalsIgnoreCase("/help"))) {
            printHelp();
        } else {
            if (args.length < 3 || args.length > 4) {
                isCorrectArgs = false;
            } else {
                namePrefix = args[0];
                buildNumber = args[1];
                commitRev = args[2];
                if (Validator.isValidRefName(namePrefix)) {
                    if (buildNumber.isEmpty() || !Validator.isUnsignInt(buildNumber)) {
                        isCorrectArgs = false;
                    } else {
                        if (Validator.isCorrectCommitRev(commitRev)) {
                            isCorrectArgs = true;
                            tagName = new StringBuilder(namePrefix).append('_').append(buildNumber).toString();
                            if (args.length == 4) {
                                String deleteParameter = args[3];
                                if (deleteParameter.matches("^-d\\d*$")) {
                                    if (deleteParameter.equals("-d")) {
                                        deleteCountAndFlag = defaultCountToDelete;
                                    }
                                    else {//-d1234
                                        deleteCountAndFlag = Integer.parseInt(deleteParameter.substring(2));
                                    }

                                }
                                else {
                                    isCorrectArgs = false;
                                }



                                else if(){

                                    }
                                    if (args.length == 5) {
                                        if (Validator.isUnsignInt(args[4])) {
                                            deleteCountAndFlag = Integer.parseInt(args[4]);
                                        } else {
                                            isCorrectArgs = false;
                                        }
                                    } else {
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

        //if (true) {
            //   ttt(git);
            VCSManager gitManager = GitManager.getInstance();
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
