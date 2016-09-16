package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amit on 8/21/2016.
 */
public class ServerUpdateLocationUpdates extends AsyncTask<String, String, String> {

    Context mContext;
    private DBHelper mydb;
    private String serverHost;

    String statusMessage = "ERROR";

    public ServerUpdateLocationUpdates(Context context, String serverHostAddr){
        mContext = context;
        serverHost = serverHostAddr;
    }



    protected String doInBackground(String... args) {


        mydb = new DBHelper(mContext);
        JSONArray locUpdateArr = new JSONArray();
        ArrayList<HashMap<String, String>> customerData = mydb.getAllLocationUpdatesData();

        for(int i=0; i<customerData.size(); i++){
            HashMap hm = customerData.get(i);
            JSONObject custData = new JSONObject();
            try {
                custData.put("customer_name", hm.get("customer_name"));
                custData.put("latitude", hm.get("latitude"));
                custData.put("longitude", hm.get("longitude"));
                custData.put("visit_status", hm.get("visit_status"));
                custData.put("reason", hm.get("reason"));
                custData.put("date_updated", hm.get("date_updated"));
                custData.put("id", hm.get("id"));
                Log.d("SSM", "Location update Id:" + hm.get("id"));
                locUpdateArr.put(custData);
            }catch(JSONException je){
                je.printStackTrace();
            }
        }

        Log.d("Sending to server loc:", locUpdateArr.toString());

        postData(locUpdateArr.toString());




        return null;
    }



    public void postData(String valueIWantToSend) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost("http://"+serverHost.trim()+"/salestracker/location_updates_receiver.php");
        Log.d("Calling URL" +
                "", "http://"+serverHost.trim()+"/salestracker/location_updates_receiver.php");
        try {
            // create a list to store HTTP variables and their values
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIWantToSend));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            Log.d("Request", "Sending new customers to server and waiting for response...");
            HttpResponse response = httpclient.execute(httppost);
            statusMessage = EntityUtils.toString(response.getEntity());
            Log.d("Response::::", statusMessage);
            if(statusMessage.equals("SUCCESS")){
                mydb.deleteAllLocationUpdateRequests();
                Log.d("SSM", "Cleared all location update requests from DB.");
            }
        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        }
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
