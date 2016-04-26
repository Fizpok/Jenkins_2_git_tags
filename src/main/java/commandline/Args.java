package commandline;

import org.kohsuke.args4j.Option;

import java.io.Serializable;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Args implements Serializable {

    @Option(name = "-name_prefix", aliases = {"-np"}, usage = "1. Set tag name prefix (Like Quick_Red)", required = true, metaVar = "String")
    private String namePrefix = null;

    @Option(name = "-build_number", aliases = {"-bn"}, usage = "2.1. Build number from Jenkins", required = false, metaVar = "Integer", depends = {"-commit_revision"})
    private int buildNumber=0;

    @Option(name = "-commit_revision", aliases = {"-cr"}, usage = "2.2. Commit revision for tag (SHA-1)", required = false, metaVar = "String", depends = {"-build_number"})
    private String commitRev = null;

    @Option(name = "-keep", aliases = {"-k"}, usage = "3. Set tags count with this name prefix to keep", required = false, metaVar = "Integer")
    private int tagsToKeep=-1;

    public String getNamePrefix() {
        return namePrefix;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getCommitRev() {
        return commitRev;
    }

    public int getTagsToKeep() {
        return tagsToKeep;
    }

    @Override
    public String toString() {
        return "Args{" +
                " namePrefix='" + namePrefix + '\'' +
                ", buildNumber=" + buildNumber +
                ", commitRev='" + commitRev + '\'' +
                ", keep=" + tagsToKeep +
                '}';
    }
}
