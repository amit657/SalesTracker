package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amit on 9/25/2016.
 */
public class LoadSalesman extends AsyncTask<String, String, String> {

    public static final String TAG_SALESMAN_ARR = "salesman_list";
    private DBHelper mydb;

    private String statusMessage = "ERROR";

    private Context mContext;
    private String serverHost;

    ProgressDialog dialog;

    public LoadSalesman(Context context, String serverHostAddr) {
        mContext = context;
        serverHost = serverHostAddr;
        Log.d("SSM", "Constructor LoadSalesman() Host: " + serverHost);
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("Downloading Salesman");
        dialog.show();
    }


    protected String doInBackground(String... args) {

        String serviceURL = "http://"+serverHost.trim()+"/salestracker/getSalesman.php";
        Log.d("Fetch Data URL", serviceURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        mydb = new DBHelper(mContext);
        JSONParser jParser = new JSONParser();
        try {
            JSONObject json = jParser.makeHttpRequest(serviceURL, "GET", params);
            Log.d("All salesman>>>>: ", json.toString());
            if (json != null) {
                mydb.deleteAllSalesman();
                try {
                    JSONArray brList = json.getJSONArray(TAG_SALESMAN_ARR);
                    for (int i = 0; i < brList.length(); i++) {
                        JSONObject br = brList.getJSONObject(i);
                        Log.d("JSON Item",br.toString());
                        mydb.insertSalesman(br.getInt("salesman_id"), br.getString("salesman_name"));
                    }
                    statusMessage = "Refresh Complete!";
                } catch (JSONException je) {
                    Log.d("ST-ERROR", "Error occurred when loading JSON data (Salesman) into DB.");
                    statusMessage = "Salesman loading Exception Occurred and Caught!";
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
        dialog.dismiss();
        new AlertDialog.Builder(mContext)
                .setTitle("Salesman Master Update Status")
                .setMessage(statusMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show();
    }


}
