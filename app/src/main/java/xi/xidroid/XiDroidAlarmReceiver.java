package xi.xidroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.Calendar;

/**
 * Created by basweerman on 11/29/15.
 */
public class XiDroidAlarmReceiver extends BroadcastReceiver {

    public PendingIntent pi;
    private AlarmManager am;
    private static Handler handler;
    private String msg;

    public void SetAlarmEveryNMinutes(Context context, AlarmManager am, Handler hand, int n, String message) {
        handler = hand;
        this.am = am;
        msg = message;
//        this.alarm = alarm;
        Intent i = new Intent(context, XiDroidAlarmReceiver.class);
        //i.putExtra("index", id);
        this.pi = PendingIntent.getBroadcast(context, 1, i, 0); //was 0
        //   String test = NubisMain.showDate(alarm.getCalendar());
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
//        if (now.getTimeInMillis() > alarm.getCalendar().getTimeInMillis()){ //alarm in the past..
//            alarm.active = false;
//        }
//        if (alarm.active){ //only add if an alarm is active!

            Calendar cal = Calendar.getInstance();
            //am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5*60*1000, pi);
//            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1*60*1000, pi);
            am.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), n*60*1000, pi);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 1*60*1000, pi);



 //       }
        //ELAPSED_REALTIME_WAKEUP t
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            handler.obtainMessage(0, msg).sendToTarget();

            //Message msg = handler.obtainMessage();
            //msg.
            //handler.dispatchMessage(msg);
        } catch (Exception e) {

        }
    }



}






