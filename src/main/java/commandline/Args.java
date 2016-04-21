package commandline;

import org.kohsuke.args4j.Option;

import java.io.Serializable;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Args implements Serializable{

    @Option(name="-np",usage="Set tag name prefix (Like Quick_Red)", required = true)
    private String namePrefix=null;

    @Option(name="-bn",usage="Set build number", required = false)
    private int buildNumber=0;

    @Option(name="-cr",usage="Set commit revision (SHA-1)", required = false)
    private String commitRev=null;

    @Option(name="-d",usage="Set tags count with this prefix to leave", required = false)
    private int tagsToLeft =-1;

    public String getNamePrefix() {
        return namePrefix;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getCommitRev() {
        return commitRev;
    }

    public int getTagsToLeft() {
        return tagsToLeft;
    }

    @Override

    public String toString() {
        return "Args{" +
                " namePrefix='" + namePrefix + '\'' +
                ", buildNumber=" + buildNumber +
                ", commitRev='" + commitRev + '\'' +
                ", deleteCountAndFlag=" + tagsToLeft +
                '}';
    }
}
