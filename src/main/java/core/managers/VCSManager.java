package core.managers;

import exceptions.*;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public interface VCSManager {
    void createTag(String tagName, String commitRevision) throws VCSCommitNotFoundException, VcsUnknownException, VCSInvalidTagNameException, VCSFatalRepositoryException, VCSRemoteConnectionException;

    void deleteTags(String namePrefix, int numberTagsToLeft) throws VCSTagNotFoundException, VcsUnknownException, VCSRemoteConnectionException, VCSFatalRepositoryException;


    boolean isValidCommitRev(String commitRev);

    boolean isValidRefName(String namePrefix);
}
