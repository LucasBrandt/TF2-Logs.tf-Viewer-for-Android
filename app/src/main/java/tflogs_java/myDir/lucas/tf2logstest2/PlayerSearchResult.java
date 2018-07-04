package tflogs_java.myDir.lucas.tf2logstest2;

import java.io.Serializable;

/**
 * Created by Lucas on 7/4/2016.
 * Singular search result for http player name string search
 */
public class PlayerSearchResult implements Serializable {
    private int numLogs;
    private String imageURL;
    private String profileID;
    private String name;


    public PlayerSearchResult(int numLogs, String imageURL, String profileID, String name) {
        this.numLogs = numLogs;
        this.imageURL = imageURL;
        this.profileID = profileID;
        this.name = name;
    }

    //RELIES ON UNCHANGED API
    public PlayerSearchResult(String src) {

        int firstCut = src.indexOf("src=");
        this.imageURL = src.substring(firstCut + 5, src.indexOf("\"", firstCut + 5));
        this.numLogs = Integer.parseInt(src.substring(src.indexOf("<td>") + 4, src.indexOf("</td>")));
        int cutoffOne = src.indexOf("<a href=");
        this.profileID = src.substring(src.indexOf("/profile/", cutoffOne) + 9, src.indexOf("\">\n", cutoffOne));
        int cutoffTwo = src.indexOf("                ", src.indexOf(profileID)) + "                ".length();
        this.name = src.substring(cutoffTwo, myMinPos(src.indexOf("\n", cutoffTwo), src.indexOf(",", cutoffTwo)));
    }

    public int getNumLogs() {
        return this.numLogs;
    }
    public String getImageURL() { return this.imageURL; }
    public String getProfileID() {
        return this.profileID;
    }
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return(name + ": " + numLogs + " logs");
    }

    private int myMinPos(int a, int b) {
        if (a < 0) {
            return b;
        } else if (b < 0) {
            return a;
        }

        if (a < b) {
            return a;
        } else if (b < a) {
            return b;
        } else {
            return a;
        }
    }

}
