package commandline;

import core.managers.GitManager;
import core.managers.VCSManager;
import exceptions.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import utils.Validator;

/**
 * Created by Evgeney Fiskin on 31-03-2016.
 */
public class main {

    private final static int defaultCountToDelete = 5;

    //
    //j2gt tag_name_prefix build_number Commit_revision [-d [n]] //3-5
    //j2gt tag_name_prefix -d [n]                               //2-3
    public static int main(String[] argsStr) throws VCSFatalRepositoryException, UnknownGitException, VCSRemouteConnectionException, VCSInvalidTagNameException, VCSCommitNotFoundException, VCSTagNotFoundException {
        int statuscode = 0;
        String errorMessage;
        Args args = new Args();
        CmdLineParser parser = new CmdLineParser(args);

        try {
            parser.parseArgument(argsStr);
        } catch (CmdLineException e) {
            statuscode = 10;
            errorMessage = e.getMessage();
        }

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
        return statuscode;
    }

    private static void printHelp() {
        System.out.println("j2gt tag_name_prefix build_number Commit_revision [-d [n]]");
    }

    private static void printError() {
        System.out.println("error");
    }

}
