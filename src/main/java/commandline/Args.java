package commandline;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Args implements Serializable {

    @Option(name = "-name_prefix", aliases = {"-np"}, usage = "1. Set tag name prefix (Like Quick_Red)", required = true, metaVar = "String")
    private String namePrefix = null;

    @Option(name = "-build_number", aliases = {"-bn"}, usage = "2.1. Build number from Jenkins", required = false, metaVar = "Integer", depends = {"-commit_revision"})
    private Integer buildNumber = null;

    @Option(name = "-commit_revision", aliases = {"-cr"}, usage = "2.2. Commit revision for tag (SHA-1)", required = false, metaVar = "String", depends = {"-build_number"})
    private String commitRev = null;

    @Option(name = "-keep", aliases = {"-k"}, usage = "3. Set tags count with this name prefix to keep", required = false, metaVar = "Integer")
    private int tagsToKeep = -1;

    @Option(name = "-repo_path", aliases = {"-rp"}, usage = "4. Set local repository path (with or without .git)", required = false, metaVar = "String")
    private File repoPath;

    public String getNamePrefix() {
        return namePrefix;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public String getCommitRev() {
        return commitRev;
    }

    public int getTagsToKeep() {
        return tagsToKeep;
    }

    public File getRepoPath() {
        return repoPath;
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
