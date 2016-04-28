package exceptions;

import java.io.File;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsWrongLocalRepoException extends VcsRepositoryException {
    public VcsWrongLocalRepoException(String s, File gitDir, Throwable e) {
        super(s+" "+gitDir.getAbsolutePath(),e);
    }

    public VcsWrongLocalRepoException(String s, File gitDir) {
        super(s+" "+gitDir.getAbsolutePath());
    }

}
