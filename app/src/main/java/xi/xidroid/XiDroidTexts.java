package xi.xidroid;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by basweerman on 11/25/15.
 */
public class XiDroidTexts {

    private Map<String, String> textmap = new HashMap<String, String>();
    private Map<String, String> defaultTextmap = new HashMap<String, String>();

    public void loadDefault(){
        defaultTextmap.put("surveyCompletedMessage", "Survey completed");


//old zemi
        defaultTextmap.put("noMessageLabel", "No messages");
        defaultTextmap.put("volumeMessage", "Please turn up volume!");
        defaultTextmap.put("noNetworkMessage", "No network!");
        defaultTextmap.put("batteryLevelRemainingMessage", "Battery level remaining: %d%%");
        defaultTextmap.put("batteryChargingMessage", "(charging)");
        defaultTextmap.put("batteryLevelLowMessage", "Battery level low. Please charge phone");
        defaultTextmap.put("pleaseAnswerAllQuestionsMessage", "Please answer all questions");
        defaultTextmap.put("savingResponsesMessage", "Saving responses");

        defaultTextmap.put("timeReferenceMessage[0]", "We will try again tomorrow.");
        defaultTextmap.put("timeReferenceMessage[1]", "We will get back to you tonight!");
        defaultTextmap.put("timeReferenceMessage[2]", "We will get back to you in the morning!");
        defaultTextmap.put("timeReferenceMessage[3]", "See you tomorrow!");
        defaultTextmap.put("timeReferenceMessage[4]", "We will try again later!");
        defaultTextmap.put("timeReferenceMessage[5]", "We will get back to you in the evening");
        defaultTextmap.put("timeReferenceMessage[6]", "We will try again tonight.");
        defaultTextmap.put("noAlarmsScheduledMessage", "There are currently no alarms scheduled for you");

        defaultTextmap.put("nextAlarmMessage[0]", "Next alarm is at %s today");
        defaultTextmap.put("nextAlarmMessage[1]", "Next alarm is tomorrow at %s");
        defaultTextmap.put("nextAlarmMessage[2]", "Next alarm is more than a week from now");
        defaultTextmap.put("nextAlarmMessage[3]", "Next alarm is %d days from now at %s");

        defaultTextmap.put("greetingMessage[0]", "Good Morning");
        defaultTextmap.put("greetingMessage[1]", "Good Afternoon");
        defaultTextmap.put("greetingMessage[2]", "Good Evening");


        defaultTextmap.put("S_NOTHING", "Welcome to Zemi!");
        defaultTextmap.put("S_MORNING_WINDOW_1_CORTISOL", "%s!\n\nYou can already take your saliva sample if you want.");
        defaultTextmap.put("S_MORNING_WINDOW_1_CORTISOL_ALARM_1", "%s!\n\nTime to take your saliva sample!");
        defaultTextmap.put("S_MORNING_WINDOW_1_CORTISOL_ALARM_2", "Hey Sleepyhead!\nOnly a few minutes left to take your saliva sample! Don’t forget the picture of the labeled vial when you are done!");

        defaultTextmap.put("S_MORNING_WINDOW_1_CORTISOL_CLOSED[0]", "Thanks!\n\nSee you in %d minutes!");
        defaultTextmap.put("S_MORNING_WINDOW_1_CORTISOL_CLOSED[1]", "You missed your wake-up saliva sample today!");


        defaultTextmap.put("S_MORNING_WINDOW_2_CORTISOL_ALARM_1", "Time to take your saliva sample again! Don’t forget to upload the picture of your labeled vial when you are done!");
        defaultTextmap.put("S_MORNING_WINDOW_2_CORTISOL_ALARM_2", "A few more minutes to take your saliva sample again! Don’t forget to upload the picture of your labeled vial when you are done!");
        defaultTextmap.put("S_MORNING_WINDOW_2_CORTISOL_CLOSED[0]", "Thanks! Have a great day!");
        defaultTextmap.put("S_MORNING_WINDOW_2_CORTISOL_CLOSED[1]", "You missed your %d-minute saliva sample this morning! We will try again later!");

        defaultTextmap.put("S_EVENING_WINDOW_3_CORTISOL", "%s!\n\nYou can already take your saliva sample if you want.");
        defaultTextmap.put("S_EVENING_WINDOW_3_CORTISOL_ALARM", "%s!\n\nTime to take your saliva sample! Don’t forget to upload a picture of the labeled vial after you are done!.");
        defaultTextmap.put("S_EVENING_WINDOW_3_CORTISOL_CLOSED[0]", "Thanks! Have a great day!");
        defaultTextmap.put("S_EVENING_WINDOW_3_CORTISOL_CLOSED[1]", "You missed your nighttime saliva sample.");


        defaultTextmap.put("S_MORNING_WINDOW_1_QUESTIONS", "%s!\n\nYou can already answer your questions if you want.");
        defaultTextmap.put("S_MORNING_WINDOW_1_QUESTIONS_ALARM_1", "%s!\n\nTime to answer your questions!");
        defaultTextmap.put("S_MORNING_WINDOW_1_QUESTIONS_ALARM_2", "Hey Sleepyhead!\n\nOnly a few minutes left to answer our questions!");
        defaultTextmap.put("S_MORNING_WINDOW_1_QUESTIONS_CLOSED[0]", "Thanks! See you in %d minutes!");
        defaultTextmap.put("S_MORNING_WINDOW_1_QUESTIONS_CLOSED[1]", "You missed your wakeup questions today!");

        defaultTextmap.put("S_MORNING_WINDOW_2_QUESTIONS_ALARM_1", "Time to answer our questions again!");
        defaultTextmap.put("S_MORNING_WINDOW_2_QUESTIONS_ALARM_2", "Time to answer our questions again!");
        defaultTextmap.put("S_MORNING_WINDOW_2_QUESTIONS_CLOSED[0]", "Thanks! Have a great day!");
        defaultTextmap.put("S_MORNING_WINDOW_2_QUESTIONS_CLOSED[1]", "You missed your wake-up questions today!");
        defaultTextmap.put("S_MORNING_WINDOW_2_QUESTIONS_CLOSED[2]", "You missed your %d-minute questions this morning! We will try again later!");

        defaultTextmap.put("S_EVENING_WINDOW_3_QUESTIONS", "%s!\n\nYou can already answer your evening questions if you want.");
        defaultTextmap.put("S_EVENING_WINDOW_3_QUESTIONS_ALARM", "%s!\n\nTime to answer your evening questions.");


        defaultTextmap.put("S_EVENING_WINDOW_3_QUESTIONS_CLOSED[0]", "Thanks! Have a great day!");
        defaultTextmap.put("S_EVENING_WINDOW_3_QUESTIONS_CLOSED[1]", "You missed your nighttime questions.");

        defaultTextmap.put("cortisolButton", "Record your saliva sample");
        defaultTextmap.put("questionButton", "Answer your questions");


        //QUESTIONS SCREEN
        defaultTextmap.put("questionHeaderMessage", "How were you feeling right before the phone signal?");
        defaultTextmap.put("question1Message", "How stressed are you feeling?");
        defaultTextmap.put("rangeMinView1Label", "not stressed");
        defaultTextmap.put("rangeMaxView1Label", "very stressed");

        defaultTextmap.put("question2Message", "How worried are you feeling?");
        defaultTextmap.put("rangeMinView2Label", "not worried");
        defaultTextmap.put("rangeMaxView2Label", "very worried");

        defaultTextmap.put("question3Message", "How panicked are you feeling?");
        defaultTextmap.put("rangeMinView3Label", "not panicked");
        defaultTextmap.put("rangeMaxView3Label", "very panicked");

        textmap.put("question4Message", "How anxious are you feeling?");
        textmap.put("rangeMinView4Label", "not anxious");
        textmap.put("rangeMaxView4Label", "very anxious");

        defaultTextmap.put("question5Message", "How happy are you feeling?");
        defaultTextmap.put("rangeMinView5Label", "happy");
        defaultTextmap.put("rangeMaxView5Label", "unhappy");

        defaultTextmap.put("question6Message", "How panicky are you feeling?");
        defaultTextmap.put("rangeMinView6Label", "not at all");
        defaultTextmap.put("rangeMaxView6Label", "very much");

        defaultTextmap.put("question7Message", "How anxious are you feeling?");
        defaultTextmap.put("rangeMinView7Label", "not at all");
        defaultTextmap.put("rangeMaxView7Label", "very much");


        defaultTextmap.put("question1Label", "stressed");
        defaultTextmap.put("question2Label", "worried");
        defaultTextmap.put("question3Label", "panicked");
        defaultTextmap.put("question4Label", "anxious");
        defaultTextmap.put("question5Label", "unhappy");
        defaultTextmap.put("question6Label", "panicky");
        defaultTextmap.put("question7Label", "anxious");

        defaultTextmap.put("openEndedNegativeQuestion", "What has got you so");
        defaultTextmap.put("openEndedPositiveQuestion", "What was the best thing that happened to you today?");
        defaultTextmap.put("stressedOutQuestion", "Why are you stressed right now?");

        defaultTextmap.put("stressedOutButton", "How do you feel?");

        defaultTextmap.put("saveResponsesButton", "Save responses");

        //PICTURE SCREEN
        defaultTextmap.put("pictureHeaderMessage", "Please take a picture with the labeled vial clearly visible and send it!");

        defaultTextmap.put("takePictureButton", "Take picture");
        defaultTextmap.put("reTakePictureButton", "Retake picture");

        defaultTextmap.put("sendButton", "Send picture");

        //SETTINGS SCREEN
        defaultTextmap.put("updateButton", "Update");
        defaultTextmap.put("closeButton", "Close");

        //ALARM SCREEN
        defaultTextmap.put("stopAlarmButton", "STOP ALARM");
        defaultTextmap.put("cortisolAlarmMessage", "Time to take your saliva sample!");
        defaultTextmap.put("questionsAlarmMessage", "Time to answer your questions!");

        //recording
        defaultTextmap.put("startRecordingMessage", "recording...");

        //resend messages
        defaultTextmap.put("resendAnswersButton", "Data not sent. Try again");


        defaultTextmap.put("alarmTime",  "20000");
    }

