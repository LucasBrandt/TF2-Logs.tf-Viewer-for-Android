package tflogs_java.myDir.lucas.tf2logstest2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Lucas on 8/19/2016.
 * contains data for the "best" stats of all players in this log
 * for instance, the number of kills which was highest, deaths lowest, etc
 * then in addRow(), if the number an individual player has matches the "best", it gets a green background
 */
public class BestStatsHolder {
    private int bK;

    private int bA;

    private int bD;

    private int bDam;

    private int bDtm;

    public BestStatsHolder (JSONObject log, JSONObject names) {
        bK = 0;
        bA = 0;
        bD = 99999;
        bDam = 0;
        bDtm = 9999;

        Iterator itr = names.keys();

        while(itr.hasNext()) {
            try {
                JSONObject plr = log.getJSONObject((String) itr.next());
                if(plr.getInt("kills") >= bK) {
                    bK = plr.getInt("kills");
                }
                if(plr.getInt("assists") >= bA) {
                    bA = plr.getInt("assists");
                }
                if(plr.getInt("deaths") <= bD) {
                    bD = plr.getInt("deaths");
                }
                if(plr.getInt("dapm") >= bDam) {
                    bDam = plr.getInt("dapm");
                }
                if(plr.getInt("dmg") != 0 && plr.getInt("classpos") != 7) {
                    int num1 = plr.getInt("dapm");
                    int num2 = plr.getInt("dmg");
                    double rate = num1 * 1.0 / num2;
                    if( (int) (plr.getInt("dt") * rate) <= bDtm) {
                        bDtm = ( (int) (plr.getInt("dt") * rate) );
                    }
                }
            }
            catch(JSONException e) {
                Log.e("JSON", "failed to create best stats holder");
            }
        }

    }

    public int getbK() {
        return bK;
    }

    public int getbA() {
        return bA;
    }

    public int getbD() {
        return bD;
    }

    public int getbDam() {
        return bDam;
    }

    public int getbDtm() {
        return bDtm;
    }
}
