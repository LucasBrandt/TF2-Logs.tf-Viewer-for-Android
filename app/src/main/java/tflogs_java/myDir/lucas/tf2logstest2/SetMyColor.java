package tflogs_java.myDir.lucas.tf2logstest2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;

/**
 * Created by Lucas on 8/12/2016.
 * sets the status bar color to 800 version of primary 700 (tf2 blu)
 */
@TargetApi(21)
public abstract class SetMyColor {
    public static void setMyColor(Activity activity) {
        activity.getWindow().setStatusBarColor(Color.parseColor("#2b2b2b"));
        //#405B65
    }
}
