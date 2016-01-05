package xi.xidroid;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by basweerman on 12/8/15.
 */
public class XiDroidDynamicQuestion {

    public SeekBar seekBar;
    public boolean seekBarClicked = false;
    public static final int Q_RADIOBUTTON_SCALE = 1;
    public static final int Q_OPENENDED = 2;
    private int type;
    private int questionNumber;


    public XiDroidDynamicQuestion(int type, int questionNumber){
        this.type = type;
        this.questionNumber = questionNumber;
    }

    public LinearLayout createSliderScale(Activity activity, int layoutId, String questionText, String rangeMinText, String rangeMaxText){
        int textSizeQuestion = 8;
        int textSizeRange = 8;
        LinearLayout row2 = (LinearLayout) activity.findViewById(layoutId);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) row2.getLayoutParams();
        lp.width= RelativeLayout.LayoutParams.FILL_PARENT;
        LinearLayout labels = new LinearLayout(activity);
        labels.setLayoutParams(lp);
        //RANGE MIN
        TextView rangeMin = new TextView(activity);
        rangeMin.setText(rangeMinText);
        rangeMin.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        rangeMin.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSizeQuestion, activity.getResources().getDisplayMetrics()));
        labels.addView(rangeMin);
        //RANGE MAX
        TextView rangeMax = new TextView(activity);
        rangeMax.setText(rangeMaxText);
        rangeMax.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        rangeMax.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSizeQuestion, activity.getResources().getDisplayMetrics()));
        rangeMax.setWidth(2000);
        rangeMax.setGravity(Gravity.RIGHT);

        labels.addView(rangeMax);

        //ADD answercategories
        LinearLayout answers = new LinearLayout(activity);
        answers.setLayoutParams(lp);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 30);   // was 0,10,0,40

        seekBar = new SeekBar(activity);




        //lp.setMargins(0, 100, 0, 0);
        // RelativeLayout.LayoutParams lp2 = (LayoutParams) answers.getLayoutParams();
        //  lp2.setMargins(0, 100, 0, 0);
        seekBar.setLayoutParams(layoutParams);
        //seekBar.setProgressDrawable(new ColorDrawable(R.color.colorPrimary));
        Drawable thumb = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            thumb = seekBar.getThumb();
            thumb.mutate().setAlpha(0);
        }

        // bar.setOnClickListener(mainBtnOnClickListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                Drawable thumb = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    thumb = seekBar.getThumb();
                    thumb.mutate().setAlpha(255);
                }
                seekBarClicked = true;
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        LayoutParams lp2 = (LayoutParams) seekBar.getLayoutParams();
//        lp2.topMargin = 500;

        answers.addView(seekBar);

        //add to row2
        row2.addView(labels);


        row2.addView(answers);

        return row2;
    }


}
