package salestracker.shyamsales.com.salestracker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class ListCountersGeneric extends ListActivity {

    ArrayList<HashMap<String,String>> list;
    private DBHelper mydb;

    String activeBeatRouteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_counters_generic);
        mydb = new DBHelper(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        //Toast.makeText(this, pref.getString("salesman", null), Toast.LENGTH_SHORT).show();
        int beatRouteId = pref.getInt("activeBeatRouteId",999999);

        if(beatRouteId == 999999) {
            Toast.makeText(getBaseContext(), "You need to set active beat route first. Goto settings screen to do that.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        activeBeatRouteName = mydb.getBeatRouteNameForId(beatRouteId);
        Log.d("SSM","Beat route name: " + activeBeatRouteName + " for ID: " + beatRouteId);
        TextView beatRouteTv = (TextView) findViewById(R.id.beatRouteName);
        beatRouteTv.setText(activeBeatRouteName);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int activeBeatRouteId = pref.getInt("activeBeatRouteId", 999999);
            if(activeBeatRouteId == 999999){
                Toast.makeText(this, "Please make a beat route active in settings.", Toast.LENGTH_SHORT).show();
                return;
            }

            String value = extras.getString("ListType");
            if(value.equals("VisitedCustomersList")){
                populateVisitedCustomersList(activeBeatRouteId);
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Visited Customers List (" + list.size() + ")");
                titleTextView.setTextColor(Color.GREEN);
            }

            if(value.equals("LocationUpdatesList")){
                populateLocationUpdatesList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Location Updates List (" + list.size() + ") ");
                titleTextView.setTextColor(Color.GREEN);
            }


            if(value.equals("NewCustomersList")){
                populateNewCustomersList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("New Customers List (" + list.size() + ") ");
                titleTextView.setTextColor(Color.BLUE);
            }

            if(value.equals("AllCustomersList")){
                populateAllCustomersList(activeBeatRouteId);
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("All Customers List (" + list.size() + ") ");
                titleTextView.setTextColor(Color.CYAN);
            }

            if(value.equals("VisitPendingCustomersList")){
                populateVisitPendingCustomersList(activeBeatRouteId);
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Pending Customer Visit List (" + list.size() + ") ");
                titleTextView.setTextColor(Color.RED);
            }

            if(value.equals("NearbyCustomersList")){
                populateNearByCustomersList(activeBeatRouteId);
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Pending Customer Visit List (" + list.size() + ") ");
                titleTextView.setTextColor(Color.RED);
            }




        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.customer_basic_list_item,
                new String[] {"customer_name","address","phone"},
                new int[] {R.id.text1,R.id.text2, R.id.text3}
        );

        setListAdapter(adapter);

    }


    private void populateNearByCustomersList(int activeBeatRouteId){
        //list = mydb.getAllNearByCustomers(100, activeBeatRouteId, )
    }

    private void populateLocationUpdatesList(){
        list = mydb.getAllLocationUpdatesData();
    }

    private void populateVisitPendingCustomersList(int activeBeatRouteId){
        list = mydb.getAllPendingCustomerVisitData(activeBeatRouteId);
    }

    private void populateVisitedCustomersList(int activeBeatRouteId){
        list = mydb.getAllVisitedCustomerData(activeBeatRouteId);
        Log.d("SSM", "In populateVisitedCustomersList...................");
        //TextView titleTextView = (TextView) findViewById(R.id.listTitle);
        //titleTextView.setText("asasda jjk");
        //titleTextView.append(" (" + list.size() + ")");

        /*for(int i=0; i<list.size(); i++){
            HashMap<String, String> hm = list.get(i);
            Log.d("SSM Loop", hm.get("customer_name"));
            Log.d("SSM Loop", hm.get("address"));
            //Log.d("SSM Loop", hm.get("phone"));
        }*/

    }

    private void populateNewCustomersList(){
        list = mydb.getAllNewCustomerData();
    }

    private void populateAllCustomersList(int activeBeatRouteId){
        list = mydb.getAllCustomerData(activeBeatRouteId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_visited_counters, menu);
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
