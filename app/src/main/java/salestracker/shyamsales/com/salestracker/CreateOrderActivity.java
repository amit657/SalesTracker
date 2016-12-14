package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;


public class CreateOrderActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private DBHelper mydb;
    String item[];
    String sku[];
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> skuAdapter;

    private String orderId;

    TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        setTitle("Create Order");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);


        final AutoCompleteTextView acTextView = (AutoCompleteTextView)findViewById(R.id.customers_ac);
        acTextView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        acTextView.setAdapter(adapter);

        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
               // acTextView.setTag(null);

                String orderId = mydb.getOrderIdForCustomer(acTextView.getText().toString());
                if(orderId != null && !orderId.equals("")){
                    Toast.makeText(getBaseContext(), "Order exist for  - ." + acTextView.getText().toString(),
                            Toast.LENGTH_LONG).show();
                    //acTextView.setTextColor(Color.RED);

                    new AlertDialog.Builder(CreateOrderActivity.this)
                            .setTitle("Order")
                            .setMessage("You already have an order for this customer. Your existing  order will be erased. Continue?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    acTextView.setText("");
                                }})
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    addRow();
                                }}).show();

                   acTextView.setBackgroundColor(Color.RED);

                }else{
                    acTextView.setBackgroundColor(Color.GREEN);
                    addRow();
                }
            }
        });

        table_layout = (TableLayout) findViewById(R.id.orderForm);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("orderId");
            Log.d("onCreate", "Order ID Received: " + orderId);
            String custName = mydb.getCustomerForOrderId(orderId);
            acTextView.setText(custName);
            loadExistingOrder(orderId);

        }else{
            orderId = UUID.randomUUID().toString();
        }




        //addRow();
        //addRow();
