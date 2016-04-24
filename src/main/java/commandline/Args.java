package commandline;

import org.kohsuke.args4j.Option;

import java.io.Serializable;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Args implements Serializable{

    @Option(name="-np",aliases = {"--name_prefix"},usage="Set tag name prefix (Like Quick_Red)", required = true)
    private String namePrefix=null;

    @Option(name="-bn",aliases = {"--build_number"},usage="Set build number", required = false)
    private int buildNumber=0;

    @Option(name="-cr",aliases = {"--commit_revision"},usage="Set commit revision (SHA-1)", required = false)
    private String commitRev=null;

    @Option(name="-k",aliases = {"--keep"},usage="Set tags count with this name prefix to keep", required = false)
    private int tagsToKeep =-1;

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
