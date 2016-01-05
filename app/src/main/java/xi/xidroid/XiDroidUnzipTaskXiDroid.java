package xi.xidroid;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by basweerman on 11/23/15.
 */
public class XiDroidUnzipTaskXiDroid extends XiDroidProgressDialogTask<String, String, String> {


    private List<String> mCommandList;
    private int command_executed;

    static XiDroidMainScreen delegate = null;


    static InputStream mZipfile;
    static String mDirectory;

    public XiDroidUnzipTaskXiDroid(Context context, String title, String message, boolean createDialog) {
        super(context, title, message, createDialog);
    }

    public XiDroidUnzipTaskXiDroid(Context context, int titleResId, int messageResId, boolean createDialog) {
        super(context, context.getString(titleResId), context.getString(messageResId), createDialog);
    }


    public static XiDroidUnzipTaskXiDroid createForConnect(XiDroidMainScreen droidm, final Context c, final InputStream zipname, final String directory, final boolean createDialog) {
        delegate = droidm;
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(String.format("unzip"));
            }
        });
        XiDroidUnzipTaskXiDroid task = new XiDroidUnzipTaskXiDroid(c, R.string.installing, R.string.installing_core_apps, createDialog);
        task.addCommand(command);

        mZipfile = zipname;
        mDirectory = directory;
        task.setNotification(R.string.unzipping_started);
        return task;

    }

    public XiDroidUnzipTaskXiDroid addCommand(List<String> commandList) {
        this.mCommandList = commandList;
        return this;
    }

    protected XiDroidUnzipTaskXiDroid setNotification(int resId) {
        command_executed = resId;
        return this;
    }

    @Override
    protected String doInBackground(String... params) {
        //return null;
        checkFilesystem();
        String command[] = mCommandList.toArray(new String[mCommandList.size()]);
//        String shell = enableSU ? "su" : "sh";
//        List<String> res = Shell.run(shell, command, null, true);
//        for (String queryRes : res)
//            publishProgress(queryRes);
//        publishProgress(COMMAND_EXECUTED);


//BEGIN ZIP
        HashMap keepFiles = new HashMap();
        String dest = mDirectory;
    try {
        if(!dest.endsWith("/")) {
            dest = dest + "/";
        }
        InputStream zipIs = mZipfile;
        boolean e = true;
        byte[] buf = new byte[8192];
        ZipInputStream zipinputstream = null;
        zipinputstream = new ZipInputStream(zipIs);
        ZipEntry zipentry = zipinputstream.getNextEntry();

        while(true) {
            while(zipentry != null) {
                String entryName = dest + zipentry.getName();
                entryName = entryName.replace('/', File.separatorChar);
                entryName = entryName.replace('\\', File.separatorChar);
                File newFile = new File(entryName);
                if(keepFiles.get(zipentry.getName()) != null && newFile.exists()) {
                    //  Log.d(TAG, "File not overwritten: " + zipentry.getName());
                    zipentry = zipinputstream.getNextEntry();
                } else if(zipentry.isDirectory()) {
                    newFile.mkdirs();
                    zipentry = zipinputstream.getNextEntry();
                } else {
                    if(!(new File(newFile.getParent())).exists()) {
                        (new File(newFile.getParent())).mkdirs();
                    }

                    FileOutputStream fileoutputstream = new FileOutputStream(entryName);

                    int n;
                    while((n = zipinputstream.read(buf, 0, 8192)) > -1) {
                        fileoutputstream.write(buf, 0, n);
                    }
                    publishProgress(zipentry.getName());
                    fileoutputstream.close();
                    zipinputstream.closeEntry();
                    zipentry = zipinputstream.getNextEntry();



                }
            }

            zipinputstream.close();
            break;
        }
    } catch (Exception var11) {

        // Log.e(TAG, var11.getMessage());
    }

    ///ENDUNZIP






        return null;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dismissProgress();
        delegate.processFinish(mDirectory);

    }

    @Override
    protected void onProgressUpdate(String... queryRes) {
        super.onProgressUpdate(queryRes);
        setMessage(queryRes[0]);
//        if (queryRes[0].equals(COMMAND_EXECUTED)) {
  //          command_executed = (command_executed == 0) ? R.string.command_executed : command_executed;
//            AppController.toast(getContext(), getContext().getString(command_executed));
    //    }
    }


    protected void checkFilesystem() {

/*        String test = Environment.getExternalStorageDirectory().getPath();
        File wallpaperDirectory = new File(test + "/xidroid");
        wallpaperDirectory.mkdirs();
        if (!wallpaperDirectory.mkdirs()) {
            Log.e("TravellerLog :: ", "Problem creating Image folder");
        }
        wallpaperDirectory = new File(test + "/xidroid/logs");
        wallpaperDirectory.mkdirs();*/


         List<String> listFiles = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("/logs");
                add("/sessions");
                add("/htdocs");
                add("/tmp");
            }
         });
         for (String filePath : listFiles) {
            File f = new File("/sdcard/xidroid".concat(filePath));
            if (!f.exists()) f.mkdirs();
         }
    }

}
