package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Utils {

    public static String convert(Date date) {


        //DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, Locale.getDefault(Locale.Category.DISPLAY));//, Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String convert(int unixTime) {
        return convert(new Date(unixTime));
    }
}
