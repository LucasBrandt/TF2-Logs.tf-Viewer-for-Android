package tflogs_java.myDir.lucas.tf2logstest2;

/**
 * Created by Lucas on 7/28/2016.
 * similar to PlayerSearchResult
 */
public class LogListResult {
    private String id;
    private String title;
    private String map;
    private String format;
    private String timestamp;

    public LogListResult(String source) {
        //parse source string
        //old format: <tr id="log_ID"><td><a href="/1476339?highlight=76561198042514422">TITLE</a></td><td>MAP</td><td class="center">FORMAT</td><td class="center">30</td><td class="datefield" data-timestamp="TIMESTAMP">25-Jul-2016 22:12:32</td></tr>
        int temp = source.indexOf("log_") + 4;
        this.id = source.substring(temp, source.indexOf("\"", temp));
        temp = source.indexOf("\">", source.indexOf("<td>"));
        this.title = source.substring(source.indexOf(">", temp) + 1, source.indexOf("</a>", temp));
        temp = source.indexOf("<td>", source.indexOf(this.title)) + 4;
        this.map = source.substring(temp, source.indexOf("</td>", temp));
        temp = source.indexOf("class=", temp) + 15;
        this.format = source.substring(temp, source.indexOf("</td>", temp));
        temp = source.indexOf("data-timestamp") + 16;
        this.timestamp = source.substring(temp, source.indexOf("\"", temp));
        //Log.i("PROFILE PARSE", this.toString());
    }


    @Override
    public String toString() {
        return "id=" + id + ", title=" + this.title + ", map=" + this.map + ", format=" + this.format + ", timestamp=" + this.timestamp;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMap() {
        return map;
    }

    public String getFormat() {
        return format;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
