package core;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public interface VCSManager {
    void createTag(String commitRevision, String tagName);

    void deleteTags(String namePrefix, int numberTagsToLeft) throws GitAPIException, IOException;
}
