package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class DataUpdateActivity extends ActionBarActivity {

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);

        mydb = new DBHelper(this);



        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref",0);

        EditText et = (EditText) findViewById(R.id.serverHostEt);
        et.setText(pref.getString("serverHost", null));

        /*
        ArrayList<String> brArray= mydb.getAllBeatRoutes();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, brArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
*/

/*
        try {
            URL url = new URL("http://192.168.1.107/salestracker/test.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
        }catch(Exception e){
            e.printStackTrace();
        }
*/


    }

    public void loadBeatRoutesFromServer(View view){
        EditText et = (EditText) findViewById(R.id.serverHostEt);
        new LoadBeatRoutes(this, et.getText().toString()).execute();
    }

    public void updateServerHost(View view){
        EditText et = (EditText) findViewById(R.id.serverHostEt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("serverHost", et.getText().toString());
        editor.commit();
        Toast.makeText(this, "Server Host Saved", Toast.LENGTH_SHORT).show();
    }
/*
    public void updateNewCustomersOnServer(View view){
        Log.d("updateNewCustomersOnS..", "calling asynchronous task...");
        EditText et = (EditText) findViewById(R.id.serverHostEt);
        new ServerUpdateNewCustomers(this, et.getText().toString()).execute();
    }
*/
    public void refreshMobileData(View view){
        /*mydb = new DBHelper(this);
        mydb.deleteAllData();
        mydb.insertCustomer("Alok Enterprises", "Parsudih Shiv Mandir", "99635865412", 22.754730, 86.212132);
        mydb.insertCustomer("Anjali Store", "Parsudih Main Road", "9963895624", 22.754735, 86.212142);
        mydb.insertCustomer("Tekchand Store", "Parsudih Shiv Mandir Road", "8965895624", 22.754745, 86.212122);
*/
        System.out.println("=================DATABASE UDATED SUCCESSFULLY=======================");

       /*
        EditText et = (EditText) findViewById(R.id.serverHostEt);
        Log.d("SSM","Server Host: " + et.getText().toString());
        new LoadBeatRoutes(this, et.getText().toString()).execute();
        new LoadCustomers(this,et.getText().toString()).execute();
        mydb.deleteAllNewCustomerData();
        mydb.deleteAllLocationUpdateRequests();
        */



        new AlertDialog.Builder(this)
                .setTitle("Update Server")
                .setMessage("This will clear and reload the following data in this device:\n- Beat Routes\n- Customers\n- Clear all new customers\n- Clear all location updates")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        EditText et = (EditText) findViewById(R.id.serverHostEt);
                        Log.d("SSM","Server Host: " + et.getText().toString());
                        new LoadBeatRoutes(DataUpdateActivity.this, et.getText().toString()).execute();
                        new LoadSalesman(DataUpdateActivity.this, et.getText().toString()).execute();
                        new LoadCustomers(DataUpdateActivity.this,et.getText().toString()).execute();
                        mydb.deleteAllNewCustomerData();
                        mydb.deleteAllLocationUpdateRequests();

                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }


    public void updateServer(View view){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", MODE_PRIVATE);

        int beatRoute = pref.getInt("activeBeatRouteId", 999999);
        if(beatRoute == 999999){
            Toast.makeText(this, "No beat route is active. Goto settings and activate a beat route.", Toast.LENGTH_SHORT).show();
            return;
        }
        //setContentView(R.layout.activity_data_update);




        new AlertDialog.Builder(this)
                .setTitle("Update Server")
                .setMessage("This will send the following data to server:\n- Customer Visit Status\n- New Customers\n- Location Updates")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", MODE_PRIVATE);
                        int beatRoute = pref.getInt("activeBeatRouteId", 999999);
                        new ServerUpdate(DataUpdateActivity.this, pref.getString("serverHost", null), pref.getString("salesman", null), beatRoute).execute();
                        new ServerUpdateNewCustomers(DataUpdateActivity.this, pref.getString("serverHost", null)).execute();
                        new ServerUpdateLocationUpdates(DataUpdateActivity.this, pref.getString("serverHost", null), pref.getString("salesman", null)).execute();

                    }})
                .setNegativeButton(android.R.string.no, null).show();






        //new ServerUpdate(this, pref.getString("serverHost", null), pref.getString("salesman", null), beatRoute).execute();
        //new ServerUpdateNewCustomers(this, pref.getString("serverHost", null)).execute();
        //new ServerUpdateLocationUpdates(this, pref.getString("serverHost", null), pref.getString("salesman", null)).execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
















}



