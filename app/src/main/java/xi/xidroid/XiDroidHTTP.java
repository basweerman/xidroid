package xi.xidroid;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by basweerman on 11/24/15.
 */
public class XiDroidHTTP extends XiDroidProgressDialogTask<String, String, String> {
//public class XiDroidHTTP extends AsyncTask<String, Integer, String> {

    public static final int H_UPLOAD = 0;
    public static final int H_DOWNLOAD = 1;
    public static final int H_CHECK_SERVER = 2;


    public static final int H_LOCAL_SERVER = 1;
    public static final int H_REMOTE_SERVER = 2;



    public XiDroidAsyncResponse delegate=null;

    private int serverType;
    private Context context;
    private XiDroidHTTPDataObject dataObject;
    int serverResponseCode;
    String serverResponseMessage;
    int deleteId = -1;
    String serverInstructions;
    int communicationType = 0;

    public XiDroidHTTP(Context context, int serverType, XiDroidAsyncResponse delegate, int communicationType, boolean showDialog) {
        super(context, "Communication", "initializing", showDialog);
        this.context = context;
        this.serverType = serverType;
        this.delegate = delegate;
        this.communicationType = communicationType;
    }

    public void setDataObject(XiDroidHTTPDataObject dataObject){
        this.dataObject = dataObject;
    }

     public XiDroidHTTP(Context context, int serverType, XiDroidHTTPDataObject dataObject, XiDroidAsyncResponse delegate, int deleteId, int communicationType, boolean showDialog){
        super(context, "Communication", "initializing", showDialog);

        this.context = context;
        this.serverType = serverType;
        this.dataObject = dataObject;
        this.delegate = delegate;
        this.deleteId = deleteId;
        this.communicationType = communicationType;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            publishProgress("connecting to server");
            if (dataObject.getType() == XiDroidHTTPDataObject.N_GET){
                String upLoadServerUri = ((XiDroidApplication)context.getApplicationContext()).settings.getServerURL(this.serverType) + "?q=" + XiDroidCommunication.Encrypt(dataObject.getGetString());
                URL url = new URL(upLoadServerUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // Open a HTTP  connection to  the URL
                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();
                //((NubisApplication)context.getApplicationContext()).settings.readSettingsFromString(serverResponseMessage);
                return serverResponseMessage;
            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_GET_READ || dataObject.getType() == XiDroidHTTPDataObject.N_CHECK_SERVER){
                String upLoadServerUri = ((XiDroidApplication)context.getApplicationContext()).settings.getServerURL(this.serverType) + "?q=" + XiDroidCommunication.Encrypt(dataObject.getGetString());
                URL url = new URL(upLoadServerUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // Open a HTTP  connection to  the URL
                conn.setConnectTimeout(5000);  //need timeout here!
                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();
                if(!String.valueOf(serverResponseCode).startsWith("2")){
                    //  error!!
                    return serverResponseMessage;
                }
                else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str;
                    String response = "";
                    while ((str = in.readLine()) != null) {
                        response += (str +"\n");
                        publishProgress("receiving data");
                    }
                    in.close();
                    publishProgress("uncompressing data");
                    response = XiDroidCommunication.Uncompress(response); //uncompress
                    serverInstructions = XiDroidCommunication.Unencrypt(response).trim();  //unencrypt
                    return serverInstructions;
                }


            }

            else if (dataObject.getType() == XiDroidHTTPDataObject.N_POST){
                String upLoadServerUri = ((XiDroidApplication)context.getApplicationContext()).settings.getServerURL(this.serverType) + "?q=" + XiDroidCommunication.Encrypt(dataObject.getGetString());
                URL url = new URL(upLoadServerUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                byte[] outputInBytes = dataObject.getPostData().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();

                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();


                os.write( outputInBytes );
                os.close();
                //((NubisApplication)context.getApplicationContext()).settings.readSettingsFromString(serverResponseMessage);
                return serverResponseMessage;

            }
            else if (dataObject.getType() == XiDroidHTTPDataObject.N_POST_FILE){
                String upLoadServerUri = ((XiDroidApplication)context.getApplicationContext()).settings.getServerURL(this.serverType) + "?q=" + XiDroidCommunication.Encrypt(dataObject.getGetString());
                URL url = new URL(upLoadServerUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + XiDroidHTTPDataObject.N_boundary);
                conn.setRequestProperty("uploaded_file", "test");

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(XiDroidHTTPDataObject.N_twoHyphens + XiDroidHTTPDataObject.N_boundary + XiDroidHTTPDataObject.N_lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + "test" + "\"" + XiDroidHTTPDataObject.N_lineEnd);

                dos.writeBytes(XiDroidHTTPDataObject.N_lineEnd);
                dataObject.getByteArrayOutputStream().writeTo(dos);

                // send multipart form data necesssary after file data...
                dos.writeBytes(XiDroidHTTPDataObject.N_lineEnd);
                dos.writeBytes(XiDroidHTTPDataObject.N_twoHyphens + XiDroidHTTPDataObject.N_boundary + XiDroidHTTPDataObject.N_twoHyphens + XiDroidHTTPDataObject.N_lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();


                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                String response = "";
                while ((str = in.readLine()) != null) {
                    response += (str +"\n");
                    publishProgress("receiving data");
                }
                in.close();
                publishProgress("uncompressing data");
                response = XiDroidCommunication.Uncompress(response); //uncompress
                serverInstructions = XiDroidCommunication.Unencrypt(response).trim();  //unencrypt
                //return serverInstructions;


                dos.flush();
                dos.close();

                //((NubisApplication)context.getApplicationContext()).settings.readSettingsFromString(serverResponseMessage);
                return serverResponseMessage;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
   /*     if (communicationType == H_DOWNLOAD){
            Toast.makeText(context, "downloading...", Toast.LENGTH_SHORT).show();
        }
        else if (communicationType == H_CHECK_SERVER){
            Toast.makeText(context,"connecting...", Toast.LENGTH_SHORT).show();
        }
        else {
            String responseStr = "";
            if (deleteId > 0){
                responseStr = " item " + deleteId;
            }
            Toast.makeText(context,"uploading..." + responseStr, Toast.LENGTH_SHORT).show();
        }*/

        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        // PowerManager pm = (PowerManager) tempcontent.getSystemService(Context.POWER_SERVICE);
        //       mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        //            getClass().getName());
        //       mWakeLock.acquire();
        //  mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        //mWakeLock.release();
        //mProgressDialog.dismiss();
        // Toast.makeText(context,"downloading....." + deleteId, Toast.LENGTH_SHORT).show();
        super.onPostExecute(result);
        dismissProgress();
        if (delegate != null){
            XiDroidHTTPReturnObject returnObject = new XiDroidHTTPReturnObject(result);
            delegate.processFinish(result, serverResponseCode, serverResponseMessage, dataObject, returnObject);
        }


    }


}
