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

import com.android.volley.toolbox.ImageLoader;
import com.example.lucas.tf2logstest2.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lucas on 8/11/2016.
 * custom adapter to fill log list objects in listview
 */
public class LogListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<LogListResult> results;
    ImageLoader imageLoader; // = AppController.getInstance().getImageLoader();

    public LogListAdapter(Activity activity, ArrayList<LogListResult> results) {
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
            convertView = inflater.inflate(R.layout.loglist_row, null);

        TextView title = (TextView) convertView.findViewById(R.id.loglist_titleText);
        TextView map = (TextView) convertView.findViewById(R.id.loglist_mapText);
        TextView date = (TextView) convertView.findViewById(R.id.loglist_dateText);

        LogListResult item = results.get(position);

        // format image
        ImageView format = (ImageView) convertView.findViewById(R.id.loglist_imageView);
        if(item.getFormat().equals("6v6") ) {
            format.setImageResource(R.drawable.sixes_format);
        } else if(item.getFormat().equals("Highlander") ) {
            format.setImageResource(R.drawable.hl_format);
        } else if(item.getFormat().equals("Ultiduo/BBall") ) {
            format.setImageResource(R.drawable.ub_format);
        } else {
            format.setImageResource(R.drawable.default_format);
        }
        format.setLayoutParams(new RelativeLayout.LayoutParams(useDip(80), useDip(80)));
        format.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // title
        title.setText(item.getTitle());

        // map
        map.setText(item.getMap());

        // date
        DateFormat df = DateFormat.getDateTimeInstance();
        Date theDate = new Date(Long.parseLong(item.getTimestamp()) * 1000);
        date.setText(df.format(theDate));

        return convertView;
    }

    private int useDip(float px) {
        return (int)(px * activity.getResources().getDisplayMetrics().density);
    }
}
