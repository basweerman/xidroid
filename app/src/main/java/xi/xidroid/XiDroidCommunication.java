package xi.xidroid;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

/**
 * Created by basweerman on 11/24/15.
 */
public class XiDroidCommunication implements XiDroidAsyncResponse{

    String HTTPReturnString = "";
    private Context context;
    private static Handler handler = null;

    public XiDroidCommunication(Context c) {
        super();
        context = c;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public void processFinish(String output, int responseCode, String responseString, XiDroidHTTPDataObject dataObject, XiDroidHTTPReturnObject returnObject) {
        HTTPReturnString = output;


        if (dataObject != null) {
            if (dataObject.getGetParameter("API") != null && dataObject.getGetParameter("API").equals("xiUploadToRemoteServer")){ //uploaded
                //Upload data success: update lastupload date/time
                String test = returnObject.getText("RESULT");
                if (test != null && test.equals("8")){ //8: query sucessfull
                    ((XiDroidApplication) context.getApplicationContext()).settings.userLastUploaded = XiDroidFunctions.showDateTimeDatabase();
                }
            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_GET) {

            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_CHECK_SERVER) {
                if (HTTPReturnString != null && HTTPReturnString != "") {
                    if (HTTPReturnString.equals("SERVER UP!!!")) { //server up check!
                        //   sendDelayedAnswer(context); //upload delayed answers
                    } else {
                        //  handleNoConnection(context);
                    }
                } else {
                    //  handleNoConnection(context);
                }
            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_GET_READ) {
                if (HTTPReturnString != "") {
                    ((XiDroidApplication) context.getApplicationContext()).settings.readSettingsFromObject(returnObject);
                    //save settings
                    ((XiDroidApplication) context.getApplicationContext()).saveSettings();
                    //update application settings
                    ((XiDroidApplication) context.getApplicationContext()).clear();
                    //     readSettingsDone = true;
                }
            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_POST) {

            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_POST_FILE) {


            }
        }
//        if (deleteId != -1){ //mark as 'sent' so next time they will get deleted.
//            delayedAnswers.getDelayedAnswer(deleteId).sent = true;
//            storeDelayedAnswers(); ///update!
//        }

        if (handler != null){
            handler.obtainMessage(1, "done").sendToTarget();
            handler = null;
        }
    }


  /*  public boolean isRemoteServerUp(XiDroidMainScreen droidm, Context context){
        this.context = context;
        boolean connected = false;
        HTTPReturnString = "";
        try {
            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_CHECK_SERVER);
            dataObject.addGetParameter("id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("api", "xiIsRemoteServerUp");
            upLoad(context, XiDroidHTTP.H_LOCAL_SERVER, dataObject, true, -1, XiDroidHTTP.H_CHECK_SERVER);
            connected = (HTTPReturnString.equals("SERVER UP!!!"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return connected; //return value is doing nothing!!
    }*/


    public void createTableIfNotExists(String tables){
        //this.context = context;
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_LOCAL_SERVER, this, XiDroidHTTP.H_UPLOAD, false);

            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_GET_READ);
            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "xiCreateTablesIfNotExistsLocal");
            dataObject.addGetParameter("TABLES", tables);

            httpCom.setDataObject(dataObject);
            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void uploadData(){
        //this.context = context;
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_LOCAL_SERVER, this, XiDroidHTTP.H_UPLOAD, true);

            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_GET_READ);
            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "xiUploadToRemoteServer");
            dataObject.addGetParameter("TABLES", "[\"data\",\"log\",\"location\",\"appdata\"]");
            dataObject.addGetParameter("LASTUPDATE", ((XiDroidApplication) context.getApplicationContext()).settings.userLastUploaded);

            httpCom.setDataObject(dataObject);
            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void runLocalQuery(String query){
        //this.context = context;
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_LOCAL_SERVER, this, XiDroidHTTP.H_UPLOAD, false);

            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_GET_READ);
            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "xiRunLocalQuery");
            dataObject.addGetParameter("QUERY", query);

            httpCom.setDataObject(dataObject);

            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadFile(String fileName, Map<String, String> parameters){
        //this.context = context;
        try {

            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_LOCAL_SERVER, this, XiDroidHTTP.H_UPLOAD, true);
            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_POST_FILE);

            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "xiUploadFileLocal");

            Gson gson = new Gson();
            dataObject.addGetParameter("RECORD", gson.toJson(parameters));

            dataObject.addFileName(fileName);

            httpCom.setDataObject(dataObject);
            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
/*
    public void uploadJson(Context context, Map<String, String> parameters){
        this.context = context;
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_LOCAL_SERVER, this, XiDroidHTTP.H_UPLOAD, false);

            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_GET_READ);
            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "xiRunLocalQuery");

            Gson gson = new Gson();
            dataObject.addGetParameter("RECORD", gson.toJson(parameters));

            httpCom.setDataObject(dataObject);
            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public void checkForUpdatesRemoteServer(){
        //this.context = context;
        boolean connected = false;
        HTTPReturnString = "";
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, XiDroidHTTP.H_REMOTE_SERVER, this, XiDroidHTTP.H_UPLOAD, true);

            XiDroidHTTPDataObject dataObject = new XiDroidHTTPDataObject(XiDroidHTTPDataObject.N_GET_READ);
            dataObject.addGetParameter("ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            dataObject.addGetParameter("VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            dataObject.addGetParameter("API", "loadsettings");

            httpCom.setDataObject(dataObject);
          //  httpCom.serverInstructions = "";
            httpCom.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
    public String upLoad(Context context, int localServer, XiDroidHTTPDataObject dataObject, boolean wait, int deleteId, int communicationType){
        //Context context, NubisDelayedAnswer delayedAnswer, NubisAsyncResponse delegate
        this.context = context;
        try {
            XiDroidHTTP httpCom = new XiDroidHTTP(context, localServer, dataObject, this, deleteId, communicationType);
            if (wait){
                httpCom.serverInstructions = "";
                httpCom.execute(); //doInBackground();//.get(210000, TimeUnit.MILLISECONDS);

                long startTime = System.currentTimeMillis();
                while(httpCom.serverInstructions == ""){
                    if ((System.currentTimeMillis()-startTime)>5000){ break; } //timeout!
                    // waiting until finished protected String[] doInBackground(Void... params)
                }
                HTTPReturnString = httpCom.serverInstructions;
            }
            else {
                httpCom.execute();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
*/


    public static String Unencrypt(String inputStr){
        try {
            XiDroidMCrypt mcrypt = new XiDroidMCrypt();
            return new String( mcrypt.decrypt( inputStr) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStr;
    }

    public static String Encrypt(String inputStr){
        try {
            XiDroidMCrypt mcrypt = new XiDroidMCrypt();
            return XiDroidMCrypt.bytesToHex( mcrypt.encrypt(inputStr) );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return inputStr;
    }


    public static String Uncompress(String inputStr){
        String unzipped = "";
        try {
            byte[] zbytes = Base64.decode(inputStr, Base64.DEFAULT);
            // byte[] zbytes = zippedText.getBytes("ISO-8859-1");
            // Add extra byte to array when Inflater is set to true
            byte[] input = new byte[zbytes.length + 1];
            System.arraycopy(zbytes, 0, input, 0, zbytes.length);
            input[zbytes.length] = 0;
            ByteArrayInputStream bin = new ByteArrayInputStream(input);
            InflaterInputStream in = new InflaterInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b); }
            bout.close();
            unzipped = bout.toString();
        }
        catch (IOException io) {

        }
        catch (Exception e){

        }
        return unzipped;

    }

}
