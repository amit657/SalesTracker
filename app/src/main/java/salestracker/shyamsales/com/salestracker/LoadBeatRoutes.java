package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by amit on 8/9/2016.
 */
public class LoadBeatRoutes extends AsyncTask<String, String, String> {

    public static final String TAG_BEAT_ARR = "beat_route_list";
    private DBHelper mydb;

    private String statusMessage = "ERROR";

    private Context mContext;
    private String serverHost;
    public LoadBeatRoutes(Context context, String serverHostAddr) {
        mContext = context;
        serverHost = serverHostAddr;
        Log.d("SSM", "Constructor LoadBeatRoutes() Host: " + serverHost);
    }


    protected String doInBackground(String... args) {

        String serviceURL = "http://"+serverHost.trim()+"/salestracker/getBeatRoutes.php";
        Log.d("Fetch Data URL", serviceURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        mydb = new DBHelper(mContext);
        JSONParser jParser = new JSONParser();
        try {
            JSONObject json = jParser.makeHttpRequest(serviceURL, "GET", params);
            Log.d("All beat routes>>>>: ", json.toString());
            if (json != null) {
                mydb.deleteAllBeatRoutes();
                try {
                    JSONArray brList = json.getJSONArray(TAG_BEAT_ARR);
                    for (int i = 0; i < brList.length(); i++) {
                        JSONObject br = brList.getJSONObject(i);
                        Log.d("JSON Item",br.toString());
                        mydb.insertBeatRoute(br.getInt("beat_route_id"), br.getString("beat_route_name"));
                    }
                    statusMessage = "Refresh Complete!";
                } catch (JSONException je) {
                    Log.d("ST-ERROR", "Error occurred when loading JSON data (Beat Routes) into DB.");
                    statusMessage = "Beat Route loading Exception Occurred and Caught!";
                    je.printStackTrace();
                }
            }
        }catch(Exception e){
            //Toast.makeText(mContext, "Error occurred while loading customers.", Toast.LENGTH_LONG).show();
        }

        return null;
    }


    @Override public void onPostExecute(String result)
    {
        //Toast.makeText(mContext, statusMessage, Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(mContext)
                .setTitle("Beat Route Master Update Status")
                .setMessage(statusMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show();
    }


}
