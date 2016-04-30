package utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Utils {

    public static String convert(Date date) {


        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, Locale.getDefault());
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, ")
        return dateFormat.format(date);
    }

    public static String convert(int unixTime) {
        return convert(new Date(unixTime));
    }
}
