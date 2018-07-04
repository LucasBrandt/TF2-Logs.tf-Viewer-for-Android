package tflogs_java.myDir.lucas.tf2logstest2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lucas.tf2logstest2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Lucas on 8/20/2016.
 * custom adapter for search result list
 */
public class PlayerListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PlayerSearchResult> results;

    public PlayerListAdapter(Activity activity, ArrayList<PlayerSearchResult> results) {
        this.activity = activity;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int location) {
        return results.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.playerlist_row, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.playerlist_image);
        TextView name = (TextView) convertView.findViewById(R.id.playerlist_name);
        TextView numLogs = (TextView) convertView.findViewById(R.id.playerlist_numlogs);

        PlayerSearchResult item = results.get(position);

        //profile picture
        Picasso.with(activity).load(item.getImageURL()).into(image);
        image.setLayoutParams(new RelativeLayout.LayoutParams(useDip(80), useDip(80)));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // title
        name.setText(item.getName());

        // map
        numLogs.setText(Integer.toString(item.getNumLogs()) + " logs");

        return convertView;
    }

    private int useDip(float px) {
        return (int)(px * activity.getResources().getDisplayMetrics().density);
    }

}
