package tflogs_java.myDir.lucas.tf2logstest2;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.lucas.tf2logstest2.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.i(tag, "length > 4000, EXTENDING");
            Log.i(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.i(tag, content);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#444444")));

        ImageView back = (ImageView) findViewById(R.id.main_background);
        Random ran = new Random();
        int x = ran.nextInt(20) + 1;

        //TODO: logs.tf homepage

        switch(ran.nextInt(20)) {
            case 0:
                back.setImageResource(R.drawable.back_blu_scout);
                break;
            case 1:
                back.setImageResource(R.drawable.back_blu_soldier);
                break;
            case 2:
                back.setImageResource(R.drawable.back_blu_pyro);
                break;
            case 3:
                back.setImageResource(R.drawable.back_blu_demo);
                break;
            case 4:
                back.setImageResource(R.drawable.back_blu_heavy);
                break;
            case 5:
                back.setImageResource(R.drawable.back_blu_engineer);
                break;
            case 6:
                back.setImageResource(R.drawable.back_blu_medic);
                break;
            case 7:
                back.setImageResource(R.drawable.back_blu_sniper);
                break;
            case 8:
                back.setImageResource(R.drawable.back_blu_spy);
                break;
            case 9:
                back.setImageResource(R.drawable.back_blu_team);
                break;
            case 10:
                back.setImageResource(R.drawable.back_red_scout);
                break;
            case 11:
                back.setImageResource(R.drawable.back_red_soldier);
                break;
            case 12:
                back.setImageResource(R.drawable.back_red_pyro);
                break;
            case 13:
                back.setImageResource(R.drawable.back_red_demo);
                break;
            case 14:
                back.setImageResource(R.drawable.back_red_heavy);
                break;
            case 15:
                back.setImageResource(R.drawable.back_red_engineer);
                break;
            case 16:
                back.setImageResource(R.drawable.back_red_medic);
                break;
            case 17:
                back.setImageResource(R.drawable.back_red_sniper);
                break;
            case 18:
                back.setImageResource(R.drawable.back_red_spy);
                break;
            case 19:
                back.setImageResource(R.drawable.back_red_team);
                break;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SetMyColor.setMyColor(this);
        }

        //TODO: FAB that takes user to a "favorite" profile

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Player name");
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setSubmitButtonEnabled(true);

        /* searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    //showInputMethod(searchView.findFocus());
                } else {
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
            }
        }); */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.search) {

            //((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            showInputMethod(item.getActionView().findFocus());
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
}
