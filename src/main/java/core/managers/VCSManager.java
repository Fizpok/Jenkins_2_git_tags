package core.managers;

import exceptions.*;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public interface VCSManager {
    void createTag(String tagName, String commitRevision) throws _VcsCommitNotFoundException, VcsUnknownException, _VcsInvalidTagNameException, _VcsRepositoryException, _VcsRemoteConnectionException;

    void deleteTags(String namePrefix, int numberTagsToLeft) throws _VcsTagNotFoundException, VcsUnknownException, _VcsRemoteConnectionException, _VcsWrongRemoteRepoException;


    boolean isValidCommitRev(String commitRev);

    boolean isValidRefName(String namePrefix);
}
