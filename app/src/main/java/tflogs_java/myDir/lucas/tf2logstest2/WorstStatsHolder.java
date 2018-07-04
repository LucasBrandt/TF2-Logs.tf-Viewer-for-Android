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
public class WorstStatsHolder {
    private int wK;

    private int wA;

    private int wD;

    private int wDam;

    private int wDtm;

    public WorstStatsHolder (JSONObject log, JSONObject names) {
        wK = 9999;
        wA = 9999;
        wD = 0;
        wDam = 9999;
        wDtm = 0;

        Iterator itr = names.keys();

        while(itr.hasNext()) {
            try {
                JSONObject plr = log.getJSONObject((String) itr.next());
                if(plr.getInt("kills") <= wK && plr.getInt("classpos") != 7) {
                    wK = plr.getInt("kills");
                }
                if(plr.getInt("assists") <= wA) {
                    wA = plr.getInt("assists");
                }
                if(plr.getInt("deaths") >= wD) {
                    wD = plr.getInt("deaths");
                }
                if(plr.getInt("dapm") <= wDam && plr.getInt("classpos") != 7) {
                    wDam = plr.getInt("dapm");
                }
                if(plr.getInt("dmg") != 0) {
                    int num1 = plr.getInt("dapm");
                    int num2 = plr.getInt("dmg");
                    double rate = num1 * 1.0 / num2;
                    if( (int) (plr.getInt("dt") * rate) >= wDtm) {
                        wDtm = ( (int) (plr.getInt("dt") * rate) );
                    }
                }
            }
            catch(JSONException e) {
                Log.e("JSON", "failed to create best stats holder");
            }
        }

    }

    public int getwK() {
        return wK;
    }

    public int getwA() {
        return wA;
    }

    public int getwD() {
        return wD;
    }

    public int getwDam() {
        return wDam;
    }

    public int getwDtm() {
        return wDtm;
    }
}
