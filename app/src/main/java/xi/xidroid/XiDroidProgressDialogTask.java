package xi.xidroid;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.TextView;


abstract public class XiDroidProgressDialogTask<ParameterT, ProgressT, ReturnT> extends AsyncTask<ParameterT, ProgressT, ReturnT> {

    protected Handler handler = new Handler(Looper.getMainLooper());

    private Dialog progress;
    private Context context;

    private TextView tv_title;
    private TextView tv_message;
    //private ProgressBar progressBar;

    private String title;
    private String message;

    public XiDroidProgressDialogTask() {

    }

    public XiDroidProgressDialogTask(Context context) {
        this(context, true);
    }
    
    public XiDroidProgressDialogTask(Context context, final boolean createDialog) {
    	this.context = context;
    	if(createDialog) {
	        handler.post(new Runnable() {
	            @Override
	            public void run() {
	                createDialog().show();
	            }
	        });
    	}
    }
    
    public XiDroidProgressDialogTask(Context context, String title, String message) {
    	this(context, title, message, true);
    }
    
    public XiDroidProgressDialogTask(Context context, String title, String message, boolean createDialog) {
        this(context, createDialog);
        this.title = title;
        this.message = message;
    }

    public XiDroidProgressDialogTask(Context context, int titleResId, int messageResId) {
        this(context, context.getString(titleResId), context.getString(messageResId));
    }
    
    public XiDroidProgressDialogTask(Context context, int titleResId, int messageResId, boolean createDialog) {
        this(context, context.getString(titleResId), context.getString(messageResId), createDialog);
    }

    protected Dialog createDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);

        progress = new Dialog(context, R.style.AppTheme); // R.style.Theme_DroidPHP_Dialog);
        progress.setContentView(inflater.inflate(R.layout.dialog_progress_holo, null));

        title = title != null ? title : "test"; //context.getString(R.string.core_apps);
        message = message != null ? message : "installing"; //context.getString(R.string.installing_core_apps);

        //Title View
        tv_title = (TextView) progress.findViewById(R.id.title);
        tv_title.setText(title);

        //Message View
        tv_message = (TextView) progress.findViewById(R.id.message);
        tv_message.setText(message);

        //progressBar = (ProgressBar) progress.findViewById(R.id.pb_progress);
        return progress;
    }

    public XiDroidProgressDialogTask setTitle(String title) {
        this.title = title;
        if(tv_title != null) {
        	tv_title.setText(title);
        }
        return this;
    }

    public XiDroidProgressDialogTask setMessage(String message) {
        this.message = message;
        if (tv_message != null) {
        	tv_message.setText(message);
        }
        return this;
    }

    /**
     * Dismiss the Dialog
     */
    protected void dismissProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public Context getContext() {
        return context;
    }

    protected Dialog getDialog() {
        return progress;
    }
}
