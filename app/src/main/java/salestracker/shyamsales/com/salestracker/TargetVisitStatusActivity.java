package salestracker.shyamsales.com.salestracker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class TargetVisitStatusActivity extends ListActivity {


    ArrayList<HashMap<String,String>> list;
    private DBHelper mydb;

    String activeBeatRouteName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_visit_status);

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
        Log.d("SSM", "Beat route name: " + activeBeatRouteName + " for ID: " + beatRouteId);
        TextView beatRouteTv = (TextView) findViewById(R.id.beatRouteName);
        beatRouteTv.setText(activeBeatRouteName);



        int activeBeatRouteId = pref.getInt("activeBeatRouteId", 999999);
        if(activeBeatRouteId == 999999){
            Toast.makeText(this, "Please make a beat route active in settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap<String, String>> allTargetCustomerDetails = mydb.getAllTargetDataWithStatus(activeBeatRouteId);
        ArrayList<HashMap<String, String>> locationUpdateDetails = mydb.getAllLocationUpdatesDataForBeat(activeBeatRouteId);
        ArrayList<String> locationUpdateCustomer = new ArrayList<String>();

        ArrayList<HashMap<String, String>> targetPenidngList = new ArrayList<HashMap<String, String>>();

        for(int i=0; i< locationUpdateDetails.size(); i++){
            locationUpdateCustomer.add(locationUpdateDetails.get(i).get("customer_name"));
        }

        for(int i=0; i<allTargetCustomerDetails.size(); i++){
            //check if customer exist in location update request table
            if(locationUpdateCustomer.contains(allTargetCustomerDetails.get(i).get("customer_name"))){
                continue;
            }
            // check if visit_status is visited
            if(allTargetCustomerDetails.get(i).get("visit_status") != null){
                continue;
            }
            targetPenidngList.add(allTargetCustomerDetails.get(i));

        }


        TextView titleTextView = (TextView) findViewById(R.id.listTitle);
        titleTextView.setText("Target Pending (" + targetPenidngList.size() + ") ");

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                targetPenidngList,
                R.layout.customer_basic_list_item_status,
                new String[] {"customer_name","address","phone"},
                new int[] {R.id.text1,R.id.text2, R.id.text3}
        );

        setListAdapter(adapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_target_visit_status, menu);
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
