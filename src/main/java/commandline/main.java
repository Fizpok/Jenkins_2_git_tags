package commandline;

import core.managers.GitManager;
import core.managers.VCSManager;
import exceptions.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    private final static int defaultCountToDelete = 5;

    //
    //j2gt tag_name_prefix build_number Commit_revision [-d [n]] //3-5
    //j2gt tag_name_prefix -d [n]                               //2-3
    public static void main(String[] argsStr) {
        int statuscode = 0;
        String errorMessage = "";
        Args args = new Args();
        CmdLineParser parser = new CmdLineParser(args);

        try {
            parser.parseArgument(argsStr);
        } catch (CmdLineException e) {
            statuscode = 10;
            errorMessage = e.getMessage();
        }
        if (statuscode == 0) {
            VCSManager gitManager = null;
            try {
                gitManager = GitManager.getInstance();
            } catch (VCSFatalRepositoryException e) {
                statuscode = 20;
                errorMessage = e.getMessage();
            } catch (VCSRemoteConnectionException e) {
                statuscode = 30;
                errorMessage = e.getMessage();
            } catch (VcsUnknownException e) {
                statuscode = 40;
                errorMessage = e.getMessage();
            }
            if (statuscode == 0) {
                if (gitManager.isValidRefName(args.getNamePrefix())) {
                    //create commit
                    if (args.getBuildNumber() != 0 && args.getCommitRev() != null) {
                        if (args.getBuildNumber() > 0) {
                            if (gitManager.isValidCommitRev(args.getCommitRev())) {
                                String tagName = args.getNamePrefix() + "#" + args.getBuildNumber();
                                try {
                                    gitManager.createTag(tagName, args.getCommitRev());
                                } catch (VCSCommitNotFoundException e) {
                                    statuscode = 60;
                                    errorMessage = e.getMessage();
                                } catch (VCSInvalidTagNameException e) {
                                    statuscode = 70;
                                    errorMessage = e.getMessage();
                                } catch (VCSFatalRepositoryException e) {
                                    statuscode = 80;
                                    errorMessage = e.getMessage();
                                } catch (VCSRemoteConnectionException e) {
                                    statuscode = 90;
                                    errorMessage = e.getMessage();
                                } catch (VcsUnknownException e) {
                                    statuscode = 100;
                                    errorMessage = e.getMessage();
                                }
                            } else {
                                statuscode = 150;
                                errorMessage = "Invalid commit revision number: " + args.getCommitRev();
                            }
                        } else {
                            statuscode = 160;
                            errorMessage = "Invalid build namber: " + args.getBuildNumber();
                        }
                    } else {
                        statuscode = 170;
                        errorMessage = "You must specify build number and commit revision number";

                    }
                } else {
                    statuscode = 50;
                    errorMessage = "Invalid name prefix " + args.getNamePrefix();
                    printError(statuscode, errorMessage);
                }
                if (statuscode == 0) {
                    if (args.getTagsToKeep() > -1) {
                        try {
                            gitManager.deleteTags(args.getNamePrefix(), args.getTagsToKeep());
                        } catch (VCSTagNotFoundException e) {
                            statuscode = 110;
                            errorMessage = e.getMessage();
                        } catch (VCSRemoteConnectionException e) {
                            statuscode = 120;
                            errorMessage = e.getMessage();
                        } catch (VCSFatalRepositoryException e) {
                            statuscode = 130;
                            errorMessage = e.getMessage();
                        } catch (VcsUnknownException e) {
                            statuscode = 140;
                            errorMessage = e.getMessage();
                        }
                    }
                }
            }
        }
        if (statuscode != 0) {
            printError(statuscode,errorMessage);
        }
       // return statuscode;
    }

    private static void printHelp() {
        System.out.println("j2gt tag_name_prefix build_number Commit_revision [-d [n]]");
    }

    private static void printError(int statuscode, String message) {
        System.out.print("Error code="+statuscode+"\t"+message);
    }

}
