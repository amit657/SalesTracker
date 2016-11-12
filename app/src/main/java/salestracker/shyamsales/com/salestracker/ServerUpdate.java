package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
 * Created by amit on 8/11/2016.
 */
public class ServerUpdate extends AsyncTask<String, String, String> {

    Context mContext;
    private DBHelper mydb;
    private String serverHost;
    private String salesman;

    String statusMessage = "ERROR";
    int activeBeatRouteId;

    ProgressDialog dialog;

    protected String doInBackground(String... args) {

        mydb = new DBHelper(mContext);
        JSONArray custListArr = new JSONArray();

        ArrayList<HashMap<String, String>> customerData = mydb.getAllCustomerDataForBeat(activeBeatRouteId);


        for(int i=0; i<customerData.size(); i++){
            HashMap hm = customerData.get(i);
            JSONObject custData = new JSONObject();
            try {
                custData.put("customer_name", hm.get("customer_name"));
                custData.put("address", hm.get("address"));
                custData.put("phone", hm.get("phone"));
                custData.put("latitude", hm.get("latitude"));
                custData.put("longitude", hm.get("longitude"));
                custData.put("visit_status", hm.get("visit_status"));
                custData.put("reason", hm.get("reason"));
                custData.put("date_updated", hm.get("date_updated"));
                custData.put("salesman", salesman);
                custData.put("order_id", hm.get("order_id"));
                custListArr.put(custData);
            }catch(JSONException je){
                je.printStackTrace();
            }
        }

        Log.d("Sending to server", custListArr.toString());

        postData(custListArr.toString());

        return null;
    }

    public ServerUpdate(Context context, String serverHostAddr, String sm, int beatRouteId){
        mContext = context;
        serverHost = serverHostAddr;
        salesman = sm;
        activeBeatRouteId = beatRouteId;
        dialog = new ProgressDialog(context);
    }



    @Override
    protected void onPreExecute() {
        dialog.setTitle("Uploading customer status");
        dialog.show();
    }



    public void postData(String valueIWantToSend) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        String url = "http://"+serverHost.trim()+"/salestracker/receiver.php";
        HttpPost httppost = new HttpPost(url);
        try {
            // create a list to store HTTP variables and their values
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIWantToSend));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            Log.d("Request", "Sending to server and waiting for response..." + url);
            HttpResponse response = httpclient.execute(httppost);

            statusMessage = EntityUtils.toString(response.getEntity());
            Log.d("Response::::", statusMessage);

        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        }
    }


    @Override public void onPostExecute(String result)
    {
        //Toast.makeText(mContext, statusMessage, Toast.LENGTH_SHORT).show();
        dialog.dismiss();

        new AlertDialog.Builder(mContext)
                .setTitle("Customer visit status server update")
                .setMessage(statusMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null).show();
    }

}
