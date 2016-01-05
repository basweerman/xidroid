package xi.xidroid;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.gson.Gson;

/**
 * Created by basweerman on 11/24/15.
 */
public class XiDroidSettings {

    public XiDroidTexts texts = new XiDroidTexts();

    public String message = "No messages";
    public boolean registered = false;
    private String rtid = "";
//    private List<NubisAlarm> alarms = new ArrayList<NubisAlarm>();

    //GPS
    public boolean locationUseGPS = true;
    public boolean locationUseNetwork = true;
    public int locationCheckInterval = 5;
    public boolean locationLinkNotification = false;

    public static int N_RTID = 1;
    public static int N_MESSAGE = 2;
    public static int N_ALARMS = 3;
    public static int N_TEXTS = 4;
    public static int N_RESET = 5;
    public static int N_NEWAPK = 6;



    public boolean randomizeQuestions = false;

    private int alarmTime = 20000;
    private boolean passwordProtection = true;
    private String password = "";

    public String localServerURL = "http://localhost:8080/xi/api/index.php";
    public String remoteServerURL = "http://128.125.142.97/bas/xi/xidroid/index.php";

    public int numberOfCortisol = 0;

    public int cortisolExtraDayNumber = 5;
    public int cortisolTotalMeasures = 9;

    public boolean allowOfflineDataEntry = true;
    public int pictureCompression = 50;

    private Context context;
    public String loadNewAPK = null;

    public String userLastUploaded = null;

    public String versionAPK = null;
    public String urlAPK = null;

    public boolean hasPasswordProtection(){
        return passwordProtection;
    }

    public boolean checkPassword(String check){
        return password.equals(check);
    }

    public String getServerURL(int type){
        if (type == 1){
            return this.localServerURL;
        }
        return this.remoteServerURL;
    }

    public void clearSettings(){
        passwordProtection = true;
//        this.alarms.clear();
        rtid = "";
    }

 //   public void addAlarm(NubisAlarm alarm){
  //      alarms.add(alarm);
 //   }

 //   public List<NubisAlarm> getAlarms(){
 //       return alarms;
 //   }

 //   public NubisAlarm getAlarm(int id){
 //       return alarms.get(id);
 //   }

    public String getRtid(){
        return this.rtid;
    }

    public void setRtid(String rtid){
        this.rtid = rtid;
    }

//    public int getAlarmCount(){
//        return alarms.size();
//
//    }

    public String toLog(){
        String log = "";
        log += "\nRTID: " + this.rtid;
 /*       for (int i = 0; i < this.getAlarmCount(); i++) {
            NubisAlarm alarm = this.getAlarm(i);
            if (alarm.parentid == -1){
                log += "\nalarm[" + i + "] * : " + NubisMain.showDate(alarm.getCalendar()) + " (" + alarm.getAlarmType() + ")  " + alarm.getAnswered();
            }
            else {
                log += "\nalarm[" + i + "]: " + NubisMain.showDate(alarm.getCalendar()) + " (" + alarm.getAlarmType() + ") " + alarm.getAnswered();
            }
        }*/
        return log;
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public XiDroidSettings create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, XiDroidSettings.class);
    }

    public void resetSettings(){
        passwordProtection = true;
        numberOfCortisol = 0;
    }

//    public int getAlarmTime(){
//        if (texts.getText("alarmTime") != ""){
//            return Integer.parseInt(texts.getText("alarmTime"));
//        }
//        return this.alarmTime;
//    }


    public void readSettingsFromObject(XiDroidHTTPReturnObject returnObject){
        versionAPK = returnObject.getText("APK_VERSION", null);
        urlAPK = returnObject.getText("APK_URL", null);
    }

    public void readSettingsFromString(String serverResponseMessage){
        try {
            int state = 0;
            if (serverResponseMessage != ""){
                String textStr[] = serverResponseMessage.split("\\r?\\n");
                Calendar cal = Calendar.getInstance();
                for(String s: textStr){
                    if (s.equals("NOT REGISTERED")){
                        this.registered = false;
                    }
                    else if (s.equals("RTID")){
                        this.rtid = "";
                        state = N_RTID;
                    }
                    else if (s.equals("NEWAPK")){  //new apk!
                        this.loadNewAPK = "";
                        state = N_NEWAPK;
                    }
//                    else if (s.equals("ALARMS")){
//                        this.alarms.clear();
//                        state = N_ALARMS;
//                    }
                    else if (s.equals("RESET")){
                        //this.alarms.clear();
                        resetSettings();
                        state = N_RESET;
                    }
                    else if (s.equals("MESSAGE")){
                        message = "No messages";
                        state = N_MESSAGE;
                    }
                    else if (s.equals("TEXTS")){

                        state = N_TEXTS;
                    }
                    else {
                        if (state == N_RTID){
                            this.setRtid(s);
                            this.registered = true;
                        }
//                        else if (state == N_ALARMS){
//                            if (!s.trim().equals("")){ //not empty string! -> no alarms
//                                String details[] = s.split("~");
//                                cal = Calendar.getInstance();
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                                cal.setTime(sdf.parse(details[0]));
//                                cal.set(Calendar.SECOND, 1);
//                                addAlarm(new NubisAlarm(cal, -1, Integer.parseInt(details[1]), Integer.parseInt(details[2]), Integer.parseInt(details[3]), Integer.parseInt(details[4]), (Integer.parseInt(details[5]) != 0), (Integer.parseInt(details[6]) != 0))); //date, type and answered, answered2.. + alert!
//                            }
//                        }
                        else if (state == N_MESSAGE){
                            message = s;
                        }
                        else if (state == N_TEXTS){
                            texts.readTextsFromString(s);
                        }
                        else if (state == N_NEWAPK){
                            this.loadNewAPK = s;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    public String updateFromServer(Context context){
		try {

			//get from the Internet
			NubisDelayedAnswer delayedanswer = new NubisDelayedAnswer(NubisDelayedAnswer.N_GET_READ);
			delayedanswer.addGetParameter("id", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
			delayedanswer.addGetParameter("p", "settings");

	        String serverResponseMessage = ((NubisApplication)context.getApplicationContext()).communication.upLoad(context, delayedanswer, false, -1, NubisHTTP.H_DOWNLOAD);

	        // ((NubisApplication)context.getApplicationContext()).communication.doInBackground();
	        // run through 'execute'
	        */
			/*
	        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	        String upLoadServerUri = NubisMain.SERVERURL + "?id=" + android_id;
	        upLoadServerUri += "&p=settings";

	        URL url = new URL(upLoadServerUri);

	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str; String response = "";
	        while ((str = in.readLine()) != null) {
	        	response += (str +"\n");
	        }
	        in.close();
	        String serverResponseMessage = response;*/
	        /*
	        if(serverResponseMessage == null || !String.valueOf(((NubisApplication)context.getApplicationContext()).communication.serverResponseCode).startsWith("2")){
	    		//  error in communication!!
	        }
	        else { //communication ok: update settings
				//update settings
				clearSettings();

				//read from string
				readSettingsFromString(serverResponseMessage);

				//save settings
				((NubisApplication)context.getApplicationContext()).saveSettings();

				//update application settings
				((NubisApplication)context.getApplicationContext()).clear();
		    }	*/
    /*
			return serverResponseMessage;
	    }
	    catch (Exception e) {
	       e.printStackTrace();
		   Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();

	    }
		return "";
    }*/


}
