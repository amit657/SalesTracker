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
public class LoadCustomers extends AsyncTask<String, String, String> {

    public static final String TAG_CUST_ARR = "customer_list";
    private DBHelper mydb;

    private String statusMessage = "ERROR";

    private Context mContext;
    private int beatRouteId;
    private String serverHost;
    public LoadCustomers(Context context, int beatRouteId, String serverHostAddr) {
        this.beatRouteId = beatRouteId;
        mContext = context;
        serverHost = serverHostAddr;
        Log.d("SSM", "Constructor LoadCustomers() Host: " + serverHost);
    }


    protected String doInBackground(String... args) {




        String serviceURL = "http://"+serverHost.trim()+"/salestracker/getCustomerList.php";
        Log.d("Fetch Data URL", serviceURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("bid",Integer.toString(beatRouteId)));



        mydb = new DBHelper(mContext);
        JSONParser jParser = new JSONParser();
        try {
            JSONObject json = jParser.makeHttpRequest(serviceURL, "GET", params);
            Log.d("All customers>>>>: ", json.toString());
            if (json != null) {
                mydb.deleteAllData();
                try {
                    JSONArray custList = json.getJSONArray(TAG_CUST_ARR);
                    for (int i = 0; i < custList.length(); i++) {
                        JSONObject cust = custList.getJSONObject(i);
                        mydb.insertCustomer(cust.get("customer_name").toString(), cust.get("address").toString(), cust.get("phone").toString(), cust.get("latitude").toString(), cust.get("longitude").toString());
                    }
                    statusMessage = "Refresh Complete!";
                } catch (JSONException je) {
                    Log.d("ST-ERROR", "Error occurred when loading JSON data into DB.");
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
                .setTitle("New Customer Update Status")
                .setMessage(statusMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show();
    }


}
