package tflogs_java.myDir.lucas.tf2logstest2;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lucas.tf2logstest2.R;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Lucas on 6/28/2016.
 * The player search result list view activity
 */
public class SearchResultsActivity extends AppCompatActivity {

    ListView listView ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // ...


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#444444")));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SetMyColor.setMyColor(this);
        }
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // ...
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            String url = "http://logs.tf/search/player?s=" + query;

            getSupportActionBar().setTitle(query);
            getSupportActionBar().setSubtitle("Player search");

            //instantiate the request queue.
            final RequestQueue queue = Volley.newRequestQueue(this);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //cut down to list of players
                            int listBegin = response.indexOf("<table class=\"table table-players log\">\n" +
                                    "    <tbody>\n" +
                                    "        ");
                            listBegin += ("<table class=\"table table-players log\">\n" +
                                    "    <tbody>\n" +
                                    "        ").length();

                            int listEnd = response.indexOf("\n" +
                                    "        </tbody>", listBegin);

                            if(listEnd < listBegin) {
                                //TODO: INDICATE NO DATA
                                return;
                            }
                            String resultListStr = response.substring(listBegin, listEnd);

                            String entries[] = resultListStr.split("</tr>");

                            ArrayList<PlayerSearchResult> results = new ArrayList<>();


                            int indexCounter = 0;
                            for(String entry : entries) {
                                if (indexCounter >= 0) {
                                    //results.add(indexCounter - 1, new PlayerSearchResult(line));
                                    results.add(new PlayerSearchResult(entry));
                                }
                                indexCounter++;
                            }

                            // Get ListView object from xml
                            listView = (ListView) findViewById(R.id.search_result_list);

                            // Define a new Adapter
                            // First parameter - Context
                            // Second parameter - Layout for the row
                            // Third parameter - ID of the TextView to which the data is written
                            // Forth - the Array of data

                            final PlayerListAdapter adapter = new PlayerListAdapter(SearchResultsActivity.this, results);

                            // Assign adapter to ListView
                            listView.setAdapter(adapter);

                            // ListView Item Click Listener
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    // ListView Clicked item index
                                    int itemPosition     = position;

                                    // ListView Clicked item value
                                    PlayerSearchResult  itemValue    = (PlayerSearchResult) listView.getItemAtPosition(position);

                                    //load profile
                                    loadProfile(itemValue);

                                }

                            });
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
    // ...



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadProfile(PlayerSearchResult profile) {
        Intent intent = new Intent("TFLOGS_LOAD_PROFILE");

        intent.setAction("TFLOGS_LOAD_PROFILE");
        intent.putExtra("profileID", profile.getProfileID());
        intent.putExtra("name", profile.getName());
        intent.putExtra("numLogs", Integer.toString(profile.getNumLogs()));
        startActivity(intent);

    }

}
