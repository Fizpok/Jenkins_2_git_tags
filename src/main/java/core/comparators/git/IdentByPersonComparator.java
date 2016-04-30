package core.comparators.git;

import org.eclipse.jgit.lib.PersonIdent;

import java.util.Comparator;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class IdentByPersonComparator implements Comparator<PersonIdent> {

    @Override
    public int compare(PersonIdent o1, PersonIdent o2) {
        int result = o1.getName().compareTo(o2.getName());
        result += o1.getEmailAddress().compareTo(o2.getEmailAddress());
        return result;
    }
}
