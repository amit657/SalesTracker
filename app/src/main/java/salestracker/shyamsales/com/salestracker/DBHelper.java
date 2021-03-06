package salestracker.shyamsales.com.salestracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by amit on 8/4/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String NEW_CUSTOMER_TABLE_NAME = "new_customer_details";
    public static final String CUSTOMER_TABLE_NAME = "customer_details";
    public static final String ITEM_MASTER_TABLE_NAME = "item_master";
    public static final String LOCATION_UPDATE_REQUEST_TABLE_NAME = "location_update_requests";
    public static final String BEAT_ROUTE_MASTER_TABLE_NAME = "beat_route_master";
    public static final String CUSTOMER_VISIT_TARGET_TABLE_NAME = "visit_target";
    public static final String BEAT_ROUTE_COLUMN_NAME = "beat_route_name";
    public static final String BEAT_ROUTE_COLUMN_ID = "beat_route_id";
    public static final String CUSTOMER_COLUMN_NAME = "customer_name";
    public static final String CUSTOMER_COLUMN_ADDRESS = "address";
    public static final String CUSTOMER_COLUMN_PHONE = "phone";
    public static final String CUSTOMER_COLUMN_LATITUDE = "latitude";
    public static final String CUSTOMER_COLUMN_LONGITUDE = "longitude";
    public static final String CUSTOMER_COLUMN_VISIT_STATUS = "visit_status";
    public static final String CUSTOMER_COLUMN_REASON = "reason";
    public static final String CUSTOMER_COLUMN_DATE_UPDATED = "date_updated";
    public static final String CUSTOMER_COLUMN_ORDER_ID = "order_id";

    public static final String NEW_CUSTOMER_COLUMN_BEAT_ROUTE = "beat_route";
    public static final String COLUMN_ID = "id";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 19);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table customer_details " +
                        "(customer_name text primary key, address text, phone text, latitude text,longitude text, visit_status text, reason text, date_updated DATETIME, beat_route_id int, order_id text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS new_customer_details " +
                        "(customer_name text primary key, address text, phone text, latitude text,longitude text, beat_route text, sync_status text, date_updated DATETIME, id text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS location_update_requests " +
                        "(customer_name text primary key, latitude text,longitude text, visit_status text, reason text, sync_status text, date_updated DATETIME, id text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS item_master " +
                        "(item_id text, item_name text primary_key, mrp real, net_rate real, tax number, conversion real, primary_unit text, alternate_unit text, margin real, pack_size text, brand text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS beat_route_master " +
                        "(beat_route_id int primary_key, beat_route_name text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS visit_target " +
                        "(customer_name text primary_key, beat_route_id int)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS salesman_master " +
                        "(salesman_id int primary_key, salesman_name text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS order_details " +
                        "(order_id text, sku_name text, quantity number, unit text)"
        );

        /*db.execSQL(
                "create table settings " +
                        "(setting text primary key, options text, type text, selected_value text, date_updated DATETIME)"
        );*/ // options can be a ; separated string

        //latitude and longitude is set to text to preserve precision of floatin point numbers
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS customer_details");
        db.execSQL("DROP TABLE IF EXISTS new_customer_details");
        db.execSQL("DROP TABLE IF EXISTS item_master");
        db.execSQL("DROP TABLE IF EXISTS beat_route_master");
        db.execSQL("DROP TABLE IF EXISTS location_update_requests");

        db.execSQL("DROP TABLE IF EXISTS order_details");

        onCreate(db);
    }

    public ArrayList<HashMap<String, String>> getOrderedCustomersForBeat(int beatRouteId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where order_id != '' and beat_route_id = " + beatRouteId, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            hp.put("order_id", res.getString(res.getColumnIndex("order_id")));
            Log.d("SSM", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }



    public ArrayList<HashMap<String, String>> getOrderDetails(String orderId){

        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from order_details where order_id ='"+ orderId +"'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("sku_name", res.getString(res.getColumnIndex("sku_name")));
            hp.put("quantity", res.getString(res.getColumnIndex("quantity")));
            hp.put("unit", res.getString(res.getColumnIndex("unit")));
            //Log.d("SSM", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;


    }

    public boolean insertOrder(String orderId, String customerName, ArrayList<HashMap<String, String>> order){
        //check if customer was changed before saving
        SQLiteDatabase db = this.getWritableDatabase();
        String existingCustName = getCustomerForOrderId(orderId);
        Log.d("MyDB", "Existing Cust Name:" + existingCustName);
        if(existingCustName != null){
            Log.d("DBHelper:insertOrder()", "Order already exist");

            if(!existingCustName.equals(customerName)){
                Log.d("DBHelper:insertOrder()", "Customer name modified for an existing order");
                String eOrderId = getOrderIdForCustomer(customerName);
                if(eOrderId != null){
                    //order already exist for modified customer, no update can be performed, return
                    return false;
                }
                //Customer modified
                //remove order id from existing customer and set order_received to ''
                ContentValues contentValues = new ContentValues();
                //contentValues.put("customer_name", existingCustName);
                contentValues.put("order_id", "");
                contentValues.put("date_updated", getDateTime());
                contentValues.put("visit_status", getDateTime());
                db.update("customer_details", contentValues, "customer_name =  '" + existingCustName.replaceAll("'", "''") + "'", null );

            }
        }else{
            //new entry insert details in customer_details table
        }

        //String existOrderId = getOrderIdForCustomer(customerName);

        //if(existOrderId == null || existOrderId.equals("")){
            //insert order_id into customer master

            ContentValues contentValues = new ContentValues();
            contentValues.put("customer_name", customerName);
            contentValues.put("order_id", orderId);
            contentValues.put("date_updated", getDateTime());
            db.update("customer_details", contentValues, "customer_name =  '" + customerName.replaceAll("'", "''") + "'", null );

        //}
        deleteAllOrderItemsForOrderId(orderId);
        for (HashMap<String, String> orderItem: order) {
            // use currInstance
            String skuName = orderItem.get("sku");
            String unit = orderItem.get("unit");
            int qty = Integer.parseInt(orderItem.get("qty"));

            ContentValues contentValuesItems = new ContentValues();
            contentValuesItems.put("order_id", orderId);
            contentValuesItems.put("sku_name", skuName);
            contentValuesItems.put("quantity", qty);
            contentValuesItems.put("unit", unit);
            Log.d("SSM", "Inserting into DB ID: " + orderId);
            Log.d("DBHelper", "Inserting into DB: " + skuName);
            Log.d("DBHelper", "Inserting into DB: " + qty);
            Log.d("DBHelper", "Inserting into DB: " + unit);
            db.insert("order_details", null, contentValuesItems);
        }

        return true;
    }

    public void deleteAllOrderItemsForOrderId(String orderId){
        SQLiteDatabase db = this.getWritableDatabase();
        String stmt= "delete from order_details where order_id = '"+ orderId +"'";
        //Log.d("deleteAllOrderItemsForOrderId", stmt);
        db.execSQL(stmt);


    }

    public String getOrderIdForCustomer(String customerName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select order_id from customer_details where customer_name='"+ customerName.replaceAll("'", "''") +"'", null );
        String id = null;
        if(!res.isAfterLast()){
            res.moveToFirst();
            id = res.getString(res.getColumnIndex("order_id"));
        }

        return id;
    }

    public String getCustomerForOrderId(String orderId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select customer_name from customer_details where order_id='"+ orderId.replaceAll("'", "''") +"'", null );
        String cust = null;
        if(!res.isAfterLast()){
            res.moveToFirst();
            cust = res.getString(res.getColumnIndex("customer_name"));
        }

        return cust;
    }

    public void removeOrderForCustomer(String customerName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select order_id from customer_details where customer_name='"+ customerName.replaceAll("'", "''") +"'", null );
        String orderId = null;
        if(!res.isAfterLast()){
            res.moveToFirst();
            orderId = res.getString(res.getColumnIndex("order_id"));
        }


        if(orderId != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("customer_name", customerName);
            contentValues.putNull("order_id");
            contentValues.putNull("visit_status");
            contentValues.putNull("date_updated");
            db.update("customer_details", contentValues, "customer_name =  '" + customerName + "'", null );

            String stmt= "delete from order_details where order_id = '"+ orderId +"'";
            db.execSQL(stmt);


        }

    }

    public HashMap getItemInfoForItemName(String item_name){
/*
        CREATE TABLE IF NOT EXISTS item_master " +
        "(item_name text primary_key, net_rate real, tax number, conversion real, primary_unit text, alternate_unit text, margin real, pack_size text)"

      */

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from item_master where item_name='"+ item_name.replaceAll("'", "''") +"'", null );
        HashMap hm = new HashMap();
        res.moveToFirst();
        System.out.println("=================>>>>> " + res.getString(1));
        hm.put("item_id", res.getString(res.getColumnIndex("item_id")));
        hm.put("item_name", res.getString(res.getColumnIndex("item_name")));
        hm.put("mrp", res.getFloat(res.getColumnIndex("mrp")));
        hm.put("net_rate", res.getFloat(res.getColumnIndex("net_rate")));
        hm.put("tax", res.getFloat(res.getColumnIndex("tax")));
        hm.put("conversion", res.getFloat(res.getColumnIndex("conversion")));
        hm.put("primary_unit", res.getString(res.getColumnIndex("primary_unit")));
        hm.put("alternate_unit", res.getString(res.getColumnIndex("alternate_unit")));
        hm.put("margin", res.getFloat(res.getColumnIndex("margin")));
        hm.put("pack_size", res.getString(res.getColumnIndex("pack_size")));

        //System.out.println(res.getString(1));
        return hm;
    }

    public HashMap getItemInfoForId(String itemId){
/*
        CREATE TABLE IF NOT EXISTS item_master " +
        "(item_name text primary_key, net_rate real, tax number, conversion real, primary_unit text, alternate_unit text, margin real, pack_size text)"

      */
//item_id text, item_name text primary_key, net_rate real, tax number, conversion real, primary_unit text, alternate_unit text, margin real, pack_size text
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from item_master where item_id='"+ itemId.replaceAll("'", "''") +"'", null );
        HashMap hm = new HashMap();
        res.moveToFirst();
        System.out.println("=================>>>>> " + res.getString(1));
        hm.put("item_id", res.getString(0));
        hm.put("item_name", res.getString(1));
        hm.put("net_rate", res.getFloat(2));
        hm.put("tax", res.getFloat(3));
        hm.put("conversion", res.getFloat(4));
        hm.put("primary_unit", res.getString(5));
        hm.put("alternate_unit", res.getString(6));
        hm.put("margin", res.getFloat(7));
        hm.put("pack_size", res.getString(8));

        //System.out.println(res.getString(1));
        return hm;
    }


    public boolean insertCustomerIntoTarget(String customer, int beatRouteId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer);
        contentValues.put("beat_route_id", beatRouteId); // beat_route_id column may not be needed in this table
        db.insert("visit_target", null, contentValues);
        return true;
    }

    public boolean removeCustomerFromTarget(String customer){
        SQLiteDatabase db = this.getWritableDatabase();
        String stmt= "delete from " + CUSTOMER_VISIT_TARGET_TABLE_NAME + " where customer_name = '" + customer.replaceAll("'", "''") + "'";
        Log.d("removeCustomerFromT", stmt);
        db.execSQL(stmt);

        return true;

    }

    public ArrayList<String> getAllTargetCustomer(int beatRouteId)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CUSTOMER_VISIT_TARGET_TABLE_NAME+" where beat_route_id="+beatRouteId+" order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<HashMap<String, String>> getAllTargetDataWithStatus(int beatRouteId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details cd, visit_target vt where cd.beat_route_id = "+ beatRouteId +" and cd.customer_name = vt.customer_name order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            Log.d("SSM", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<HashMap<String, String>> getAllNearByCustomers(int distanceRange, int beatRouteId, Location currentLocation)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details cd, visit_target vt where cd.beat_route_id = "+ beatRouteId +" and cd.customer_name = vt.customer_name order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Location storeLocation = new Location("StoreLocation");
            storeLocation.setLatitude(Double.parseDouble(res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE))));
            storeLocation.setLongitude(Double.parseDouble(res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE))));

            float distance = currentLocation.distanceTo(storeLocation);
            if(distance <= distanceRange) {
                HashMap<String, String> hp = new HashMap<String, String>();
                hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
                hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
                hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
                hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
                hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
                hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
                hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
                hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
                Log.d("SSM", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
                array_list.add(hp);
            }
            res.moveToNext();
        }
        return array_list;
    }




    public boolean insertCustomer  (String customer_name, String address, String phone, String latitude, String longitude, int beatRoute)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer_name);
        contentValues.put("address", address);
        contentValues.put("phone", phone);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("beat_route_id", beatRoute);
        db.insert("customer_details", null, contentValues);
        return true;
    }

    public boolean insertLocationUpdate  (String customer_name, Location newLocation, String visitStatus, String reason )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String uniqueID = UUID.randomUUID().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer_name);
        contentValues.put("visit_status", visitStatus);
        contentValues.put("reason", reason);
        contentValues.put("latitude", newLocation.getLatitude());
        contentValues.put("longitude", newLocation.getLongitude());
        contentValues.put("date_updated", getDateTime());
        contentValues.put("id", uniqueID);
        Log.d("SSM", "Inserting into DB ID: " + uniqueID);
        db.insert("location_update_requests", null, contentValues);
        return true;
    }

    public boolean insertItem  (String itemId, String itemName, double mrp, double netRate, double tax, double conversion, String primaryUnit, String alternateUnit, double margin, String packSize)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_id", itemId);
        contentValues.put("item_name", itemName);
        contentValues.put("mrp", mrp);
        contentValues.put("net_rate", netRate);
        contentValues.put("tax", tax);
        contentValues.put("conversion", conversion);
        contentValues.put("primary_unit", primaryUnit);
        contentValues.put("alternate_unit", alternateUnit);
        contentValues.put("margin", margin);
        contentValues.put("pack_size", packSize);
        db.insert("item_master", null, contentValues);
        return true;
    }

    public boolean insertBeatRoute  (int brId, String brName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("beat_route_id", brId);
        contentValues.put("beat_route_name", brName);
        db.insert("beat_route_master", null, contentValues);
        return true;
    }

    public boolean insertSalesman  (int smId, String smName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("salesman_id", smId);
        contentValues.put("salesman_name", smName);
        db.insert("salesman_master", null, contentValues);
        return true;
    }


    public boolean insertNewCustomer  (String customer_name, String address, String phone, String latitude, String longitude, String beat_route)
    {
        String uniqueID = UUID.randomUUID().toString();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer_name);
        contentValues.put("address", address);
        contentValues.put("phone", phone);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("beat_route", beat_route);
        contentValues.put("sync_status", "PENDING");
        contentValues.put("date_updated", getDateTime());
        contentValues.put("id", uniqueID);


        //db.insert("new_customer_details", null, contentValues);
        try{
            db.insertOrThrow("new_customer_details", null, contentValues);
            return true;
        }catch(Exception e){
            Log.d("SSM", "Unique constraint failed!!");
            return false;
        }


    }



    public Cursor getData(String customer_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where customer_name='"+ customer_name +"'", null );
        System.out.println(res.getString(1));
        return res;
    }


    public HashMap getCustomerInfo(String customer_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where customer_name='"+ customer_name.replaceAll("'", "''") +"'", null );
        System.out.println(">>>>>>>>>>>>>>>> select * from customer_details where customer_name='"+ customer_name +"'");
        HashMap hp = new HashMap();
        res.moveToFirst();
        System.out.println("=================>>>>> " + res.getString(1));

        hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
        hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
        hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
        hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
        hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
        hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
        hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
        hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
        hp.put("order_id", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ORDER_ID)));

        /*
        hm.put("customer_name", res.getString(0));
        hm.put("address", res.getString(1));
        hm.put("phone", res.getString(2));
        hm.put("latitude", res.getDouble(3));
        hm.put("longitude", res.getDouble(4));
        hm.put("visit_status", res.getString(5));
        hm.put("reason", res.getString(6));
        hm.put("date_updated", res.getString(7));*/

        //System.out.println(res.getString(1));
        return hp;
    }

