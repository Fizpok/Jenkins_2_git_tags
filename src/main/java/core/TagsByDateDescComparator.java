package core;

import org.eclipse.jgit.revwalk.RevTag;

import java.util.Comparator;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class TagsByDateDescComparator<R> implements Comparator<RevTag> {
    @Override
    public int compare(RevTag o1, RevTag o2) {
        return o2.getTaggerIdent().getWhen().compareTo(o1.getTaggerIdent().getWhen());
    }
}
