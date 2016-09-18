package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class Settings extends ActionBarActivity {

    private DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText smEditText = (EditText) findViewById(R.id.salesManNameEt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);

        //Toast.makeText(this, pref.getString("salesman", null), Toast.LENGTH_SHORT).show();
        smEditText.setText(pref.getString("salesman", null));
        mydb = new DBHelper(this);



        //Load beat routes in spinner

        ArrayList<String> brArray= mydb.getAllBeatRoutes();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, brArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        int activeBeatRouteId = pref.getInt("activeBeatRouteId", 999999);
        if(activeBeatRouteId != 999999){
            /*SharedPreferences.Editor editor = pref.edit();
            editor.putInt("activeBeatRouteId", 1);
            editor.commit();*/
            Log.d("SSM","Beat Route from storage: " + activeBeatRouteId);
            int i=0;
            for(i=0; i < brArray.size(); i++){
                Log.d("SSM",brArray.get(i) + "  index: "+ i);
                if(mydb.getBeatRouteIdForName(brArray.get(i)).equals(Integer.toString(activeBeatRouteId))){
                    Log.d("SSM","Breaking now...");
                    break;

                }

            }
            Log.d("SSM","Position in spinner: " + i);
            sItems.setSelection(i);
        }




        final Settings currentView = this;

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, final int position, long id) {
                // your code here
                Spinner sp = (Spinner) findViewById(R.id.spinner);
                Log.d("SSM", sp.getItemAtPosition(position).toString());
                Log.d("SSM", "Saving beat route id: " + position);

                setActiveBeatRoute(position);
                /*
                new AlertDialog.Builder(currentView)
                        .setTitle("Change Active Beat Route")
                        .setMessage("This will reset all entries for this beat route. Do you want to continue?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                setActiveBeatRoute(position);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                        */


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



    }

    private void setActiveBeatRoute(int position){
        Log.d("setActiveBeatRoute", "Position received: " + position);
        Spinner sp = (Spinner) findViewById(R.id.spinner);
        String brName = sp.getItemAtPosition(position).toString();
        Log.d("setActiveBeatRoute", "Name : " + brName);
        mydb.getBeatRouteIdForName(brName);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("activeBeatRouteId", Integer.parseInt(mydb.getBeatRouteIdForName(brName)));
        editor.commit();

        Toast.makeText(this, "Beat Route " + brName + " is now active.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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


    public void saveSalesMan_click(View view){

        EditText smEditText = (EditText) findViewById(R.id.salesManNameEt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("salesman", smEditText.getText().toString());
        editor.commit();

        //prefs.edit().putString("salesman", smTextView.getText().toString());
        Toast.makeText(this, "Salesman Saved", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllNewCustomer_click(View view){

        new AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Do you really want to delete all new customers?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        mydb.deleteAllNewCustomerData();
                        Toast.makeText(Settings.this, "New customers cleared from database.", Toast.LENGTH_SHORT).show();

                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }
}
