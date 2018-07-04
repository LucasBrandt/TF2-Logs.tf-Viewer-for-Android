package tflogs_java.myDir.lucas.tf2logstest2;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lucas.tf2logstest2.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 7/10/2016.
 * Loads a profile of a single player
 */
public class ProfileViewLoader extends AppCompatActivity {
    ListView listView;
    int numPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SetMyColor.setMyColor(this);
        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#444444")));

        //#5B818F
        //#A75D50

        //TODO: a button that allows you to save this player as "favorite" to be accessed from FAB in main activity
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // ...
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if(intent == null) {
            return;
        }
        if(intent.getAction() == null) {
            return;
        }
        if ((intent.getAction()).equals("TFLOGS_LOAD_PROFILE")) {
            numPages = 1;
            final String id = intent.getStringExtra("profileID");
            final String name = intent.getStringExtra("name");
            final String numLogs = intent.getStringExtra("numLogs");

            String url = "http://logs.tf/profile/" + id;

            if(name != null) {
                getSupportActionBar().setTitle(name);
            }
            if(numLogs != null) {
                getSupportActionBar().setSubtitle(numLogs + " logs");
            }

            //instantiate the request queue.
            final RequestQueue queue = Volley.newRequestQueue(this);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //PARSE RESPONSE TO DISPLAY LOGS ON PROFILE
                            int placeholder = response.indexOf("<table class=\"table loglist\">");
                            int listStart = response.indexOf("<tbody>\t\t\t\t", placeholder);
                            int listEnd = response.indexOf("\t\t\t\t\t</tbody>", listStart + 11);
                            String profileLogsList = response.substring(listStart + 11, listEnd);

                            //split into <tr> attributes
                            String linesTemp[] = profileLogsList.split("\n\t\t\t\t\t\t\t");
                            final ArrayList<LogListResult> results = new ArrayList<>();

                            for(String one : linesTemp) {
                                results.add(new LogListResult(one.replaceAll("\t", "").replaceAll("\n", "")));
                            }


                            // Get ListView object from xml
                            listView = (ListView) findViewById(R.id.profile_logs_list);

                            // Define a new Adapter
                            // First parameter - Context
                            // Second parameter - Layout for the row
                            // Third parameter - ID of the TextView to which the data is written
                            // Forth - the Array of data

                            final LogListAdapter adapter = new LogListAdapter(ProfileViewLoader.this, results);


                            // Assign adapter to ListView
                            listView.setAdapter(adapter);


                            // Attach the listener to the AdapterView onCreate
                            listView.setOnScrollListener(new InfiniteScrollListener(5) {
                                @Override
                                public void loadMore(int page, int totalItemsCount) {
                                    String requestUrl = new String("http://logs.tf/profile/" + id + "?p=" + page);
                                    // Request a string response from the provided URL.
                                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    //check if response is from logs.tf homepage- indicating that this is one page too many
                                                    int checkStop1 = response.indexOf("<meta property=\"og:url\" content=\"") + "<meta property=\"og:url\" content=\"".length();
                                                    String urlFrom = response.substring(checkStop1, (response.indexOf("\">", checkStop1) ));
                                                    if(urlFrom.length() <= "http://logs.tf/".length()) {
                                                        return;
                                                    }

                                                    ArrayList<LogListResult> holder = new ArrayList<LogListResult>();
                                                    holder.addAll(results);
                                                    ArrayList<LogListResult> newData = parseLogList(response);
                                                    results.clear();
                                                    results.addAll(holder);
                                                    //results.addAll(newData);


                                                    if(response.contains("LBLOGS_ERROR") && response.length() == "LBLOGS_ERROR".length()) {
                                                        return;
                                                    }
                                                    int placeholder = response.indexOf("<table class=\"table loglist\">");
                                                    int listStart = response.indexOf("<tbody>\t\t\t\t", placeholder);
                                                    int listEnd = response.indexOf("\t\t\t\t\t</tbody>", listStart + 11);
                                                    String profileLogsList = response.substring(listStart + 11, listEnd);

                                                    //split into <tr> attributes
                                                    String linesTemp[] = profileLogsList.split("\n\t\t\t\t\t\t\t");
                                                    //final ArrayList<LogListResult> results = new ArrayList<>();

                                                    for(String one : linesTemp) {
                                                        newData.add(new LogListResult(one.replaceAll("\t", "").replaceAll("\n", "")));
                                                    }

                                                    results.addAll(newData);

                                                    adapter.notifyDataSetChanged();
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("GET", "Couldn't load another page");
                                        }
                                    });
                                    // Add the request to the RequestQueue.
                                    queue.add(stringRequest);
                                }
                            });

                            // ListView Item Click Listener
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    // ListView Clicked item index
                                    int itemPosition     = position;

                                    // ListView Clicked item value
                                    LogListResult  itemValue    = (LogListResult) listView.getItemAtPosition(position);

                                    //load log
                                    loadLog(itemValue);
                                }

                            });

                            //sendMessage(results);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("GET", "Player search failed");

                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private String retrievePageHttp(String requestUrl) {

        //instantiate the request queue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final ArrayList<String> httpArray = new ArrayList<>();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // return here
                        httpArray.add(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "Couldn't load another page");
                //return error here
                httpArray.add("LBLOGS_ERROR");

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        return httpArray.get(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<LogListResult> parseLogList(String http) {
        ArrayList<LogListResult> list = new ArrayList<>();

        if(http.contains("LBLOGS_ERROR") && http.length() == "LBLOGS_ERROR".length()) {
            return list;
        }
        int placeholder = http.indexOf("<table class=\"table loglist\">");
        int listStart = http.indexOf("<tbody>\t\t\t\t", placeholder);
        int listEnd = http.indexOf("\t\t\t\t\t</tbody>", listStart + 11);
        String profileLogsList = http.substring(listStart + 11, listEnd);

        //split into <tr> attributes
        String linesTemp[] = profileLogsList.split("\n\t\t\t\t\t\t\t");
        final ArrayList<LogListResult> results = new ArrayList<>();

        for(String one : linesTemp) {
            results.add(new LogListResult(one.replaceAll("\t", "").replaceAll("\n", "")));
        }

        return list;
    }


    private void loadLog(LogListResult item) {
        Intent intent = new Intent("TFLOGS_LOAD_LOG)");

        intent.setAction("TFLOGS_LOAD_LOG");
        intent.putExtra("logID", item.getId());
        intent.putExtra("mapName", item.getMap());
        intent.putExtra("logTitle", item.getTitle());
        startActivity(intent);




        return;
    }
}
