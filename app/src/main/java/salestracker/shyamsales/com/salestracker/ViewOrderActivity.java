package salestracker.shyamsales.com.salestracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ViewOrderActivity extends ActionBarActivity {

    private String orderId = "";
    private DBHelper mydb;

    private StringBuilder orderDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        setTitle("View Order");
        orderDetails = new StringBuilder("");
        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("orderId");
        }

        String custName = mydb.getCustomerForOrderId(orderId);
        HashMap custHm = new HashMap();
        custHm = mydb.getCustomerInfo(custName);
        orderDetails.append(custName);
        orderDetails.append("\n");
        orderDetails.append(custHm.get("address"));
        orderDetails.append("\n");
        orderDetails.append("Phone: " + custHm.get("phone"));

        orderDetails.append("\n\n");


        ArrayList<HashMap<String, String>> orderItems;// = new ArrayList<HashMap<String, String>>();
        float totalAmount = 0;
        orderItems = mydb.getOrderDetails(orderId);
        for(int i=0; i<orderItems.size(); i++){
            HashMap<String, String> hm = orderItems.get(i);
            String skuName = hm.get("sku_name");
            String qty = hm.get("quantity");
            String unit = hm.get("unit");

            orderDetails.append(i + 1);
            orderDetails.append(". ");
            orderDetails.append(skuName);
            orderDetails.append(" -  ");
            orderDetails.append(qty);
            orderDetails.append(" ");
            orderDetails.append(unit);
            orderDetails.append("\n");

            HashMap itemHm = mydb.getItemInfoForItemName(skuName);
            Float rate = (Float) itemHm.get("net_rate");
            if(unit.equals("Case")){
                Float conversion = (Float)itemHm.get("conversion");
                totalAmount = totalAmount + ((Integer.parseInt(qty) * conversion) * rate);
            }else{
                totalAmount = totalAmount + (Integer.parseInt(qty) * rate);
            }
        }
        orderDetails.append("\n\n");
        orderDetails.append("Amount: ");
        orderDetails.append(Math.ceil(totalAmount));
        TextView tv = (TextView) findViewById(R.id.orderDetailsTv);
        tv.setText(orderDetails.toString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_order, menu);
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
