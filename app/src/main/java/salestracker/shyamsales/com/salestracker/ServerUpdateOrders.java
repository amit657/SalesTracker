package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
public class ServerUpdateOrders extends AsyncTask<String, String, String> {

    Context mContext;
    private DBHelper mydb;
    private String serverHost;

    String statusMessage = "ERROR";
    private int beatRouteId;
    ProgressDialog dialog;

    boolean showDialog = true;
    public ServerUpdateOrders(Context context, String serverHostAddr, int beat){
        mContext = context;
        serverHost = serverHostAddr;
        dialog = new ProgressDialog(context);
        beatRouteId = beat;
    }


    @Override
    protected void onPreExecute() {

    }

    protected String doInBackground(String... args) {


        mydb = new DBHelper(mContext);
        JSONArray orderArr = new JSONArray();

        if(args.length > 0){
            showDialog = false;
        }

        if(showDialog) {
            /*dialog.setTitle("Uploading Orders");
            dialog.show();*/
        }


        if(args.length == 0){
            ArrayList<HashMap<String, String>> customerData = mydb.getOrderedCustomersForBeat(beatRouteId);

            for(int i=0; i<customerData.size(); i++){
                HashMap custHm = customerData.get(i);

                JSONObject custOrder = new JSONObject();
                JSONArray orderList = new JSONArray();
                String orderId = custHm.get("order_id").toString();
                String custName = custHm.get("customer_name").toString();
                Log.d("Order","Processing - " + custName);
                ArrayList<HashMap<String, String>> orderItems;
                try{

                custOrder.put("order_id", orderId);
                custOrder.put("customer_name", custName);

                    orderItems = mydb.getOrderDetails(orderId);
                    for(int j=0; j<orderItems.size(); j++){
                        HashMap<String, String> odrHm = orderItems.get(j);
                        String skuName = odrHm.get("sku_name");
                        String qty = odrHm.get("quantity");
                        String unit = odrHm.get("unit");
                        JSONObject orderData = new JSONObject();
                        orderData.put("sku_name", odrHm.get("sku_name"));
                        orderData.put("quantity", odrHm.get("quantity"));
                        orderData.put("unit", odrHm.get("unit"));
                        orderList.put(orderData);


                    }
                    custOrder.put("order_details", orderList);
                    orderArr.put(custOrder);
                }catch(JSONException je) {
                    je.printStackTrace();
                }
            }

        }else{
            showDialog = false;
            String custName = args[0];
            HashMap custHm = mydb.getCustomerInfo(custName);
            String orderId = mydb.getOrderIdForCustomer(custName);
            JSONObject custOrder = new JSONObject();
            JSONArray orderList = new JSONArray();
            ArrayList<HashMap<String, String>> orderItems;
            try{

                custOrder.put("order_id", orderId);
                custOrder.put("customer_name", custName);

                orderItems = mydb.getOrderDetails(orderId);
                for(int j=0; j<orderItems.size(); j++){
                    HashMap<String, String> odrHm = orderItems.get(j);
                    String skuName = odrHm.get("sku_name");
                    String qty = odrHm.get("quantity");
                    String unit = odrHm.get("unit");
                    JSONObject orderData = new JSONObject();
                    orderData.put("sku_name", odrHm.get("sku_name"));
                    orderData.put("quantity", odrHm.get("quantity"));
                    orderData.put("unit", odrHm.get("unit"));
                    orderList.put(orderData);


                }
                custOrder.put("order_details", orderList);
                orderArr.put(custOrder);
            }catch(JSONException je) {
                je.printStackTrace();
            }



        }

        //Log.d("Sending to server loc:", locUpdateArr.toString());
        postData(orderArr.toString());
        return null;
    }



    public void postData(String valueIWantToSend) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost("http://"+serverHost.trim()+"/salestracker/receiver_order.php");
        Log.d("Calling URL" +
                "", "http://"+serverHost.trim()+"/salestracker/receiver_order.php");
        try {
            // create a list to store HTTP variables and their values
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("orderHttpData", valueIWantToSend));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            Log.d("Request", "Sending orders to server and waiting for response...");
            Log.d("order values", valueIWantToSend);
            HttpResponse response = httpclient.execute(httppost);
            statusMessage = EntityUtils.toString(response.getEntity());
            Log.d("Response::::", statusMessage);
            if(statusMessage.equals("SUCCESS")){
                //mydb.deleteAllLocationUpdateRequests();
                //statusMessage = statusMessage + "\n\n Local location update request DB cleared.";
                //Log.d("SSM", "Cleared all location update requests from DB.");
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
        if(showDialog){
            /*dialog.dismiss();
            new AlertDialog.Builder(mContext)
                    .setTitle("Orders update Status")
                    .setMessage(statusMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, null).show();*/
        }else{
            Toast.makeText(mContext, "Order Uploaded!", Toast.LENGTH_SHORT).show();
        }

    }



}
