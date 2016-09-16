package salestracker.shyamsales.com.salestracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public static final String BEAT_ROUTE_COLUMN_NAME = "beat_route_name";
    public static final String CUSTOMER_COLUMN_NAME = "customer_name";
    public static final String CUSTOMER_COLUMN_ADDRESS = "address";
    public static final String CUSTOMER_COLUMN_PHONE = "phone";
    public static final String CUSTOMER_COLUMN_LATITUDE = "latitude";
    public static final String CUSTOMER_COLUMN_LONGITUDE = "longitude";
    public static final String CUSTOMER_COLUMN_VISIT_STATUS = "visit_status";
    public static final String CUSTOMER_COLUMN_REASON = "reason";
    public static final String CUSTOMER_COLUMN_DATE_UPDATED = "date_updated";
    public static final String NEW_CUSTOMER_COLUMN_BEAT_ROUTE = "beat_route";
    public static final String COLUMN_ID = "id";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 13);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table customer_details " +
                        "(customer_name text primary key, address text, phone text, latitude text,longitude text, visit_status text, reason text, date_updated DATETIME)"
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
                        "(item_name text primary_key, net_rate real, tax number, conversion real, primary_unit text, alternate_unit text, margin real, pack_size text)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS beat_route_master " +
                        "(beat_route_id int primary_key, beat_route_name text)"
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

        onCreate(db);
    }

    public boolean insertCustomer  (String customer_name, String address, String phone, String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_name", customer_name);
        contentValues.put("address", address);
        contentValues.put("phone", phone);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
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

    public boolean insertItem  (String itemName, double netRate, double tax, double conversion, String primaryUnit, String alternateUnit, double margin, String packSize)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_name", itemName);
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
        Cursor res =  db.rawQuery( "select * from customer_details where customer_name='"+ customer_name +"'", null );
        System.out.println(">>>>>>>>>>>>>>>> select * from customer_details where customer_name='"+ customer_name +"'");
        HashMap hm = new HashMap();
        res.moveToFirst();
        System.out.println("=================>>>>> " + res.getString(1));
        hm.put("customer_name", res.getString(0));
        hm.put("address", res.getString(1));
        hm.put("phone", res.getString(2));
        hm.put("latitude", res.getDouble(3));
        hm.put("longitude", res.getDouble(4));
        hm.put("visit_status", res.getString(5));
        hm.put("reason", res.getString(6));


        //System.out.println(res.getString(1));
        return hm;
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
        db.update("customer_details", contentValues, "customer_name =  '" + customer_name + "'", null );
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

    public void updateCustomerNoOrder(String customerName, String reason){

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


    public ArrayList<String> getAllCustomer()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details order by customer_name asc", null );
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

    public ArrayList<HashMap<String, String>> getAllCustomerData()
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details order by customer_name asc", null );
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


    public ArrayList<HashMap<String, String>> getAllLocationUpdatesData()
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



    public ArrayList<HashMap<String, String>> getAllVisitedCustomerData()
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where visit_status is not null order by customer_name asc", null );
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


    public ArrayList<HashMap<String, String>> getAllPendingCustomerVisitData()
    {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from customer_details where visit_status is null order by customer_name asc", null );
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



    public boolean deleteAllNewCustomerData(){
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
