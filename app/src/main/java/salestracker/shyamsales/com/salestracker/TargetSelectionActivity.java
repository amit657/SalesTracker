package salestracker.shyamsales.com.salestracker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class TargetSelectionActivity extends ListActivity {

    ArrayList<HashMap<String,String>> list;
    private DBHelper mydb;

    String activeBeatRouteName;
    int activeBeatRouteId;
    int targetCustomerCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_selection);

        setContentView(R.layout.activity_target_selection);
        mydb = new DBHelper(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        //Toast.makeText(this, pref.getString("salesman", null), Toast.LENGTH_SHORT).show();
        activeBeatRouteId = pref.getInt("activeBeatRouteId",999999);

        if(activeBeatRouteId == 999999) {
            Toast.makeText(getBaseContext(), "You need to set active beat route first. Goto settings screen to do that.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        activeBeatRouteName = mydb.getBeatRouteNameForId(activeBeatRouteId);
        Log.d("SSM", "Beat route name: " + activeBeatRouteName + " for ID: " + activeBeatRouteId);
        TextView beatRouteTv = (TextView) findViewById(R.id.beatRouteName);
        beatRouteTv.setText(activeBeatRouteName);

        int activeBeatRouteId = pref.getInt("activeBeatRouteId", 999999);
        if(activeBeatRouteId == 999999){
            Toast.makeText(this, "Please make a beat route active in settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        list = mydb.getAllCustomerDataForBeat(activeBeatRouteId);

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.customer_basic_list_item,
                new String[] {"customer_name","address","phone"},
                new int[] {R.id.text1,R.id.text2, R.id.text3}
        );


        setListAdapter(adapter);

        ArrayList<String> targetCustomers = mydb.getAllTargetCustomer(activeBeatRouteId);
        for(int i=0; i<list.size(); i++){
            if(targetCustomers.contains(list.get(i).get("customer_name"))){
                getListView().setItemChecked(i, true);
                targetCustomerCount++;
            }
        }
        TextView titleTextView = (TextView) findViewById(R.id.listTitle);
        titleTextView.setText("Target List (" + targetCustomerCount + "/" + list.size() + ")");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_target_selection, menu);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d("SSM", "Clicked! "+ position);
        String customerName = list.get(position).get("customer_name");
        String beatRouteId = list.get(position).get("beat_route_id");
        if(l.isItemChecked(position)){
            Log.d("ListView", "Is checked " + position + " Customer name:" + list.get(position));
            mydb.insertCustomerIntoTarget(customerName, activeBeatRouteId);
            targetCustomerCount++;
        }else{
            mydb.removeCustomerFromTarget(customerName);
            Log.d("ListView", "Is unchecked " + position);
            targetCustomerCount--;
        }

        TextView titleTextView = (TextView) findViewById(R.id.listTitle);
        titleTextView.setText("Target List (" + targetCustomerCount + "/" + list.size() + ")");

        //v.setSelected(true);
            //l.getChildAt(position).setBackgroundColor(Color.GREEN);
    }
}