/*
        mydb.insertItem ("Amla Candy 500GM", 105.15, 14.0, 24.0, "Pcs", "Cs", 20, "500GM");
        mydb.insertItem ("Cow's Whole Milk Powder 500GM", 155, 14.0, 60, "Pcs", "Cs", 12.5, "500GM");
        mydb.insertItem ("Atta Noodles Chatpata 60GM", 8.9, 14.0, 128.0, "Pcs", "Cs", 12.5, "60GM");
        mydb.insertItem ("Marie Biscuit 300GM", 26.67, 14.0, 30, "Pcs", "Cs", 12.5, "300GM");
*/

        ArrayList<String> sku_list = mydb.getAllItem();
        sku = new String[sku_list.size()];

        for(int i=0; i<sku_list.size(); i++){
            sku[i] = sku_list.get(i);
        }
        skuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sku);
    }


    @Override
    public boolean onSupportNavigateUp(){
        Log.d("", "onSupportNavigateUp===================>>>>>");
        onBackPressed();
        /*finish();*/
        return true;
    }

    public void loadExistingOrder(String orderId){

        ArrayList<HashMap<String, String>> orderItems;// = new ArrayList<HashMap<String, String>>();

        orderItems = mydb.getOrderDetails(orderId);
        for(int i=0; i<orderItems.size(); i++){
            HashMap<String, String> hm = orderItems.get(i);
            String skuName = hm.get("sku_name");
            String qty = hm.get("quantity");
            String unit = hm.get("unit");
            addRow(skuName, Integer.parseInt(qty), unit);
//            Log.d("SKU", sku[i]);
        }
        updateTitleText();



    }


    @Override
    public void onBackPressed() {

        if(table_layout.getChildCount() < 2){
            finish();
            return;
        }
        Log.d("","Back button pressed!!!!!!!");
        new AlertDialog.Builder(CreateOrderActivity.this)
                .setTitle("Order")
                .setMessage("Save Order?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(saveOrder()){
                            finish();
                        }

                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }}).show();
    }


    public void addRow(String sku, int qty, String unit){
        Log.d("addRow", "SKU:" + sku + "  Qty: " + qty + "  Unit: "+ unit);
        LayoutInflater inflator = this.getLayoutInflater();
        TableRow rowView = new TableRow(this);
        inflator.inflate(R.layout.order_item, rowView);

        AutoCompleteTextView acSkuTextView = (AutoCompleteTextView)rowView.findViewById(R.id.sku_ac);
        acSkuTextView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        acSkuTextView.setAdapter(skuAdapter);
        acSkuTextView.setOnItemSelectedListener(this);
        acSkuTextView.setOnItemClickListener(this);
        acSkuTextView.setText(sku);

        EditText qtyEditText = (EditText)rowView.findViewById(R.id.qtyEt);
        qtyEditText.setText(String.valueOf(qty));

        CheckBox caseCb = (CheckBox)rowView.findViewById(R.id.caseUnitCb);
        if(unit.equals("Case")){
            caseCb.setChecked(true);
        }

        table_layout.addView(rowView);
        updateTitleText();

    }

    public void addRow(){
        LayoutInflater inflator = this.getLayoutInflater();
        TableRow rowView = new TableRow(this);
        inflator.inflate(R.layout.order_item, rowView);

        AutoCompleteTextView acSkuTextView = (AutoCompleteTextView)rowView.findViewById(R.id.sku_ac);
        acSkuTextView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        acSkuTextView.setAdapter(skuAdapter);
        acSkuTextView.setOnItemSelectedListener(this);
        acSkuTextView.setOnItemClickListener(this);

        table_layout.addView(rowView);
        acSkuTextView.requestFocus();
        updateTitleText();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_create_order, menu);
        return true;*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_order, menu);
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

        if (id == R.id.add_order_item) {
            /*Toast.makeText(getBaseContext(), "Add item clicked",
                    Toast.LENGTH_LONG).show();*/
            addRow();
        }

        if (id == R.id.save_order) {
            saveOrder();
        }
        if(id == R.id.delete_order){
            deleteOrder();
        }

        if(id == R.id.home){
            onBackPressed();


        }
        return super.onOptionsItemSelected(item);
    }



    public void deleteOrder(){


        new AlertDialog.Builder(CreateOrderActivity.this)
                .setTitle("Delete Order")
                .setMessage("Are you sure yu want to delete this order?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String cust = mydb.getCustomerForOrderId(orderId);
                        mydb.removeOrderForCustomer(cust);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //finish();
                    }}).show();
    }



    public boolean saveOrder(){
        Log.d("SAVE","---------- IN SAVE ORDER ---------------");
        AutoCompleteTextView acTextView = (AutoCompleteTextView)findViewById(R.id.customers_ac);
        String customerName = acTextView.getText().toString();
        if(customerName.equals("")){
            Toast.makeText(getBaseContext(), "Please select a customer.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("SAVE","---------- Starting Loop ---------------");
        ArrayList<HashMap<String, String>> orderItems = new ArrayList<HashMap<String, String>>();
        float totalAmount = 0;

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());


        StringBuilder logBody = new StringBuilder("\n\n####### SAVE - "+ formattedDate +" ######\n===> " + customerName + " <====");
        for (int i = 1; i < table_layout.getChildCount(); i++) {
            View parentRow = table_layout.getChildAt(i);
            if(parentRow instanceof TableRow){
                Log.d("SAVE","---------- parentRow instanceof TableRow found ---------------");
                //RelativeLayout rl = (RelativeLayout) ((TableRow) parentRow).getChildAt(0);
                //AutoCompleteTextView skuTv = (AutoCompleteTextView) ((TableRow) parentRow).getChildAt(0);
                AutoCompleteTextView skuTv = (AutoCompleteTextView) parentRow.findViewById(R.id.sku_ac);
                EditText qtyEt = (EditText) parentRow.findViewById(R.id.qtyEt);
                CheckBox caseCb = (CheckBox) parentRow.findViewById(R.id.caseUnitCb);
                String skuName = skuTv.getText().toString();
                //Log.d("SAVE-LOOP", "SKU FOUND: " + skuName);
                if(!skuName.equals("")) {
                    Log.d("SAVE-LOOP", "SKU FOUND: " + skuName);

                    String qtyStr = qtyEt.getText().toString();
                    Log.d("SAVE-LOOP", "Quantity: " + qtyStr);
                    if(qtyStr.equals("")){
                        Toast.makeText(getBaseContext(), "Please enter quantity for " + skuName,
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                    boolean isCase = caseCb.isChecked();
                    String unit = isCase ? "Case" : "Piece";
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("sku", skuName);
                    hm.put("qty", qtyStr);
                    hm.put("unit", unit);
                    orderItems.add(hm);
                    logBody.append("\nCustomer: " + customerName + " Item: " + skuName + ", Qty: " + qtyStr + " Unit: " + unit);
                    Log.d("saveOrder()", "Added to order list: " + skuName + "   " + qtyStr + "   " + unit + " is checkbox selected:" + caseCb.isChecked());
                }
            }

        }
        updateTitleText();

        writeFileOnInternalStorage(this, logBody.toString());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", MODE_PRIVATE);




        boolean retVal;
        if(orderItems.size() == 0){
            Toast.makeText(getBaseContext(), "Nothing to save!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        boolean insertStatus = mydb.insertOrder(orderId, customerName, orderItems);
        if(!insertStatus){
            Toast.makeText(getBaseContext(), "Error saving data. Customer might have already ordered!",
                    Toast.LENGTH_SHORT).show();
            retVal = false;
        }else{
            mydb.updateVisitStatus(customerName, "ORDER_RECEIVED", "");
            Toast.makeText(getBaseContext(), "Saved!",
                    Toast.LENGTH_SHORT).show();
            retVal = true;
        }

        if(isNetworkAvailable()){
            new ServerUpdate(CreateOrderActivity.this, pref.getString("serverHost", null), pref.getString("salesman", null), 0).execute(customerName);
            new ServerUpdateOrders(CreateOrderActivity.this, pref.getString("serverHost", null), 0).execute(customerName);
        }




        return retVal;
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sBody){
        //File file = new File(mcoContext.getFilesDir(),"salesTrackerDir");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),"salesTrackerDir");
        //File file = new File(mcoContext.getCacheDir(),"salesTrackerDir");


        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy");

        //System.out.println("Current Date: " + ft.format(dNow));
        String logFileName = "Log-" + ft.format(dNow) + ".txt";

        if(!file.exists()){
            file.mkdir();
            Log.d("Logging", "Log directory does not exist, creating a new one...");
        }

        try{
            File gpxfile = new File(file, logFileName);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody);
            writer.flush();
            writer.close();
            /*Toast.makeText(getBaseContext(), "Saved in: " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();*/
            Log.d("Logging: ", "Saved in: " + file.getAbsolutePath());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTitleText(){

        float totalAmount = 0;
        for (int i = 1; i < table_layout.getChildCount(); i++) {
            View parentRow = table_layout.getChildAt(i);
            if(parentRow instanceof TableRow){
                AutoCompleteTextView skuTv = (AutoCompleteTextView) parentRow.findViewById(R.id.sku_ac);
                EditText qtyEt = (EditText) parentRow.findViewById(R.id.qtyEt);
                CheckBox caseCb = (CheckBox) parentRow.findViewById(R.id.caseUnitCb);
                String skuName = skuTv.getText().toString();
                if(skuName.equals("")){
                    continue;
                }
                Log.d("UPD-TITLE","SKU FOUND: " + skuName);
                String qtyStr = qtyEt.getText().toString();
                if(qtyStr.equals("")){
                    continue;
                }
                boolean isCase = caseCb.isChecked();
                HashMap itemHm = mydb.getItemInfoForItemName(skuName);
                Float rate = (Float) itemHm.get("net_rate");
                if(isCase){
                    Float conversion = (Float)itemHm.get("conversion");
                    Log.d("updateTitleText",skuName + "  --  Conversion: "+conversion);
                    totalAmount = totalAmount + ((Integer.parseInt(qtyStr) * conversion) * rate);
                }else{
                    totalAmount = totalAmount + (Integer.parseInt(qtyStr) * rate);
                }

                //Log.d("saveOrder()", "Added to order list: " + skuName + "   " + qtyStr + "   " + unit + " is checkbox selected:" + caseCb.isChecked());
            }
        }
        setTitle("₹ " + String.valueOf((int)Math.ceil(totalAmount)) + "/-");


    }

    public void removeOrderItem_Click(View v){

        final TableRow parent = (TableRow) v.getParent().getParent();
        table_layout.removeView(parent);
        updateTitleText();
    }


    public void displayItemInfo_Click(View v){

        final TableRow parent = (TableRow) v.getParent().getParent();

        AutoCompleteTextView skuTv = (AutoCompleteTextView) parent.findViewById(R.id.sku_ac);
        String tSkuName = skuTv.getText().toString();
        if(tSkuName.equals("")){
            return;
        }

        HashMap itemHm = mydb.getItemInfoForItemName(tSkuName);
        Float rate = (Float) itemHm.get("net_rate");
        Float mrp = (Float) itemHm.get("mrp");


        new AlertDialog.Builder(CreateOrderActivity.this)
                .setTitle(skuTv.getText())
                .setMessage("Net Rate: " + rate + "\nMRP: " +mrp )
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {



                    }}).show();


        /*Toast.makeText(getBaseContext(), skuTv.getText(),
                Toast.LENGTH_LONG).show();*/
        //updateTitleText();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        Log.d("AutocompleteContacts", "onItemSelected() position " + position);
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

    }
}
