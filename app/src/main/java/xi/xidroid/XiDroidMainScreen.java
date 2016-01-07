package xi.xidroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XiDroidMainScreen extends AppCompatActivity implements View.OnClickListener {

    //CAMERA STATICS
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private Bitmap mImageBitmap = null;

    //SOUND RECORDING
    private MediaRecorder mRecorder = null;
    private boolean recording = false;
    private boolean soundrecorded = false;
    private String soundFileName = null;

    //HOME SCREEN
    TableLayout homeLayout;
    Button surveyBtn;

    //Questions
    LinearLayout questionLayout;
    List<XiDroidDynamicQuestion> dynamicQuestions = new ArrayList<XiDroidDynamicQuestion>();

    //SURVEYS
    WebView webView;

    //INFI
    LinearLayout infoLayout;

    //ADMIN
    LinearLayout adminLayout;
    Button mAdminerBtn;
    Button mInstallBtn;
    Button mRestartServerBtn;
    Button mUpdateBtn;

    //GPS
    Button gpsBtn;
    LinearLayout gpsLayout;
    TextView gpsText;
    int locationAlarmCounter = 0;
    int dayAlarmCounter = 0;
    String log = "";

    //REPORT IN
    LinearLayout reportInLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xi_droid_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);
        setSupportActionBar(toolbar);

        if (!((XiDroidApplication) getApplicationContext()).appRunning) { //only once!!!
            ((XiDroidApplication) getApplicationContext()).appRunning = true;

            SharedPreferences preferencesReader = getSharedPreferences("XiDroidSettings", Context.MODE_PRIVATE);
            // Read the shared preference value
            ((XiDroidApplication) getApplicationContext()).loadSettings(preferencesReader, this);

            //Load communication module
            ((XiDroidApplication) getApplicationContext()).communication = new XiDroidCommunication(this);

            //START GPS service
            ((XiDroidApplication) getApplicationContext()).gpsInfo = new XiDroidGPS(this);

            //check to see if everything is installed
            File file = new File(((XiDroidApplication) getApplicationContext()).mysqldLocation /*"/data/data/xi.xidroid/services/mysql/sbin/mysqld"*/);
            if (file.exists()) {  //mysql is there already.. start server
                startServiceOnClickListenerIntent();
            } else { //install
                serviceInstallListenerIntent();
                //start the service when this is done!!
            }

            checkForUpdatedAPK();

            ((XiDroidApplication) getApplicationContext()).alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            XiDroidAlarmReceiver test1 = new XiDroidAlarmReceiver();

            test1.SetAlarmEveryNMinutes(this, ((XiDroidApplication) getApplicationContext()).alarmMgr, new Handler() {

                public void handleMessage(Message msg) {
                    //Handle GPS
                    if (((XiDroidApplication) getApplicationContext()).settings.locationUseGPS || ((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork) {
                        //called every 5 minutes.. Look at gps settings
                        int interval = ((XiDroidApplication) getApplicationContext()).settings.locationCheckInterval;  //5/10/15/60
                        locationAlarmCounter++;
                        if (interval / locationAlarmCounter == 5) {
                            ((XiDroidApplication) getApplicationContext()).gpsInfo.storeLocation(((XiDroidApplication) getApplicationContext()).settings.locationUseGPS, ((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork);
                            locationAlarmCounter = 0;
                        }
                    }
                    dayAlarmCounter++;
                    if (dayAlarmCounter >= 288){ //once every day
                        //Handle daily upload/download
                        ((XiDroidApplication) getApplicationContext()).communication.checkForUpdatesRemoteServer();
                        ((XiDroidApplication) getApplicationContext()).communication.uploadData();
                        dayAlarmCounter = 0;
                    }
                }
            }, 5, "msg1");

        /*    XiDroidAlarmReceiver test2 = new XiDroidAlarmReceiver();
            test2.SetAlarmEveryNMinutes(this, ((XiDroidApplication) getApplicationContext()).alarmMgr, new Handler() {

                public void handleMessage(Message msg) {
                    //Handle daily upload/download
                    ((XiDroidApplication) getApplicationContext()).communication.checkForUpdatesRemoteServer();
                    ((XiDroidApplication) getApplicationContext()).communication.uploadData();

                }
            }, 1, "msg2");  //1440
*/

        }






        //SURVEYS
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new webViewClient());
        webView.setWebChromeClient(new chromeViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        //webView.clearCache(true);
        webSettings.setJavaScriptEnabled(true);

        //QUESTIONS
        questionLayout = (LinearLayout) findViewById(R.id.questionLayout);
        Button emaBtn = (Button) findViewById(R.id.emaBtn);
        emaBtn.setOnClickListener(this);


        //HOME
        homeLayout = (TableLayout) findViewById(R.id.homeLayout);
        surveyBtn = (Button) findViewById(R.id.surveyBtn);
        surveyBtn.setOnClickListener(this);
        Button reportInBtn = (Button) findViewById(R.id.reportInBtn);
        reportInBtn.setOnClickListener(this);

        //INFO
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);

        //REPORT IN
        reportInLayout = (LinearLayout) findViewById(R.id.reportInLayout);
        reportInLayout.setVisibility(View.GONE);
        ImageButton cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(this);
        ImageButton microphoneBtn = (ImageButton) findViewById(R.id.microphoneBtn);
        microphoneBtn.setOnClickListener(this);
        Button reportInSubmitBtn = (Button) findViewById(R.id.reportInSubmitBtn);
        reportInSubmitBtn.setOnClickListener(this);

        //GPS
        gpsLayout = (LinearLayout) findViewById(R.id.gpsLayout);
        gpsLayout.setVisibility(View.GONE);
        gpsBtn = (Button) findViewById(R.id.gpsBtn);
        gpsBtn.setOnClickListener(this);
        gpsText = (TextView) findViewById(R.id.gpsView);

        //ADMIN
        adminLayout = (LinearLayout) findViewById(R.id.adminLayout);
        adminLayout.setVisibility(View.GONE);
        mAdminerBtn = (Button) findViewById(R.id.adminerBtn);
        mAdminerBtn.setOnClickListener(this);
        mInstallBtn = (Button) findViewById(R.id.installBtn);
        mInstallBtn.setOnClickListener(this);
        mRestartServerBtn = (Button) findViewById(R.id.restartBtn);
        mRestartServerBtn.setOnClickListener(this);
        mUpdateBtn = (Button) findViewById(R.id.updateBtn);
        mUpdateBtn.setOnClickListener(this);
        Button mInfoButn = (Button) findViewById(R.id.infoBtn);
        mInfoButn.setOnClickListener(this);

    }

    public void checkForUpdatedAPK() {
        try {  //is this version the latest apk??
            String latestApkVersion = ((XiDroidApplication) getApplicationContext()).settings.versionAPK;
            if (latestApkVersion != null && !(latestApkVersion.equals(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName))) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((XiDroidApplication) getApplicationContext()).settings.urlAPK)));
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    private void dispatchInfoIntent(){
        getSupportActionBar().setTitle("XiDroid Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(infoLayout);

        TextView infoText = (TextView) findViewById(R.id.infoText);

        String infoStr = "";
        infoStr += "Android id: " + ((XiDroidApplication) getApplicationContext()).androidId + "\n";
        infoStr += "Last upload: " + ((XiDroidApplication) getApplicationContext()).settings.userLastUploaded + "\n";

        infoText.setText(infoStr);

    }

    private void setScreen(View layout){
        homeLayout.setVisibility(View.GONE);
        adminLayout.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        gpsLayout.setVisibility(View.GONE);
        reportInLayout.setVisibility(View.GONE);
        infoLayout.setVisibility(View.GONE);
        questionLayout.setVisibility(View.GONE);

        layout.setVisibility(View.VISIBLE);

    }


    private void reportInScreen() {
        getSupportActionBar().setTitle("XiDroid Report In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(reportInLayout);


        EditText openEnded = (EditText) findViewById(R.id.openEndedText);
        openEnded.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(openEnded, InputMethodManager.SHOW_FORCED);


    }


    private void startGpsScreen() {
        getSupportActionBar().setTitle("XiDroid GPS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(gpsLayout);


        CheckBox locationUseGps = (CheckBox) findViewById(R.id.chkBoxUseGPS);
        locationUseGps.setChecked(((XiDroidApplication) getApplicationContext()).settings.locationUseGPS);
        locationUseGps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((XiDroidApplication) getApplicationContext()).settings.locationUseGPS = !((XiDroidApplication) getApplicationContext()).settings.locationUseGPS;
                ((XiDroidApplication) getApplicationContext()).saveSettings();
            }
        });
        CheckBox locationUseNetwork = (CheckBox) findViewById(R.id.chkBoxUseNetwork);
        locationUseNetwork.setChecked(((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork);
        locationUseNetwork.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork = !((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork;
                ((XiDroidApplication) getApplicationContext()).saveSettings();
            }
        });
        Spinner locationCheckInterval = (Spinner) findViewById(R.id.spnrLocationCheckInterval);
        final int[] timing_values = getResources().getIntArray(R.array.gps_interval_times_items);
        locationCheckInterval.setSelection(Ints.indexOf(timing_values, ((XiDroidApplication) getApplicationContext()).settings.locationCheckInterval));
        locationCheckInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final int[] timing_values = getResources().getIntArray(R.array.gps_interval_times_items);
                ((XiDroidApplication) getApplicationContext()).settings.locationCheckInterval = timing_values[pos]; // 5, 10, 15, 60
                ((XiDroidApplication) getApplicationContext()).saveSettings();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //locationCheckInterval.setOnItemClickListener(this);
        CheckBox locationLinkNotification = (CheckBox) findViewById(R.id.chkBoxNotification);
        locationLinkNotification.setChecked(((XiDroidApplication) getApplicationContext()).settings.locationLinkNotification);
        locationLinkNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((XiDroidApplication) getApplicationContext()).settings.locationLinkNotification = !((XiDroidApplication) getApplicationContext()).settings.locationLinkNotification;
                ((XiDroidApplication) getApplicationContext()).saveSettings();
            }
        });

