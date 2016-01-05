package xi.xidroid;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by basweerman on 11/23/15.
 */
public class XiDroidApplication extends Application {

    public boolean appRunning = false;
    public XiDroidCommunication communication; // = new XiDroidCommunication();

    public int adminEntries = 0;


    public XiDroidGPS gpsInfo;

    public String androidId;

    public XiDroidSettings settings = new XiDroidSettings();
    public AlarmManager alarmMgr = null;
    public String appLocation = "/data/data/xi.xidroid";
    public String sdcardLocation = "/sdcard/xidroid/htdocs";
    public String mysqldLocation = "/data/data/xi.xidroid/services/mysql/sbin/mysqld";



    private SharedPreferences preferencesReader;

    public void loadSettings(SharedPreferences preferencesReader, Context context){
        try {
            this.preferencesReader = preferencesReader;
            String serializedDataFromPreference = preferencesReader.getString("Settings", null);
            if (serializedDataFromPreference != null){ // Create a new object from the serialized data with the same state
                settings = XiDroidSettings.create(serializedDataFromPreference);
            }
            androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        catch (Exception e) {
            Toast.makeText(context, "Settings changed. Plz update app!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void saveSettings(){
        // Serialize the object into a string
        String serializedData = settings.serialize();
        SharedPreferences preferencesReader = this.preferencesReader; //  context.getSharedPreferences("NubisSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesReader.edit();
        editor.putString("Settings", serializedData);
        editor.commit();
    }

    public void clear(){
/*        alarms.clear();
        status = 0;

        lastMainAlarmIndex = -1;
        lastMainAlarm = null;

        lastSubAlarmIndex = -1;
        lastSubAlarm = null;

        log = "";*/

    }


}