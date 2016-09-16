package salestracker.shyamsales.com.salestracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddNewCustomerActivity extends ActionBarActivity implements LocationListener {

    Location currentLocation;
    private DBHelper mydb;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_customer);

        mydb = new DBHelper(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,0, this);

        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_customer, menu);
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

    public void saveCustomer_Click(View view){

        if(currentLocation == null){
            Toast.makeText(getBaseContext(), "Current location not available. ",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String customerName, customerAddress, customerPhone, beatRoute;

        EditText cnet = (EditText) findViewById(R.id.custNameEt);
        customerName = cnet.getText().toString();

        EditText caet = (EditText) findViewById(R.id.custAddressEt);
        customerAddress = caet.getText().toString();

        EditText phet = (EditText) findViewById(R.id.phoneInput);
        customerPhone = phet.getText().toString();

        Spinner sp = (Spinner) findViewById(R.id.beat_route_spinner);
        beatRoute = sp.getSelectedItem().toString();

        if(!mydb.insertNewCustomer(customerName, customerAddress, customerPhone, Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()), beatRoute)){
            Toast.makeText(this, "Customer already exist!",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Customer successfully added!",
                    Toast.LENGTH_SHORT).show();
            cnet.setText("");
            caet.setText("");
            phet.setText("");
            sp.setSelection(0);
        }


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

        //int lat = (int) (location.getLatitude());
        //int lng = (int) (location.getLongitude());
        currentLocation = location;

        TextView locTextView = (TextView) findViewById(R.id.locationDisplayTv);
        locTextView.setBackgroundColor(65280);
        locTextView.setText(location.getLatitude() + ", " + location.getLongitude());
        //System.out.println(">>>>>>>>>>>>>>>>>Location change received!!");

        //Toast.makeText(this, "Location change received ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }







}
