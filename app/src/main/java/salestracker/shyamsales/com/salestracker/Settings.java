package salestracker.shyamsales.com.salestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Settings extends ActionBarActivity {

    private DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText smEditText = (EditText) findViewById(R.id.salesManNameEt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);

        //Toast.makeText(this, pref.getString("salesman", null), Toast.LENGTH_SHORT).show();
        smEditText.setText(pref.getString("salesman", null));
        mydb = new DBHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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


    public void saveSalesMan_click(View view){

        EditText smEditText = (EditText) findViewById(R.id.salesManNameEt);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SalesTrackerPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("salesman", smEditText.getText().toString());
        editor.commit();

        //prefs.edit().putString("salesman", smTextView.getText().toString());
        Toast.makeText(this, "Salesman Saved", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllNewCustomer_click(View view){

        new AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Do you really want to delete all new customers?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        mydb.deleteAllNewCustomerData();
                        Toast.makeText(Settings.this, "New customers cleared from database.", Toast.LENGTH_SHORT).show();

                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }
}
