package salestracker.shyamsales.com.salestracker;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ListCountersGeneric extends ListActivity {

    ArrayList<HashMap<String,String>> list;
    private DBHelper mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mydb = new DBHelper(this);
        setContentView(R.layout.activity_list_counters_generic);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("ListType");
            if(value.equals("VisitedCustomersList")){
                populateVisitedCustomersList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Visited Customers List (" + list.size() + ")");
                titleTextView.setTextColor(Color.GREEN);
            }
            if(value.equals("NewCustomersList")){
                populateNewCustomersList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("New Customers List (" + list.size() + ")");
                titleTextView.setTextColor(Color.BLUE);

            }

            if(value.equals("AllCustomersList")){
                populateAllCustomersList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("All Customers List (" + list.size() + ")");
                titleTextView.setTextColor(Color.CYAN);
            }

            if(value.equals("VisitPendingCustomersList")){
                populateVisitPendingCustomersList();
                TextView titleTextView = (TextView) findViewById(R.id.listTitle);
                titleTextView.setText("Pending Customer Visit List (" + list.size() + ")");
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

        if(list.size() > 0){
            TextView tv = (TextView) findViewById(R.id.no_counter_visited_tv);
            tv.setVisibility(View.INVISIBLE);
        }

        setListAdapter(adapter);

    }

    private void populateVisitPendingCustomersList(){
        list = mydb.getAllPendingCustomerVisitData();
    }

    private void populateVisitedCustomersList(){
        list = mydb.getAllVisitedCustomerData();
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

    private void populateAllCustomersList(){
        list = mydb.getAllCustomerData();
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