/*
    public boolean updateCustomerLocation (String customer_name, Location location)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", location.getLatitude());
        contentValues.put("longitude", location.getLongitude());
        Log.d("DB Update", "Latitude:" + location.getLatitude() + " ;Longitude:" + location.getLongitude());
        db.update("customer_details", contentValues, "customer_name = ? ", new String[] { customer_name } );
        return true;
    }
*/
    public boolean updateVisitStatus (String customer_name, String visitStatus, String reason)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer_name);
        contentValues.put("visit_status", visitStatus);
        contentValues.put("reason", reason);
        contentValues.put("date_updated", getDateTime());
        db.update("customer_details", contentValues, "customer_name =  '" + customer_name.replaceAll("'", "''") + "'", null );
        return true;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public Integer deleteCustomer (String customer_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("customer_details",
                "customer_name = ? ",
                new String[] { customer_name });
    }

    public void deleteAllTarget (int beatRouteId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from visit_target where beat_route_id = " + beatRouteId);
        /*return db.delete("customer_details",
                "customer_name = ? ", new String[] {"*"});*/
    }

    public void deleteAllData ()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + CUSTOMER_TABLE_NAME);
        /*return db.delete("customer_details",
                "customer_name = ? ", new String[] {"*"});*/
    }

    public void deleteAllItems (){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + ITEM_MASTER_TABLE_NAME);
        /*return db.delete("customer_details",
                "customer_name = ? ", new String[] {"*"});*/
    }

    public void deleteAllBeatRoutes (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + BEAT_ROUTE_MASTER_TABLE_NAME);
    }

    public void deleteAllSalesman (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from salesman_master");
    }

    public void updateCustomerNoOrder(String customerName, String reason){

    }

    public String getBeatRouteIdForName(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select beat_route_id from "+ BEAT_ROUTE_MASTER_TABLE_NAME +" where beat_route_name = '" + title + "'", null );
        if(res.getCount() > 0) {
            Log.d("getBeatRouteIdForName","reading beat route ID");
            res.moveToFirst();
            String brId;
            brId = res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_ID));
            Log.d("getBeatRouteIdForName","beat route ID: " + brId);
            return brId;
        }else{
            return null;
        }
    }

    public String getBeatRouteNameForId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select beat_route_name from "+ BEAT_ROUTE_MASTER_TABLE_NAME +" where beat_route_id = " + id, null );
        res.moveToFirst();
        String brName;
        if(res.getCount() > 0){
            brName = res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_NAME));
            Log.d("getBeatRouteNameForId", res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_NAME)));
            return brName;
        }else{
            Log.d("getBeatRouteNameForId", res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_NAME)));
            return null;
        }

    }

    public ArrayList<String> getAllBeatRoutes()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ BEAT_ROUTE_MASTER_TABLE_NAME +" order by beat_route_id asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_NAME)));
            Log.d("SSM getAllBeatRoutes", res.getString(res.getColumnIndex(BEAT_ROUTE_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllSalesman()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from salesman_master order by salesman_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("salesman_name")));
            Log.d("SSM getAllSalesman", res.getString(res.getColumnIndex("salesman_name")));
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<String> getAllCustomer(int beatRouteId)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from customer_details where beat_route_id="+beatRouteId+" order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllItem()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from item_master order by item_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("item_name")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<HashMap<String, String>> getAllCustomerDataForBeat(int activeBeatRouteId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where beat_route_id = "+ activeBeatRouteId +" order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            hp.put("order_id", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ORDER_ID)));

            Log.d("SSM", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<HashMap<String, String>> getAllNewCustomerData()
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from new_customer_details order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("beat_route", res.getString(res.getColumnIndex(NEW_CUSTOMER_COLUMN_BEAT_ROUTE)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            hp.put("id", res.getString(res.getColumnIndex(COLUMN_ID)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<HashMap<String, String>> getAllLocationUpdateData()
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from location_update_requests order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("id", res.getString(res.getColumnIndex(COLUMN_ID)));
            Log.d("DBHelper", "Loc update ID:" + res.getString(res.getColumnIndex(COLUMN_ID)) );
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<HashMap<String, String>> getAllLocationUpdatesDataForBeat(int beatId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from location_update_requests where customer_name in (select customer_name from customer_details where beat_route_id = "+ beatId +" ) order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("id", res.getString(res.getColumnIndex(COLUMN_ID)));
            Log.d("DBHelper", "Loc update ID:" + res.getString(res.getColumnIndex(COLUMN_ID)) );
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }



    public ArrayList<HashMap<String, String>> getAllVisitedCustomerData(int activeBeatRouteId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();

        ArrayList<String> locationUpdateData = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from customer_details where beat_route_id ="+activeBeatRouteId+" and visit_status is not null order by customer_name asc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }


    public ArrayList<HashMap<String, String>> getAllPendingCustomerVisitData(int activeBeatRouteId)
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();

        ArrayList<HashMap<String, String>> locationUpdatedList = new ArrayList<HashMap<String, String>>();
        locationUpdatedList = getAllLocationUpdatesDataForBeat(activeBeatRouteId);
        ArrayList<String> locationUpdateCustomerNameList = new ArrayList<String>();
        for(int i=0;i<locationUpdatedList.size(); i++){
            locationUpdateCustomerNameList.add(locationUpdatedList.get(i).get("customer_name"));
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where beat_route_id = "+ activeBeatRouteId +" and visit_status is null order by customer_name asc", null );
        res.moveToFirst();
        Log.d("SSM","getAllPendingCustomerVisitData: select * from customer_details where beat_route_id = "+ activeBeatRouteId +" and visit_status is null order by customer_name asc");
        while(res.isAfterLast() == false){
            Log.d("SSM", "In database read loop.");
            String customerName = res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME));
            if(locationUpdateCustomerNameList.contains(customerName)){
                Log.d("SSM","Customer pending update location request, excluding from pending visit list..." + customerName);
                res.moveToNext();
                continue;
            }
            HashMap<String, String> hp = new HashMap<String, String>();
            hp.put("customer_name", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_NAME)));
            hp.put("address", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_ADDRESS)));
            hp.put("phone", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_PHONE)));
            hp.put("latitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LATITUDE)));
            hp.put("longitude", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_LONGITUDE)));
            hp.put("visit_status", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_VISIT_STATUS)));
            hp.put("reason", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_REASON)));
            hp.put("date_updated", res.getString(res.getColumnIndex(CUSTOMER_COLUMN_DATE_UPDATED)));
            array_list.add(hp);
            res.moveToNext();
        }
        return array_list;
    }



    public boolean deleteAllNewCustomerData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + NEW_CUSTOMER_TABLE_NAME);
        return true;
    }


    public boolean deleteAllLocationUpdateRequests(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + LOCATION_UPDATE_REQUEST_TABLE_NAME);
        return true;
    }
}
