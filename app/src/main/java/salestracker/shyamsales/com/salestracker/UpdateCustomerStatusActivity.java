package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class UpdateCustomerStatusActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, LocationListener {

    //Autocomplete
    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;

    private int distanceRange = 50;
    //These values show in autocomplete
    String item[];/*={
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
    };*/

    private DBHelper mydb;

    private LocationManager locationManager;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_status);



        mydb = new DBHelper(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        //Toast.makeText(this, pref.getString("salesman", null), Toast.LENGTH_SHORT).show();
        int beatRouteId = pref.getInt("activeBeatRouteId",999999);

        if(beatRouteId == 999999) {
            Toast.makeText(getBaseContext(), "You need to set active beat route first. Goto settings screen to do that.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        ArrayList<String> array_list = mydb.getAllCustomer(beatRouteId);
        item = new String[array_list.size()];

        for(int i=0; i<array_list.size(); i++){
            item[i] = array_list.get(i);
        }





        //autocomplete code -
        //http://androidexample.com/Show_AutoComplete_Suggestions_-_Android_Example/index.php?view=article_discription&aid=105
        textView = (AutoCompleteTextView) findViewById(R.id.customers_ac);

        //Create adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);

        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);



        //location

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,0, this); //10000 milliseconds, 5 metres
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this,null);
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



        /*
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        crta.setAltitudeRequired(false);
        crta.setBearingRequired(false);
        crta.setCostAllowed(true);
        crta.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(crta, true);

        // String provider = LocationManager.GPS_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);
        //updateWithNewLocation(location);

        locationManager.requestLocationUpdates(provider, 1000, 0,
                this);
*/


    }

    public void updateStatusBtnAction(View view){
        if(currentLocation == null){
            Toast.makeText(getBaseContext(), "Your current location is not available!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.customers_ac);
        if(atv.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "Please select a store",
                    Toast.LENGTH_LONG).show();
            return;
        }

        RadioGroup radioStatusGroup;
        RadioButton radioQtyButton;
        radioStatusGroup = (RadioGroup)findViewById(R.id.orderStatusRadioGroup);

        if(radioStatusGroup.getCheckedRadioButtonId()!=-1){
            int id = radioStatusGroup.getCheckedRadioButtonId();
            View radioButton = radioStatusGroup.findViewById(id);
            int radioId = radioStatusGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioStatusGroup.getChildAt(radioId);
            String selection = (String) btn.getText();

            if(selection.equals("ORDER RECEIVED")){
                orderReceivedAction();
            }

            if(selection.equals("NO ORDER")){
                noOrderAction();
            }
        }else{

            Toast.makeText(getBaseContext(), "Please select an order status",
                    Toast.LENGTH_LONG).show();
            return;
            //no radio selected
        }

    }

    public void orderReceivedAction(){

        AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.customers_ac);
        HashMap hm = mydb.getCustomerInfo(atv.getText().toString());
        Location storeLocation = new Location("StoreLocation");
        storeLocation.setLatitude(Double.parseDouble(hm.get("latitude").toString()));
        storeLocation.setLongitude(Double.parseDouble(hm.get("longitude").toString()));

        if(checkLocationRange(storeLocation, currentLocation)){
            mydb.updateVisitStatus(atv.getText().toString(), "ORDER_RECEIVED", "");
            Toast.makeText(getBaseContext(), "Status updated for: " + atv.getText().toString(),
                    Toast.LENGTH_LONG).show();
            finish();
        }else{
            float difference = storeLocation.distanceTo(currentLocation) - distanceRange;
            if(difference > 100000){
                Toast.makeText(getBaseContext(), "Please click on update location button." + atv.getText().toString(),
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(), "You are "+difference+ " metres away from " + atv.getText().toString(),
                        Toast.LENGTH_LONG).show();
            }

        }

    }

    public void noOrderAction(){

        AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.customers_ac);

        String reasonText;
        Spinner sp = (Spinner)findViewById(R.id.no_order_reason_sp);
        if(sp.getSelectedItem().toString().equals("Select a reason")){
            Toast.makeText(getBaseContext(), "Please select a reason",
                    Toast.LENGTH_LONG).show();
            return;
        }else if(sp.getSelectedItem().toString().equals("Others")){
            EditText et = (EditText) findViewById(R.id.no_order_reason);
            reasonText = et.getText().toString();
            if(reasonText.equals("")){
                Toast.makeText(getBaseContext(), "Please enter a reason",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }else{
            reasonText = sp.getSelectedItem().toString();
        }




        HashMap hm = mydb.getCustomerInfo(atv.getText().toString());

        Location storeLocation = new Location("StoreLocation");
        storeLocation.setLatitude(Double.parseDouble(hm.get("latitude").toString()));
        storeLocation.setLongitude(Double.parseDouble(hm.get("longitude").toString()));

        //float distance = storeLocation.distanceTo(currentLocation);
        //System.out.println(">>>>>>>>>>>>>> Distance - " + distance);
        if(checkLocationRange(storeLocation, currentLocation)){
            mydb.updateVisitStatus(atv.getText().toString(), "NO_ORDER", reasonText);
            Toast.makeText(getBaseContext(), "Status updated for: " + atv.getText().toString(),
                    Toast.LENGTH_LONG).show();
        }else{
            float difference = storeLocation.distanceTo(currentLocation) - distanceRange;
            Toast.makeText(getBaseContext(), "You are "+difference+ " metres away from " + atv.getText().toString(),
                    Toast.LENGTH_LONG).show();
        }


    }

    public boolean checkLocationRange(Location locA, Location locB){
        float distance = locA.distanceTo(locB);

        if(distance < distanceRange){
            return true;
        }else{
            return false;
        }
    }


    public void locationUpdateAction(){

        if(currentLocation == null){
            Toast.makeText(getBaseContext(), "Your current location is not available!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.customers_ac);
        if(atv.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "Please select a store",
                    Toast.LENGTH_LONG).show();
            return;
        }

        RadioGroup radioStatusGroup;
        RadioButton radioQtyButton;
        radioStatusGroup = (RadioGroup)findViewById(R.id.orderStatusRadioGroup);
        EditText et = (EditText) findViewById(R.id.no_order_reason);
        String reasonText = "";

        Log.d("LOC", "Checking radio button............");
        if(radioStatusGroup.getCheckedRadioButtonId()!= -1){
            int id = radioStatusGroup.getCheckedRadioButtonId();
            View radioButton = radioStatusGroup.findViewById(id);
            int radioId = radioStatusGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioStatusGroup.getChildAt(radioId);
            String selection = (String) btn.getText();
            String orderStatus = "";
            if(selection.equals("ORDER RECEIVED")){
                orderStatus = "ORDER_RECEIVED";
            }
            if(selection.equals("NO ORDER")){
                Log.d("SSM", "In No ORDER of Location update");
                Spinner sp = (Spinner)findViewById(R.id.no_order_reason_sp);
                if(sp.getSelectedItem().toString().equals("Select a reason")){
                    Toast.makeText(getBaseContext(), "Please select a reason",
                            Toast.LENGTH_LONG).show();
                    return;
                }else if(sp.getSelectedItem().toString().equals("Others")){
                    reasonText = et.getText().toString();
                    if(reasonText.equals("")){
                        Toast.makeText(getBaseContext(), "Please enter a reason",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    reasonText = sp.getSelectedItem().toString();
                }




                if(reasonText.equals("")){
                    Toast.makeText(getBaseContext(), "Please enter a reason",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                orderStatus = "NO_ORDER";
            }
            Log.d("LOC", "Inserting location update.......");
            mydb.insertLocationUpdate(atv.getText().toString(), currentLocation, orderStatus, reasonText);
            Toast.makeText(getBaseContext(), "Location update request submitted.",
                    Toast.LENGTH_LONG).show();
            finish();
        }else{

            Toast.makeText(getBaseContext(), "Please select an order status",
                    Toast.LENGTH_LONG).show();
            return;
            //no radio selected
        }





    }
    public void updateLocationBtnClick(View view){


           if(currentLocation != null){


               new AlertDialog.Builder(this)
                       .setTitle("Update Location")
                       .setMessage("Do you really want to update location?")
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                           public void onClick(DialogInterface dialog, int whichButton) {

                               locationUpdateAction();


                               /*
                               AutoCompleteTextView atv = (AutoCompleteTextView)findViewById(R.id.customers_ac);
                               //mydb.updateCustomerLocation(atv.getText().toString(), currentLocation);
                               Toast.makeText(getBaseContext(), "Location updated for: " + atv.getText().toString()+ "\n"+ currentLocation.getLatitude() + ", " + currentLocation.getLongitude(),
                                       Toast.LENGTH_LONG).show();

                               HashMap hm = mydb.getCustomerInfo(atv.getText().toString());
                               Log.d("DB Read", "Latitude: " + hm.get("latitude") + " , longitude: " + hm.get("longitude"));*/

                           }})
                       .setNegativeButton(android.R.string.no, null).show();

           }else{
               Toast.makeText(getBaseContext(), "Current location not available. ",
                       Toast.LENGTH_LONG).show();
           }
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Show Alert
        //Toast.makeText(getBaseContext(), "Position:" + arg2 + " Month:" + arg0.getItemAtPosition(arg2),
                //Toast.LENGTH_LONG).show();

        Log.d("AutocompleteContacts", "Position:" + arg2 + " Month:" + arg0.getItemAtPosition(arg2));

        HashMap hm = mydb.getCustomerInfo(arg0.getItemAtPosition(arg2).toString());
        TextView tv = (TextView)findViewById(R.id.customer_details_tv);
        tv.setText(hm.get("customer_name").toString() + "\n" + hm.get("address").toString() + "\n" + hm.get("phone").toString());

        //customer_details_tv

        //Show the distance

        if(currentLocation != null){

            Location storeLocation = new Location("StoreLocation");
            storeLocation.setLatitude(Double.parseDouble(hm.get("latitude").toString()));
            storeLocation.setLongitude(Double.parseDouble(hm.get("longitude").toString()));

            Log.d("Latitude From Object",hm.get("latitude").toString());
            Log.d("Latitude From Double",Double.toString(Double.parseDouble(hm.get("latitude").toString())));

            TextView atv = (TextView)findViewById(R.id.distancetv);
            double difference = storeLocation.distanceTo(currentLocation) - distanceRange - currentLocation.getAccuracy();

            difference = Math.round(difference * 100.0) / 100.0;


            String text = "";
            if(difference <= 0){
                text = "You are within range of " + hm.get("customer_name").toString() + " " + difference;

            }/*else{
                text = "You are " + difference + " metres away from " + hm.get("customer_name").toString();
            }*/
            atv.setText(text);


        }

        //distancetv


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_customer_status, menu);
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


    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location.getAccuracy() < 10){
            int lat = (int) (location.getLatitude());
            int lng = (int) (location.getLongitude());
            currentLocation = location;

            TextView locTextView = (TextView) findViewById(R.id.locationStausTv);
            locTextView.setBackgroundColor(65280);
            locTextView.setText("Acc:" + location.getAccuracy() + " + "+ distanceRange + " achieved.");
            distanceRange = distanceRange + (int)location.getAccuracy();
            locationManager.removeUpdates(this);
        }else{
            TextView locTextView = (TextView) findViewById(R.id.locationStausTv);
            locTextView.setText("Current location accuracy: " + location.getAccuracy());
        }

    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


}
