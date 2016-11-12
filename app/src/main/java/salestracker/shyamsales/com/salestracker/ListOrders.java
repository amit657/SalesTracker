package salestracker.shyamsales.com.salestracker;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
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


public class ListOrders extends ListActivity {

    ArrayList<HashMap<String,String>> list;
    private DBHelper mydb;

    String activeBeatRouteName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_orders);


        mydb = new DBHelper(this);
        loadListView();



    }

    @Override
    protected void onResume(){
        super.onResume();
        loadListView();

    }

    public void loadListView(){

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


        //populateLocationUpdatesList(activeBeatRouteId);
        list = mydb.getOrderedCustomersForBeat(beatRouteId);


        TextView titleTextView = (TextView) findViewById(R.id.listTitle);
        titleTextView.setText("Orders (" + list.size() + ") ");
        titleTextView.setTextColor(Color.BLUE);



        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.customer_basic_list_item,
                new String[] {"customer_name","address","phone"},
                new int[] {R.id.text1,R.id.text2, R.id.text3}
        );

        setListAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_orders, menu);
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

        Log.d("SSM", "Clicked! "+ position + " Value: " + list.get(position));
        HashMap<String, String> hm = list.get(position);
        Intent intent = new Intent(this, CreateOrderActivity.class);
        //Intent intent = new Intent(this, TargetSelectionActivity.class);
        intent.putExtra("orderId", hm.get("order_id"));
        startActivity(intent);

        /*String customerName = list.get(position).get("customer_name");
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
*/
        //v.setSelected(true);
        //l.getChildAt(position).setBackgroundColor(Color.GREEN);
    }



}
