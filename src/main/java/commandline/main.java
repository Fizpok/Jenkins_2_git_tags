package commandline;

import core.managers.GitManager;
import core.managers.VCSManager;
import exceptions.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    private static final int UNKNOWN_ERROR = 10;
    private static final int WRONG_CLI_ARG = 20;
    private static final int REPO_ERROR = 30;
    private static final int REPO_CONNECTION_ERROR = 40;
    private static final int TAG_NOT_FOUND_ERROR = 50;
    private static final int INVALID_TAG_NAME_ERROR = 55;
    private static final int COMMIT_NOT_FOUND_ERROR = 60;

    public static void main(String[] argsStrArr) {
        //The ASCII art created by http://patorjk.com/software/taag/
        System.out.println("  _______               \\                           \n" +
                " '   /      ___  , __   |   , ` , __     ____       \n" +
                "     |    .'   ` |'  `. |  /  | |'  `.  (           \n" +
                "     |    |----' |    | |-<   | |    |  `--.        \n" +
                "  `--/    `.___, /    | /  \\_ / /    | \\___.'       \n" +
                "                                                    \n" +
                "                 ___                                \n" +
                "                /   \\                               \n" +
                "                  _-'                               \n" +
                "                 /                                  \n" +
                "                /___,                               \n" +
                "                                                    \n" +
                " __    __   ___    _____       _______              \n" +
                " |     |  .'   \\  (           '   /      ___    ___.\n" +
                "  \\    /  |        `--.           |     /   ` .'   `\n" +
                "   \\  /   |           |           |    |    | |    |\n" +
                "    \\/     `.__, \\___.'           /    `.__/|  `---|\n" +
                "                                               \\___/\n");
        int exitcode = 0;
        String errorMessage = "";
        Args args = new Args();

        ParserProperties parserProperties = ParserProperties.defaults()
                .withShowDefaults(false)
                .withOptionSorter((o1, o2) -> o1.option.usage().compareTo(o2.option.usage()))
                .withUsageWidth(80);
        CmdLineParser cmdLineParser = new CmdLineParser(args, parserProperties);

        try {
            cmdLineParser.parseArgument(argsStrArr);
        } catch (CmdLineException e) {
            exitcode = WRONG_CLI_ARG;
            errorMessage = e.getMessage();
        }
        if (exitcode == 0) {
            String repoPath;
            if (args.getRepoPath() == null) {
                repoPath = System.getProperty("user.dir");
            } else {
                repoPath = args.getRepoPath();
            }
            VCSManager gitManager = null;
            try {
                gitManager = GitManager.getInstance(repoPath);
            } catch (VcsRemoteConnectionException e) {
                exitcode = REPO_CONNECTION_ERROR;
                errorMessage = e.getMessage();
            } catch (VcsUnknownException e) {
                exitcode = UNKNOWN_ERROR;
                errorMessage = e.getMessage();
            } catch (VcsRepositoryException e) {
                exitcode = REPO_ERROR;
                errorMessage = e.getMessage();
            }
            if (exitcode == 0) {
                if (gitManager.isValidRefName(args.getNamePrefix())) {
                    //create commit
                    if (args.getBuildNumber() > 0) {
                        if (gitManager.isValidCommitRev(args.getCommitRev())) {
                            String tagName = args.getNamePrefix() + "#" + args.getBuildNumber();
                            try {
                                gitManager.createTag(tagName, args.getCommitRev());
                            } catch (VcsCommitNotFoundException e) {
                                exitcode = COMMIT_NOT_FOUND_ERROR;
                                errorMessage = e.getMessage();
                            } catch (VcsInvalidTagNameException e) {
                                exitcode = INVALID_TAG_NAME_ERROR;
                                errorMessage = e.getMessage();
                            } catch (VcsRepositoryException e) {
                                exitcode = REPO_ERROR;
                                errorMessage = e.getMessage();
                            } catch (VcsRemoteConnectionException e) {
                                exitcode = REPO_CONNECTION_ERROR;
                                errorMessage = e.getMessage();
                            } catch (VcsUnknownException e) {
                                exitcode = UNKNOWN_ERROR;
                                errorMessage = e.getMessage();
                            }
                        } else {
                            exitcode = WRONG_CLI_ARG;
                            errorMessage = "Illegal commit revision id: " + args.getCommitRev() + "\n It must be commit revision id (SHA-1)";
                        }
                    } else {
                        exitcode = WRONG_CLI_ARG;
                        errorMessage = "Illegal build number: " + args.getBuildNumber();
                    }
                } else {
                    exitcode = WRONG_CLI_ARG;
                    errorMessage = "Invalid name prefix " + args.getNamePrefix();
                }
                if (exitcode == 0) {
                    if (args.getTagsToKeep() > -1) {//delete unnecessary tags
                        try {
                            gitManager.deleteTags(args.getNamePrefix(), args.getTagsToKeep());
                        } catch (VcsTagNotFoundException e) {
                            exitcode = TAG_NOT_FOUND_ERROR;
                            errorMessage = e.getMessage();
                        } catch (VcsRemoteConnectionException e) {
                            exitcode = REPO_CONNECTION_ERROR;
                            errorMessage = e.getMessage();
                        } catch (VcsRepositoryException e) {
                            exitcode = REPO_ERROR;
                            errorMessage = e.getMessage();
                        } catch (VcsUnknownException e) {
                            exitcode = UNKNOWN_ERROR;
                            errorMessage = e.getMessage();
                        }
                    }
                }
            }
        }
        if (exitcode != 0) {
            if (exitcode == WRONG_CLI_ARG) {
                printHelp(cmdLineParser, errorMessage);
            } else {
                printError(exitcode, errorMessage);
            }
            System.exit(exitcode);
        }
    }

    private static void printHelp(CmdLineParser parser, String errorMessage) {
        System.err.println("Error: " + errorMessage + "\n\n");
        parser.printSingleLineUsage(System.out);
        System.out.println();
        parser.printUsage(System.out);


//        StringBuilder sb = new StringBuilder();
//        sb.append(errorMessage);
//
//        sb.append("\n\nRun this command with these parameters: \n");
//        sb.append("\t j2gt -name_prefix={Tag name prefix} [-build_number={Jenkins build number} -commit_revision={Tagged commit revision}] [-keep={number of tags with this name prefix to keep}]\n");
//        sb.append("For example\n");
//        sb.append("\t j2gt -name_prefix=Full_Green [-build_number=12345 -commit_revision=ce4136dc4af2f8f589daaeab13294c9078ef596a] [-keep=5]\n");
//        sb.append("or use short keys\n\t-name_prefix = -np\n\t-build_number = -bn\n\t-commit_revision = -cr\n\t-keep = -k");
//        System.out.println(sb.toString());
    }

    private static void printError(int statusCode, String message) {
        System.err.println("Error code = " + statusCode + "\t" + message);
    }
//Moove into repo directory or spec parameter
}