    public void addText(String key, int value){
        textmap.put(key, Integer.toString(value));

    }

    public void addText(String key, String value){
        textmap.put(key, value);
    }

    public void addText(int key, String value){
        textmap.put(Integer.toString(key), value);
    }


    public String getGreeting(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        if (now.get(Calendar.HOUR_OF_DAY) < 12){//good morning
            return getText("greetingMessage[0]");
        }
        else if (now.get(Calendar.HOUR_OF_DAY) < 17){
            return getText("greetingMessage[1]");
        }
        else {
            return getText("greetingMessage[2]");
        }
    }


    public String getText(String key){
        //loadDefault();
        if (textmap.size() == 0){
            loadDefault();
        }
        if (textmap.containsKey(key)){
            return textmap.get(key);
        }
        else {//get default!
            loadDefault(); //take out later!
            if (defaultTextmap.containsKey(key)){
                return defaultTextmap.get(key);
            }
            return "";
        }
    }

    public String getText(String key, int param){
        try {
            return String.format(getText(key), param);
        }
        catch(Exception e){
            loadDefault(); //take out later!
            return getText(key);
        }
    }

    public String getText(String key, String param){
        try {
            return String.format(getText(key), param);
        }
        catch(Exception e){
            loadDefault(); //take out later!
            return getText(key);
        }
    }

    public String getText(String key, int param1, String param2){
        try {
            return String.format(getText(key), param1, param2);
        }
        catch(Exception e){
            loadDefault(); //take out later!
            return getText(key);
        }
    }

    public void readTextsFromString(String input){
        try{
            JSONObject textsJson = new JSONObject(input.trim());
            Iterator<?> keys = textsJson.keys();
            while( keys.hasNext() ){
                String key = (String) keys.next();
                textmap.put(key, (String) textsJson.getString(key));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
