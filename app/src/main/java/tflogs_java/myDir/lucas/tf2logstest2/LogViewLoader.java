package tflogs_java.myDir.lucas.tf2logstest2;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lucas.tf2logstest2.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lucas on 8/15/2016.
 * Display a log page
 */
public class LogViewLoader extends AppCompatActivity {

    private static int sCount = 0;
    private static final int rht = 40;
    private static final int nwd = 140;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#444444")));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SetMyColor.setMyColor(this);
        }
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        String logID = intent.getStringExtra("logID");
        final String mapName = intent.getStringExtra("mapName");
        final String logTitle = intent.getStringExtra("logTitle");


        getSupportActionBar().setTitle(logTitle);
        getSupportActionBar().setSubtitle(mapName);

        String url = "http://logs.tf/json/" + logID;

        final TextView bluScore = (TextView) findViewById(R.id.logview_bluscore);
        final TextView redScore = (TextView) findViewById(R.id.logview_redscore);

        /*
        final View titleViewPad = findViewById(R.id.logview_titlepad);
        final TextView titleView = (TextView) findViewById(R.id.logview_title);
        final TextView mapView = (TextView) findViewById(R.id.logview_map);
        final View mapViewPad = findViewById(R.id.logview_mappad);
        */

        final TableLayout header = (TableLayout) findViewById(R.id.logview_tableHeader);
        final TableLayout board = (TableLayout) findViewById(R.id.logview_table);

        //instantiate the request queue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //parse JSON to display log
                        //important player data: class, K, A, D, DA/M, DT/M

                        //convert string into json object
                        try {
                            //entire json object from http response
                            JSONObject src = new JSONObject(response);

                            //players and their individual statistics
                            JSONObject origPlayers = src.getJSONObject("players");

                            //player steam IDs matched to names
                            JSONObject names = src.getJSONObject("names");

                            //sorted players by team and class
                            JSONObject players = orderPlayers(origPlayers, names);

                            //team overall statistics- used for score
                            JSONObject teams = src.getJSONObject("teams");

                            /*
                            titleViewPad.setBackgroundColor(Color.DKGRAY);
                            titleView.setText(logTitle);
                            titleView.setBackgroundColor(Color.DKGRAY);
                            mapView.setText(mapName);
                            mapView.setBackgroundColor(Color.DKGRAY);
                            mapViewPad.setBackgroundColor(Color.DKGRAY);
                            */
                            bluScore.setText(Integer.toString(teams.getJSONObject("Blue").getInt("score")));
                            redScore.setText(Integer.toString(teams.getJSONObject("Red").getInt("score")));



                            header.addView(addSpacerRow());
                            header.addView(addHeaderRow());
                            header.addView(addSpacerRow());

                            BestStatsHolder bsh = new BestStatsHolder(players, names);
                            WorstStatsHolder wsh = new WorstStatsHolder(players,names);

                            Iterator itr = players.keys();
                            while(itr.hasNext()) {
                                String key = (String) itr.next();
                                String nameStr = names.getString(key);
                                board.addView(addRow(players.getJSONObject(key), nameStr, bsh, wsh));
                                board.addView(addSpacerRow());
                            }
                        }
                        catch(JSONException e) {
                            Log.e("JSON", "JSON object construction failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "Log json request failed");

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private TableRow addRow(JSONObject player, String pName, BestStatsHolder bsh, WorstStatsHolder wsh) {
        //player data in each row: name, class, K, A, D, DA/M, DT/M
        TableRow tr = new TableRow(this);
        tr.setId(1000 + sCount);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        Configuration configuration = this.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;

        int dpLeftWidth = screenWidthDp - nwd - rht - 6*2;
        TableRow.LayoutParams colParams = new TableRow.LayoutParams(useDip(dpLeftWidth/5),
                useDip(rht));

        //name column
        final TextView nameView = new TextView(this);
        nameView.setLayoutParams(new TableRow.LayoutParams(useDip(nwd), TableRow.LayoutParams.MATCH_PARENT));
        try {
            if (player.getString("team").equals("Blue")) {
                nameView.setBackgroundColor(Color.parseColor("#5B818F"));
            } else if (player.getString("team").equals("Red")) {
                nameView.setBackgroundColor(Color.parseColor("#A75D50"));
            } else {
                Log.e("JSON", "Failed to find proper team for a player in a new row");
                return null;
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to add row for a player- name failure");
            return null;
        }
        nameView.setTextColor(Color.WHITE);
        //TODO: indicate (perhaps yellow text?) whether this is the player from the previous profile
        nameView.setText(pName);
        nameView.setGravity(Gravity.CENTER);
        tr.addView(nameView);

        tr.addView(addSpacerColumn());

        //image column
        final ImageView classView = new ImageView(this);
        classView.setLayoutParams(new TableRow.LayoutParams(useDip(rht), useDip(rht)));
        classView.setBackgroundColor(Color.LTGRAY);
        classView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        try {
            String pClass = player.getJSONArray("class_stats").getJSONObject(0).getString("type");
            switch (pClass) {
                case "scout":
                    classView.setImageResource(R.drawable.classicon_scout);
                    break;
                case "soldier":
                    classView.setImageResource(R.drawable.classicon_soldier);
                    break;
                case "pyro":
                    classView.setImageResource(R.drawable.classicon_pyro);
                    break;
                case "demoman":
                    classView.setImageResource(R.drawable.classicon_demoman);
                    break;
                case "heavyweapons":
                    classView.setImageResource(R.drawable.classicon_heavyweapons);
                    break;
                case "engineer":
                    classView.setImageResource(R.drawable.classicon_engineer);
                    break;
                case "medic":
                    classView.setImageResource(R.drawable.classicon_medic);
                    break;
                case "sniper":
                    classView.setImageResource(R.drawable.classicon_sniper);
                    break;
                case "spy":
                    classView.setImageResource(R.drawable.classicon_spy);
                    break;
                default:
                    classView.setImageResource(R.drawable.default_format);
            }
        } catch(JSONException e) {
            Log.e("JSON", "Failed to get most-played class");
            classView.setImageResource(R.drawable.default_format);
        }
        tr.addView(classView);

        tr.addView(addSpacerColumn());

        //kill view
        final TextView kView = new TextView(this);
        kView.setLayoutParams(colParams);
        kView.setTextColor(Color.BLACK);
        try {
            kView.setText(Integer.toString(player.getInt("kills")));
            if(player.getInt("kills") >= bsh.getbK()) {
                kView.setBackgroundColor(Color.parseColor("#afd9af"));
            } else if(player.getInt("kills") <= wsh.getwK() && player.getInt("classpos") != 7) {
                kView.setBackgroundColor(Color.parseColor("#d9afaf"));
            } else {
                kView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse Kills for player");
            kView.setText("ERR");
            kView.setBackgroundColor(Color.LTGRAY);
        }
        kView.setGravity(Gravity.CENTER);
        tr.addView(kView);

        tr.addView(addSpacerColumn());

        //assist view
        final TextView aView = new TextView(this);
        aView.setLayoutParams(colParams);
        aView.setTextColor(Color.BLACK);
        try {
            aView.setText(Integer.toString(player.getInt("assists")));
            if(player.getInt("assists") >= bsh.getbA()) {
                aView.setBackgroundColor(Color.parseColor("#afd9af"));
            } else if(player.getInt("assists") <= wsh.getwA() ) {
                aView.setBackgroundColor(Color.parseColor("#d9afaf"));
            } else {
                aView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse assists for player");
            aView.setText("ERR");
            aView.setBackgroundColor(Color.LTGRAY);
        }
        aView.setGravity(Gravity.CENTER);
        tr.addView(aView);

        tr.addView(addSpacerColumn());

        //death view
        final TextView dView = new TextView(this);
        dView.setLayoutParams(colParams);
        dView.setTextColor(Color.BLACK);
        try {
            dView.setText(Integer.toString(player.getInt("deaths")));
            if(player.getInt("deaths") <= bsh.getbD()) {
                dView.setBackgroundColor(Color.parseColor("#afd9af"));
            } else if(player.getInt("deaths") >= wsh.getwD()) {
                dView.setBackgroundColor(Color.parseColor("#d9afaf"));
            } else {
                dView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse deaths for player");
            dView.setText("ERR");
            dView.setBackgroundColor(Color.LTGRAY);
        }
        dView.setGravity(Gravity.CENTER);
        tr.addView(dView);

        tr.addView(addSpacerColumn());

        //da/m view
        final TextView damView = new TextView(this);
        damView.setLayoutParams(colParams);
        damView.setTextColor(Color.BLACK);
        try {
            damView.setText(Integer.toString(player.getInt("dapm")));
            if(player.getInt("dapm") >= bsh.getbDam()) {
                damView.setBackgroundColor(Color.parseColor("#afd9af"));
            } else if(player.getInt("dapm") <= wsh.getwDam() && player.getInt("classpos") != 7) {
                damView.setBackgroundColor(Color.parseColor("#d9afaf"));
            } else {
                damView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse da/m for player");
            damView.setText("ERR");
            damView.setBackgroundColor(Color.LTGRAY);
        }
        damView.setGravity(Gravity.CENTER);
        tr.addView(damView);

        tr.addView(addSpacerColumn());

        //dt/m view
        final TextView dtmView = new TextView(this);
        dtmView.setLayoutParams(colParams);
        dtmView.setTextColor(Color.BLACK);
        try {
            if(player.getInt("dmg") != 0) {
                int num1 = player.getInt("dapm");
                int num2 = player.getInt("dmg");
                double rate = num1 * 1.0 / num2;
                dtmView.setText(Integer.toString( (int) (player.getInt("dt") * rate)));
                if( (int) (player.getInt("dt") * rate) <= bsh.getbDtm() && player.getInt("classpos") != 7) {
                    dtmView.setBackgroundColor(Color.parseColor("#afd9af"));
                } else if( (int) (player.getInt("dt") * rate) >= wsh.getwDtm()) {
                    dtmView.setBackgroundColor(Color.parseColor("#d9afaf"));
                } else {
                    dtmView.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                dtmView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse dt/m for player");
            dtmView.setText("ERR");
            dtmView.setBackgroundColor(Color.LTGRAY);
        }
        dtmView.setGravity(Gravity.CENTER);
        tr.addView(dtmView);

        //end padding
        final View padView = new View(this);
        padView.setLayoutParams(new TableRow.LayoutParams(useDip(50), useDip(rht)));
        try {
            if(player.getInt("dmg") != 0) {
                int num1 = player.getInt("dapm");
                int num2 = player.getInt("dmg");
                double rate = num1 * 1.0 / num2;
                if( (int) (player.getInt("dt") * rate) <= bsh.getbDtm() && player.getInt("classpos") != 7) {
                    padView.setBackgroundColor(Color.parseColor("#afd9af"));
                } else if( (int) (player.getInt("dt") * rate) >= wsh.getwDtm()) {
                    padView.setBackgroundColor(Color.parseColor("#d9afaf"));
                } else {
                    padView.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                padView.setBackgroundColor(Color.LTGRAY);
            }
        }
        catch(JSONException e) {
            Log.e("JSON", "Failed to parse dt/m for player");
            padView.setBackgroundColor(Color.LTGRAY);
        }
        tr.addView(padView);

        sCount++;
        return tr;
    }

    private TableRow addHeaderRow() {
        //title header for each row: name, class, K, A, D, DA/M, DT/M
        TableRow tr = new TableRow(this);
        tr.setId(1000 + sCount);

        Configuration configuration = this.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;

        //tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        int dpLeftWidth = screenWidthDp - nwd - rht - 6*2;
        TableRow.LayoutParams colParams = new TableRow.LayoutParams(useDip(dpLeftWidth/5),
                useDip(rht));



        //name column
        final TextView nameView = new TextView(this);
        nameView.setLayoutParams(new TableRow.LayoutParams(useDip(nwd), useDip(rht)));
        nameView.setTextColor(Color.WHITE);
        nameView.setText("Player");
        nameView.setBackgroundColor(Color.DKGRAY);
        nameView.setGravity(Gravity.CENTER);
        tr.addView(nameView);

        tr.addView(addSpacerColumn());

        //class column
        final TextView classView = new TextView(this);
        classView.setLayoutParams(new TableRow.LayoutParams(useDip(rht), useDip(rht)));
        classView.setTextColor(Color.WHITE);
        classView.setText("C");
        classView.setBackgroundColor(Color.DKGRAY);
        classView.setGravity(Gravity.CENTER);
        tr.addView(classView);

        tr.addView(addSpacerColumn());

        //K
        final TextView kView = new TextView(this);
        kView.setLayoutParams(colParams);
        kView.setTextColor(Color.WHITE);
        kView.setText("K");
        kView.setBackgroundColor(Color.parseColor("#444444"));
        kView.setGravity(Gravity.CENTER);
        tr.addView(kView);

        tr.addView(addSpacerColumn());

        //A
        final TextView aView = new TextView(this);
        aView.setLayoutParams(colParams);
        aView.setTextColor(Color.WHITE);
        aView.setText("A");
        aView.setBackgroundColor(Color.parseColor("#444444"));
        aView.setGravity(Gravity.CENTER);
        tr.addView(aView);

        tr.addView(addSpacerColumn());

        //D
        final TextView dView = new TextView(this);
        dView.setLayoutParams(colParams);
        dView.setTextColor(Color.WHITE);
        dView.setText("D");
        dView.setBackgroundColor(Color.parseColor("#444444"));
        dView.setGravity(Gravity.CENTER);
        tr.addView(dView);

        tr.addView(addSpacerColumn());

        //DA/M
        final TextView damView = new TextView(this);
        damView.setLayoutParams(colParams);
        damView.setTextColor(Color.WHITE);
        damView.setText("DA/M");
        damView.setBackgroundColor(Color.parseColor("#444444"));
        damView.setGravity(Gravity.CENTER);
        tr.addView(damView);

        tr.addView(addSpacerColumn());

        //DT/M
        final TextView dtmView = new TextView(this);
        dtmView.setLayoutParams(colParams);
        dtmView.setTextColor(Color.WHITE);
        dtmView.setText("DT/M");
        dtmView.setBackgroundColor(Color.parseColor("#444444"));
        dtmView.setGravity(Gravity.CENTER);
        tr.addView(dtmView);

        //end padding
        final View padView = new View(this);
        padView.setLayoutParams(new TableRow.LayoutParams(useDip(50), useDip(rht)));
        padView.setBackgroundColor(Color.parseColor("#444444"));
        tr.addView(padView);

        sCount++;

        return tr;
    }

    private TableRow addSpacerRow() {
        TableRow tr = new TableRow(this);

        tr.setMinimumHeight(useDip(2));
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(Color.WHITE);

        return tr;
    }

    private View addSpacerColumn() {
        View v = new View(this);

        v.setLayoutParams(new TableRow.LayoutParams(useDip(2), useDip(rht)));
        v.setBackgroundColor(Color.WHITE);

        return v;
    }

    private JSONObject orderPlayers(JSONObject src, JSONObject names) {
        //order by team, then by class (lower # first)
        JSONObject dest = new JSONObject();

            ArrayList<JSONObject> bluArr = new ArrayList<>();
            ArrayList<JSONObject> redArr = new ArrayList<>();

            Iterator<String> itr = names.keys();
            while(itr.hasNext()) {
                try {
                    String idStr = itr.next();
                    JSONObject pl = src.getJSONObject(idStr);
                    pl.put("lucas_steamid", idStr);
                    switch (pl.getJSONArray("class_stats").getJSONObject(0).getString("type")) {
                        case "scout":
                            pl.put("classpos", 1);
                            break;
                        case "soldier":
                            pl.put("classpos", 2);
                            break;
                        case "pyro":
                            pl.put("classpos", 3);
                            break;
                        case "demoman":
                            pl.put("classpos", 4);
                            break;
                        case "heavyweapons":
                            pl.put("classpos", 5);
                            break;
                        case "engineer":
                            pl.put("classpos", 6);
                            break;
                        case "medic":
                            pl.put("classpos", 7);
                            break;
                        case "sniper":
                            pl.put("classpos", 8);
                            break;
                        case "spy":
                            pl.put("classpos", 9);
                            break;
                        default:
                            pl.put("classpos", 10);
                            break;
                    }

                    boolean inserted = false;
                    if(pl.getString("team").equals("Blue")) {
                        if( !(bluArr.isEmpty()) ) {
                            int bluSizeHolder = bluArr.size();
                            for(int i = 0; i < bluSizeHolder; i++) {
                                if ( pl.getInt("classpos") <= bluArr.get(i).getInt("classpos")) {
                                    bluArr.add(i, pl);
                                    inserted = true;
                                    break;
                                }
                            }
                        }
                        if(!inserted) {
                            bluArr.add(pl);
                        }
                    }
                    if(pl.getString("team").equals("Red")) {
                        if( !(redArr.isEmpty()) ) {
                            int redSizeHolder = redArr.size();
                            for(int i = 0; i < redSizeHolder; i++) {
                                if ( pl.getInt("classpos") <= redArr.get(i).getInt("classpos")) {
                                    redArr.add(i, pl);
                                    inserted = true;
                                    break;
                                }
                            }
                        }
                        if(!inserted) {
                            redArr.add(pl);
                        }
                    }
                }
                catch(JSONException e) {
                    Log.e("JSON", "failed to order");
                    return src;
                }
            }


            for(int counter = 0; counter < bluArr.size(); counter++) {
                try {
                    dest.put(bluArr.get(counter).getString("lucas_steamid"), bluArr.get(counter));
                }
                catch(JSONException e) {
                    Log.e("JSON", "failed to find inserted steamid, failed to order");
                    return src;
                }
            }
            for(int counter2 = 0; counter2 < redArr.size(); counter2++) {
                try {
                    dest.put(redArr.get(counter2).getString("lucas_steamid"), redArr.get(counter2));
                }
                catch(JSONException e) {
                    Log.e("JSON", "failed to find inserted steamid, failed to order");
                    return src;
                }
            }
        return dest;
    }

    private int useDip(float px) {
        return (int)(px * getResources().getDisplayMetrics().density);
    }
}
