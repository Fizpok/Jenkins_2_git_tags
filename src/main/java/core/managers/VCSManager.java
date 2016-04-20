package core.managers;

import exceptions.*;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public interface VCSManager {
    void createTag(String tagName, String commitRevision) throws VCSCommitNotFoundException, UnknownGitException, VCSInvalidTagNameException, VCSFatalRepositoryException, VCSRemouteConnectionException;

    void deleteTags(String namePrefix, int numberTagsToLeft) throws VCSTagNotFoundException, UnknownGitException, VCSRemouteConnectionException, VCSFatalRepositoryException;
}