//        public int locationCheckInterval = 5;
//        public boolean locationLinkNotification = false;


        gpsText.setText(((XiDroidApplication) getApplicationContext()).gpsInfo.getLocationAsString(
                ((XiDroidApplication) getApplicationContext()).settings.locationUseGPS,
                ((XiDroidApplication) getApplicationContext()).settings.locationUseNetwork));

    }

    private void startSurveyScreen() {
        String url = "http://localhost:8080/xi/?clear=1";
//        if (!webView.getUrl().equals(url)) {  //won't reload.. disable for now
        webView.loadUrl(url);
//        }


        getSupportActionBar().setTitle("XiDroid Surveys");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(webView);

    }


    private void homeOnClickListenerIntent() {
        //startServiceOnClickListenerIntent();
        getSupportActionBar().setTitle("XiDroid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setScreen(homeLayout);

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.adminLayout.getWindowToken(), 0);
    }

    public void startAdminScreen() {
        getSupportActionBar().setTitle("XiDroid Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(adminLayout);

        File file = new File(((XiDroidApplication) getApplicationContext()).mysqldLocation); // /*"/data/data/xi.xidroid/services/mysql/sbin/mysqld"/);
        if (!file.exists()) {  //don't show if mysql is there already
            mInstallBtn.setEnabled(true);
            mRestartServerBtn.setEnabled(false);
        } else {
            mInstallBtn.setEnabled(false);
            mRestartServerBtn.setEnabled(true);
        }
    }


    public void startServiceOnClickListenerIntent() {
        Context context = this;
        context.startService(new Intent(context, XiDroidMainScreen.class));

        final boolean enableSU = false; //.getBoolean("run_as_root", false);
        final String execName = "lighttpd"; //preferences.getString("use_server_httpd", "lighttpd");
        final String bindPort = "8080"; //preferences.getString("server_port", "8080");

        XiDroidCommandTaskXiDroid task = XiDroidCommandTaskXiDroid.createForConnect(this, context);
        task.enableSU(enableSU);
        task.execute();
    }


    private void serviceInstallListenerIntent() {
        try {
            //services
            HashMap keepFiles = new HashMap();
            String INSTALL_DIR = ((XiDroidApplication) getApplicationContext()).appLocation;  //'"/data/data/xi.xidroid";
            extractZip(getAssets().open("content.zip"), INSTALL_DIR, keepFiles);
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }
        log += "unzipped";
    }

    private void phpInstallListenerIntent() {
        try {
            //php
            HashMap keepFiles = new HashMap();
            String INSTALL_DIR = ((XiDroidApplication) getApplicationContext()).sdcardLocation; // "/sdcard/xidroid/htdocs";
            extractZip(getAssets().open("htdocs.zip"), INSTALL_DIR, keepFiles);
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }
        log += "unzipped";
    }

    private void uploadOnClickListenerIntent() {
        startService(new Intent(this, XiDroidMainScreen.class));
        ((XiDroidApplication) getApplicationContext()).communication.uploadData();
    }

    private void updateRemoteServerOnClickListenerIntent() {
        startService(new Intent(this, XiDroidMainScreen.class));
        ((XiDroidApplication) getApplicationContext()).communication.checkForUpdatesRemoteServer();
    }

    private void extractZip(InputStream zipIs, String dest, HashMap<String, Integer> keepFiles) {
        Context context = this;
        context.startService(new Intent(context, XiDroidMainScreen.class));
        XiDroidUnzipTaskXiDroid task = XiDroidUnzipTaskXiDroid.createForConnect(this, context, zipIs, dest, true);
        task.execute();
    }

    public void processFinish(String dest) {
        if (dest != null && dest.equals(((XiDroidApplication) getApplicationContext()).appLocation)) { // /* == "/data/data/xi.xidroid"*/){  //done isntalling services
            phpInstallListenerIntent();
        } else if (dest != null && dest.equals(((XiDroidApplication) getApplicationContext()).sdcardLocation)) { //  == "/sdcard/xidroid/htdocs"){ //start service
            startServiceOnClickListenerIntent();
        } else {  //start webservice done!
            //String url = "http://localhost:8080/xi/?clear=1";
            //webView.loadUrl(url);
            //CREATE TABLES IF NOT EXISTS
            ((XiDroidApplication) getApplicationContext()).communication.createTableIfNotExists("[\"location\",\"appdata\"]");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xi_droid_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) { //home arrow right clicked
            homeOnClickListenerIntent();
        }
        if (id == R.id.action_browse) {
            homeOnClickListenerIntent();
            return true;
        }
        if (id == R.id.action_upload) {
            uploadOnClickListenerIntent();
            return true;
        }
        if (id == R.id.action_about) {

            String android_id = ((XiDroidApplication) getApplicationContext()).androidId; //Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            String aboutStr = "XiDroid version: ";
            try {
                aboutStr = aboutStr + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                aboutStr = aboutStr + "unknown";
            }
            aboutStr = aboutStr + "\nPhone id: " + android_id + "\n";
            aboutStr = aboutStr + "\nCopyright (C) 2014 Albert Weerman\n\nThis library/program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.";


            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage(aboutStr)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();


            return true;
        }
        if (id == R.id.action_admin) {
            if (((XiDroidApplication) getApplicationContext()).adminEntries == 0) { //only log in once
                ((XiDroidApplication) getApplicationContext()).adminEntries++;
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Please enter password");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setSingleLine(true);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        if (((XiDroidApplication) getApplicationContext()).settings.checkPassword(value.toString())) {
                            startAdminScreen();
                        } else {
                            Toast.makeText(getBaseContext(), "Invalid password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            } else {
                startAdminScreen();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
/*        File file = new File("/data/data/xi.xidroid/services/mysql/sbin/mysqld");
        if(!file.exists()){  //don't show if mysql is there already
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(false);
            menu.getItem(3).setEnabled(false);
        }
        else {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);
        }*/
        return true;
    }


    private class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }


    private class chromeViewClient extends WebChromeClient {
/*
        @Override
        public void onPageFinished(WebView view, String url)
        {
            view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        }
*/

        @Override
        public void onReceivedTitle(WebView view, String title) {

        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            if (message.equals("end")) {
                webView.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), ((XiDroidApplication) getApplicationContext()).settings.texts.getText("surveyCompletedMessage"), Toast.LENGTH_LONG).show();
            }
            result.cancel();
            return true;
        }

        ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
/*
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if (result.equals("startserver")){
                    startServiceOnClickListenerIntent();
                }
                else if(result.equals("checkupdates")){
                    updateRemoteServerOnClickListenerIntent();
                }
                else if(result.equals("installserver")){
                    serviceInstallListenerIntent();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }*/

        if (resultCode == RESULT_OK) {
            handleSmallCameraPhoto(data);
        }


    }//onActivityResult

    @Override
    public void onClick(View v) {

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.toolbar: {
                homeOnClickListenerIntent();
                break;
            }

            case R.id.adminerBtn: {
                // do something for button 1 click
                final Intent intent = new Intent(this, XiDroidBrowserScreen.class);
                intent.putExtra("page", "http://localhost:10000?username=root");
                startActivity(intent);
                break;
            }
            case R.id.restartBtn: { //restart server
                startServiceOnClickListenerIntent();
                break;
            }
            case R.id.updateBtn: { //check for updates
                updateRemoteServerOnClickListenerIntent();
                break;
            }
            case R.id.installBtn: { //install server
                serviceInstallListenerIntent();
                break;
            }
            case R.id.gpsBtn: {
                startGpsScreen();
                break;
            }
            case R.id.surveyBtn: {
                startSurveyScreen();
                break;
            }
            case R.id.reportInBtn: {
                reportInScreen();
                break;
            }
            case R.id.reportInSubmitBtn: {
                dispatchReportInSubmitIntent();
                break;
            }
            case R.id.cameraBtn: {
                startCamera();
                break;
            }
            case R.id.microphoneBtn: {
                dispatchRecordIntent();
                break;
            }
            case R.id.infoBtn: {
                dispatchInfoIntent();
                break;
            }
            case R.id.emaBtn: {
                AskQuestionIntent();
                break;
            }

        }
    }

    public void dispatchReportInSubmitIntent(){
        int ceid = 21; //this needs to be unique so we can tie them together
        EditText openEnded = (EditText) findViewById(R.id.openEndedText);
        if (mImageBitmap != null){
            try {
                String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + XiDroidFunctions.showDateTimeFile() + "_" + Integer.toString(ceid) + ".jpg";
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, ((XiDroidApplication) getApplicationContext()).settings.pictureCompression, out);
                OutputStream outputStreamFile = new FileOutputStream(fileName);
                out.writeTo(outputStreamFile);

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("primkey", ((XiDroidApplication) getApplicationContext()).androidId);
                parameters.put("ceid", "" + ceid);
                parameters.put("filetype", "jpg");
                parameters.put("screen", "reportin");
                parameters.put("ts", XiDroidFunctions.showDateTimeDatabase());

                ((XiDroidApplication) getApplicationContext()).communication.uploadFile(fileName, parameters);

                mImageBitmap = null; //RESET
            }
            catch (Exception e){
                //could not upload picture
            }
        }
        if (soundrecorded && soundFileName != null){
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("primkey", ((XiDroidApplication) getApplicationContext()).androidId);
            parameters.put("ceid", "" + ceid);
            parameters.put("filetype", "3gp");
            parameters.put("screen", "reportin");
            parameters.put("ts", XiDroidFunctions.showDateTimeDatabase());

            ((XiDroidApplication) getApplicationContext()).communication.uploadFile(soundFileName, parameters);

            soundrecorded = false; //RESET
        }
        if (openEnded.getText().length() > 0){
            //some text has been entered

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("primkey", ((XiDroidApplication) getApplicationContext()).androidId);
            parameters.put("ceid", "" + ceid);
            parameters.put("screen", "reportin");
            parameters.put("openended", "" + openEnded.getText());
            parameters.put("ts", XiDroidFunctions.showDateTimeDatabase());

            Gson gson = new Gson();
            String query = "INSERT INTO appdata (primkey, record) VALUES ('" + ((XiDroidApplication)getApplicationContext()).androidId + "', '" + gson.toJson(parameters) + "')";
            ((XiDroidApplication) getApplicationContext()).communication.runLocalQuery(query);

//            ((XiDroidApplication) getApplicationContext()).communication.uploadJson(this, parameters);

            openEnded.setText(""); //RESET

        }

        homeOnClickListenerIntent();
    }

    public void dispatchRecordIntent() {
        if (!recording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void stopRecording() {
        if (recording) {
            final ImageButton btn = (ImageButton) findViewById(R.id.microphoneBtn);
            btn.clearAnimation();
            btn.setImageResource(R.mipmap.ic_launcher_mic_check);
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            recording = false;
            soundrecorded = true;
        }

    }

    public void startRecording() {
        if (!recording) {
            final Animation animation = new AlphaAnimation(1.0f, 0.3f); // Change alpha from fully visible to invisible
            animation.setDuration(500); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            final ImageButton btn = (ImageButton) findViewById(R.id.microphoneBtn);
            btn.startAnimation(animation);

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            soundFileName = Environment.getExternalStorageDirectory().getPath() + "/" + "sound" + ".3gp";
            mRecorder.setOutputFile(soundFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                //       Log.e(LOG_TAG, "prepare() failed");
            }
            mRecorder.start();
            recording = true;
        }
    }


    public void startCamera() {
/*
        final Intent intent = new Intent(this, XiDroidBrowserScreen.class);
      //  https://simpl.info/getusermedia/sources/
        intent.putExtra("page", "http://localhost:8080/index.php");
        //intent.putExtra("page", "https://simpl.info/getusermedia/sources/");
        startActivity(intent);
*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_S);


    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        ImageButton cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        cameraBtn.setImageResource(R.mipmap.ic_launcher_camera_check);
    }

    public void AskQuestionIntent(){
        getSupportActionBar().setTitle("XiDroid EMA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setScreen(questionLayout);

        LinearLayout temp = (LinearLayout) findViewById(R.id.questionLayout);
        TextView tv = new TextView(this);
        tv.setText("How are you feeling right now?");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tv.setPadding(0, 0, 0, 30);
        temp.addView(tv);



        dynamicQuestions.add(new XiDroidDynamicQuestion(XiDroidDynamicQuestion.Q_RADIOBUTTON_SCALE, 1));
        dynamicQuestions.get(0).createSliderScale(this, R.id.questionLayout, "q1", "not stressed", "very stressed");
        dynamicQuestions.add(new XiDroidDynamicQuestion(XiDroidDynamicQuestion.Q_RADIOBUTTON_SCALE, 2));
        dynamicQuestions.get(1).createSliderScale(this, R.id.questionLayout, "q2", "not worried", "very worried");
        dynamicQuestions.add(new XiDroidDynamicQuestion(XiDroidDynamicQuestion.Q_RADIOBUTTON_SCALE, 3));
        dynamicQuestions.get(2).createSliderScale(this, R.id.questionLayout, "q3", "not panicked", "very panicked");
        dynamicQuestions.add(new XiDroidDynamicQuestion(XiDroidDynamicQuestion.Q_RADIOBUTTON_SCALE, 4));
        dynamicQuestions.get(3).createSliderScale(this, R.id.questionLayout, "q4", "not anxious", "very anxious");
        dynamicQuestions.add(new XiDroidDynamicQuestion(XiDroidDynamicQuestion.Q_RADIOBUTTON_SCALE, 5));
        dynamicQuestions.get(4).createSliderScale(this, R.id.questionLayout, "q5", "happy", "unhappy");


        RelativeLayout rLayout = new RelativeLayout(this);

        Button submitButton = new Button(this);
        submitButton.setText(" SUBMIT >> ");
        submitButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        submitButton.setBackgroundResource(R.drawable.xidroid_buttons);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnswerQuestionIntent(v);
                // Perform action on click
            }
        });
        temp.addView(submitButton);
/*

    <RelativeLayout
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" >


        /<Button
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=" SUBMIT >> "
        android:id="@+id/reportInSubmitBtn"
        android:textColor="@color/colorPrimary"
        android:textStyle="bold"
        android:background="@drawable/xidroid_buttons"

                />*/


    }

    public void AnswerQuestionIntent(View v){
        double answer1 = dynamicQuestions.get(0).getAnswer();
        double answer2 = dynamicQuestions.get(1).getAnswer();
        double answer3 = dynamicQuestions.get(2).getAnswer();

        double answer4 = dynamicQuestions.get(3).getAnswer();
        double answer5 = dynamicQuestions.get(4).getAnswer();

        if (answer1 == -1 || answer2 == -1 || answer3 == -1 || answer4 == -1 || answer5 == -1){
            Toast.makeText(this.getBaseContext(), ((XiDroidApplication)getApplicationContext()).settings.texts.getText("pleaseAnswerAllQuestionsMessage"), Toast.LENGTH_LONG).show();
        }
        else { //save and move away
            LinearLayout temp = (LinearLayout) findViewById(R.id.questionLayout);
            temp.removeAllViewsInLayout();
            dynamicQuestions.clear();
            homeOnClickListenerIntent();
        }

    }



}