package core.managers;

import exceptions.*;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public interface VcsManager {
    void createTag(String tagName, String commitRevision) throws VcsCommitNotFoundException, VcsUnknownException, VcsInvalidTagNameException, VcsRepositoryException, VcsRemoteConnectionException;

    void deleteTags(String namePrefix, int numberTagsToLeft) throws VcsTagNotFoundException, VcsUnknownException, VcsRemoteConnectionException, VcsWrongRemoteRepoException;


    boolean isValidCommitRev(String commitRev);

    boolean isValidTagName(String namePrefix);
}
