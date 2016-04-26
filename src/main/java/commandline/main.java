package commandline;

import core.managers.GitManager;
import core.managers.VCSManager;
import exceptions.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.OptionHandler;

import java.util.Comparator;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    public static void main(String[] argsStr) {
        //The ASCII art created by http://patorjk.com/software/taag/
        System.out.println("     _      U _____ u   _   _        _  __                    _   _       ____     \n" +
                "  U |\"| u   \\| ___\"|/  | \\ |\"|      |\"|/ /         ___       | \\ |\"|     / __\"| u  \n" +
                " _ \\| |/     |  _|\"   <|  \\| |>     | ' /         |_\"_|     <|  \\| |>   <\\___ \\/   \n" +
                "| |_| |_,-.  | |___   U| |\\  |u   U/| . \\\\u        | |      U| |\\  |u    u___) |   \n" +
                " \\___/-(_/   |_____|   |_| \\_|      |_|\\_\\       U/| |\\u     |_| \\_|     |____/>>  \n" +
                "  _//        <<   >>   ||   \\\\,-. ,-,>> \\\\,-. .-,_|___|_,-.  ||   \\\\,-.   )(  (__) \n" +
                " (__)       (__) (__)  (_\")  (_/   \\.)   (_/   \\_)-' '-(_/   (_\")  (_/   (__)      \n" +
                "                                      ____                                         \n" +
                "                                     |___\"\\                                        \n" +
                "                                     U __) |                                       \n" +
                "                                     \\/ __/ \\                                      \n" +
                "                                     |_____|u                                      \n" +
                "                                     <<  //                                        \n" +
                "                                    (__)(__)                                       \n" +
                " __     __      ____    ____           _____       _         ____     ____         \n" +
                " \\ \\   /\"/u  U /\"___|  / __\"| u       |_ \" _|  U  /\"\\  u  U /\"___|u  / __\"| u      \n" +
                "  \\ \\ / //   \\| | u   <\\___ \\/          | |     \\/ _ \\/   \\| |  _ / <\\___ \\/       \n" +
                "  /\\ V /_,-.  | |/__   u___) |         /| |\\    / ___ \\    | |_| |   u___) |       \n" +
                " U  \\_/-(_/    \\____|  |____/>>       u |_|U   /_/   \\_\\    \\____|   |____/>>      \n" +
                "   //         _// \\\\    )(  (__)      _// \\\\_   \\\\    >>    _)(|_     )(  (__)     \n" +
                "  (__)       (__)(__)  (__)          (__) (__) (__)  (__)  (__)__)   (__)          ");
        int exitcode = 0;
        String errorMessage = "";
        Args args = new Args();
        Comparator<OptionHandler> sorter = (o1, o2) -> o1.option.usage().compareTo(o2.option.usage());
        ParserProperties parser2Properties = ParserProperties.defaults().withShowDefaults(false).withOptionSorter(sorter);
        CmdLineParser parser = new CmdLineParser(args,parser2Properties);

        parser.printSingleLineUsage(System.out);
        System.out.println();
        System.out.println();
        parser.printUsage(System.out);
        System.out.println();
        System.out.println();

        try {
            parser.parseArgument(argsStr);
        } catch (CmdLineException e) {
            exitcode = 20;
            errorMessage = e.getMessage();
            printHelp(parser,errorMessage);
        }
        if (exitcode == 0) {
            VCSManager gitManager = null;
            try {
                gitManager = GitManager.getInstance();
            } catch (VCSRemoteConnectionException e) {
                exitcode = -30;
                errorMessage = e.getMessage();
            } catch (VcsUnknownException e) {
                exitcode = 10;
                errorMessage = e.getMessage();
            } catch (VCSWrongRemoteRepoException e) {
                exitcode = -20;
                errorMessage = e.getMessage();
            } catch (VCSWrongLocalRepoException e) {
                e.printStackTrace();
            }
            if (exitcode == 0) {
                if (gitManager.isValidRefName(args.getNamePrefix())) {
                    //create commit
                    if (args.getBuildNumber() != 0 && args.getCommitRev() != null) {
                        if (args.getBuildNumber() > 0) {
                            if (gitManager.isValidCommitRev(args.getCommitRev())) {
                                String tagName = args.getNamePrefix() + "#" + args.getBuildNumber();
                                try {
                                    gitManager.createTag(tagName, args.getCommitRev());
                                } catch (VCSCommitNotFoundException e) {
                                    exitcode = -60;
                                    errorMessage = e.getMessage();
                                } catch (VCSInvalidTagNameException e) {
                                    exitcode = -70;
                                    errorMessage = e.getMessage();
                                } catch (VcsCommonFatalRepositoryException e) {
                                    exitcode = -80;
                                    errorMessage = e.getMessage();
                                } catch (VCSRemoteConnectionException e) {
                                    exitcode = -90;
                                    errorMessage = e.getMessage();
                                } catch (VcsUnknownException e) {
                                    exitcode = 10;
                                    errorMessage = e.getMessage();
                                }
                            } else {
                                exitcode = -150;
                                errorMessage = "Invalid commit revision number: " + args.getCommitRev();
                            }
                        } else {
                            exitcode = -160;
                            errorMessage = "Invalid build namber: " + args.getBuildNumber();
                        }
                    } else {
                        exitcode = -170;
                        errorMessage = "You must specify build number and commit revision number";

                    }
                } else {
                    exitcode = -50;
                    errorMessage = "Invalid name prefix " + args.getNamePrefix();
                    printError(exitcode, errorMessage);
                }
                if (exitcode == 0) {
                    if (args.getTagsToKeep() > -1) {//delete unnecessary tags
                        try {
                            gitManager.deleteTags(args.getNamePrefix(), args.getTagsToKeep());
                        } catch (VCSTagNotFoundException e) {
                            exitcode = -110;
                            errorMessage = e.getMessage();
                        } catch (VCSRemoteConnectionException e) {
                            exitcode = -120;
                            errorMessage = e.getMessage();
                        } catch (VcsCommonFatalRepositoryException e) {
                            exitcode = -130;
                            errorMessage = e.getMessage();
                        } catch (VcsUnknownException e) {
                            exitcode = 10;
                            errorMessage = e.getMessage();
                        }
                    }
                }
            }
        }
        if (exitcode != 0) {
            System.err.println(errorMessage);
            System.exit(exitcode);
        }
    }

    private static void printHelp(CmdLineParser parser, String errorMessage) {
        System.err.println("errorMessage");
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
        System.out.print("Error code=" + statusCode + "\t" + message);
    }

}
