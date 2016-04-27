package exceptions;

import java.io.File;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSWrongLocalRepoException extends VcsRepositoryException {
    public VCSWrongLocalRepoException(String s, File gitDir, Throwable e) {
        super(s+" "+gitDir.getAbsolutePath(),e);
    }
}
