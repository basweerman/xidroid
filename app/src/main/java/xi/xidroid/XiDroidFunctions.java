package xi.xidroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by basweerman on 12/3/15.
 */
public class XiDroidFunctions {



    //DATE functions
    public static String showDate(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(calendar.getTimeInMillis());
    }

    public static String showDateTimeDatabase(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return showDateTimeDatabase(now);
    }

    public static String showDateTimeDatabase(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(calendar.getTimeInMillis());
    }


    public static String showDateTimeFile(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return showDateTimeFile(now);
    }

    public static String showDateTimeFile(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return formatter.format(calendar.getTimeInMillis());
    }

    public static String showDateTime(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(calendar.getTimeInMillis());
    }

    public static String showDateOnly(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTimeInMillis());
    }

    public static String showHour(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mma");
        return formatter.format(calendar.getTimeInMillis());
    }


}
