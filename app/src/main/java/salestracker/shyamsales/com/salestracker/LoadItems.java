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
public class LoadItems extends AsyncTask<String, String, String> {

    public static final String TAG_CUST_ARR = "customer_list";
    private DBHelper mydb;

    private String statusMessage = "ERROR";

    private Context mContext;
    private int beatRouteId;
    private String serverHost;
    public LoadItems(Context context, String serverHostAddr) {
        this.beatRouteId = beatRouteId;
        mContext = context;
        serverHost = serverHostAddr;
        Log.d("SSM", "Constructor LoadItems() Host: " + serverHost);
    }


    protected String doInBackground(String... args) {




        String serviceURL = "http://"+serverHost.trim()+"/salestracker/getItemList.php";
        Log.d("Fetch Data URL", serviceURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        mydb = new DBHelper(mContext);
        JSONParser jParser = new JSONParser();
        try {
            JSONObject json = jParser.makeHttpRequest(serviceURL, "GET", params);
            Log.d("All items>>>>: ", json.toString());
            if (json != null) {
                mydb.deleteAllItems();
                try {
                    JSONArray custList = json.getJSONArray(TAG_CUST_ARR);
                    for (int i = 0; i < custList.length(); i++) {
                        JSONObject cust = custList.getJSONObject(i);
                        mydb.insertItem(cust.get("item_name").toString(), Float.parseFloat(cust.get("net_rate").toString()), Float.parseFloat(cust.get("tax").toString()), Float.parseFloat(cust.get("conversion").toString()), cust.get("primary_unit").toString(), cust.get("alternate_unit").toString(), Float.parseFloat(cust.get("margin").toString()), cust.get("pack_size").toString());
                    }
                    statusMessage = "Refresh Complete!";
                } catch (JSONException je) {
                    Log.d("ST-ERROR", "Error occurred when loading JSON data (Items) into DB.");
                    statusMessage = "Exception Occurred and Caught!";
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
                .setTitle("Item Master Update Status")
                .setMessage(statusMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show();
    }


}
