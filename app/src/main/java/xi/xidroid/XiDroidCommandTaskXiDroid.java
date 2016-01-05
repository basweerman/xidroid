package xi.xidroid;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

//import org.opendroidphp.app.AppController;
//import org.opendroidphp.app.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class XiDroidCommandTaskXiDroid extends XiDroidProgressDialogTask<String, String, String> {

    public final static String CHANGE_PERMISSION = "/system/bin/chmod 755 ";
    public final static String COMMAND_EXECUTED = "command_executed";

    private List<String> mCommandList;
    private boolean enableSU;
    private int command_executed;

    static XiDroidMainScreen delegate = null;

    public XiDroidCommandTaskXiDroid() {

    }

    public XiDroidCommandTaskXiDroid(Context context) {
        super(context);
    }

    public XiDroidCommandTaskXiDroid(Context context, String title, String message) {
        super(context, title, message);
    }
    
    public XiDroidCommandTaskXiDroid(Context context, String title, String message, boolean createDialog) {
        super(context, title, message, createDialog);
    }

    public XiDroidCommandTaskXiDroid(Context context, int titleResId, int messageResId) {
        super(context, context.getString(titleResId), context.getString(messageResId));
    }
    
    public XiDroidCommandTaskXiDroid(Context context, int titleResId, int messageResId, boolean createDialog) {
        super(context, context.getString(titleResId), context.getString(messageResId), createDialog);
    }

    public static XiDroidCommandTaskXiDroid createForConnect(XiDroidMainScreen droidm, final Context c) {
        return createForConnect(droidm, c, true);
    }
    public static XiDroidCommandTaskXiDroid createForConnect(XiDroidMainScreen droidm, final Context c, final boolean createDialog) {
        delegate = droidm;
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(CHANGE_PERMISSION.concat(((XiDroidApplication)c.getApplicationContext()).appLocation + "/start.sh"));
                add(String.format("%s/start.sh", ((XiDroidApplication) c.getApplicationContext()).appLocation));

                //add(CHANGE_PERMISSION.concat("/data/data/xi.xidroid" + "/start.sh"));
                //add(String.format("%s/start.sh %s %s", "/data/data/xi.xidroid", execName, bindPort));
            }
        });
        String test = "tes";
        File file = new File(((XiDroidApplication)c.getApplicationContext()).appLocation  + "/services/start.sh"); //+ "/data/data/xi.xidroid" + "/services/start.sh");
        if (file.exists()){
            test = test + "dd";
        }
        test = test + "dd";
        XiDroidCommandTaskXiDroid task = new XiDroidCommandTaskXiDroid(c, R.string.server_loading, R.string.turning_on_server, createDialog);
        task.addCommand(command);
        task.setNotification(R.string.web_server_is_running);
        return task;
    }

    public static XiDroidCommandTaskXiDroid createForDisconnect(XiDroidMainScreen droidm, final Context c) {
        delegate = droidm;
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(((XiDroidApplication) c.getApplicationContext()).appLocation + "stop.sh");
                //add("/data/data/xi.xidroid" + "/scripts/stop.sh");
            }
        });
        XiDroidCommandTaskXiDroid task = new XiDroidCommandTaskXiDroid(c, R.string.server_loading, R.string.turning_off_server);
        task.addCommand(command);
        return task;
    }

    public static XiDroidCommandTaskXiDroid createForUninstall(final Context c) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(String.format("rm -R %s", ((XiDroidApplication)c.getApplicationContext()).appLocation.concat("/")));
            }
        });
        XiDroidCommandTaskXiDroid task = new XiDroidCommandTaskXiDroid(c, R.string.uninstalling_core_apps, R.string.core_uninstalling);
        task.addCommand(command);
        task.setNotification(R.string.core_apps_uninstalled);
        return task;
    }

    @Override
    protected String doInBackground(String... cmdArgs) {
        checkFilesystem();
        String command[] = mCommandList.toArray(new String[mCommandList.size()]);
        String shell = enableSU ? "su" : "sh";
        List<String> res = Shell.run(shell, command, null, true);
        for (String queryRes : res)
            publishProgress(queryRes);
        publishProgress(COMMAND_EXECUTED);
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
        delegate.processFinish(s);
    }

    @Override
    protected void onProgressUpdate(String... queryRes) {
        super.onProgressUpdate(queryRes);
        setMessage(queryRes[0]);
        if (queryRes[0].equals(COMMAND_EXECUTED)) {
            command_executed = (command_executed == 0) ? R.string.command_executed : command_executed;
//            AppController.toast(getContext(), getContext().getString(command_executed));
        }
    }

    public XiDroidCommandTaskXiDroid addCommand(List<String> commandList) {
        this.mCommandList = commandList;
        return this;
    }

    public boolean isEnabledSU() {
        return enableSU;
    }

    public XiDroidCommandTaskXiDroid enableSU(final boolean enableSU) {
        this.enableSU = enableSU;
        return this;
    }

    public XiDroidCommandTaskXiDroid toggleEnableSU() {
        return enableSU(!enableSU);
    }

    protected XiDroidCommandTaskXiDroid setNotification(int resId) {
        command_executed = resId;
        return this;
    }

    protected void checkFilesystem() {
    /*    List<String> listFiles = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("/logs");
                add("/conf");
                add("/conf/nginx/logs");
                add("/hosts/nginx");
                add("/hosts/lighttpd");
                add("/sessions");
                add("/packages");
                add("/htdocs");
                add("/tmp");
            }
        });

        for (String filePath : listFiles) {
            File f = new File("/sdcard/zidroid".concat(filePath));
            if (!f.exists()) f.mkdirs();
        }*/
    }

}