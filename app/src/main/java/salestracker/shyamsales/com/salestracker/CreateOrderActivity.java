package salestracker.shyamsales.com.salestracker;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CreateOrderActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private DBHelper mydb;
    String item[];
    private ArrayAdapter<String> adapter;

    TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        mydb = new DBHelper(this);

        ArrayList<String> array_list = mydb.getAllItem();
        item = new String[array_list.size()];

        for(int i=0; i<array_list.size(); i++){
            item[i] = array_list.get(i);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);

        table_layout = (TableLayout) findViewById(R.id.orderForm);

        addRow();
        addRow();









    }


    public void addRow(){

        TableLayout itemRowTable = new TableLayout(this);
        itemRowTable.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        Resources resource = this.getResources();
        row.setBackgroundColor(resource.getColor(R.color.switch_thumb_normal_material_light));
        row.setBackgroundColor(0xff0000);
        AutoCompleteTextView acTextView = new AutoCompleteTextView(this);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);
        acTextView.setOnItemSelectedListener(this);
        acTextView.setOnItemClickListener(this);
        acTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        //acTextView.setGravity(Gravity.CENTER);
        acTextView.setTextSize(18);
        //acTextView.setPadding(0, 5, 0, 5);
        row.addView(acTextView);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        EditText qtyEt = new EditText(this);


        /*TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        //tv.setBackgroundResource(R.drawable.cell_shape);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setPadding(0, 5, 0, 5);
        tv.setText("Data");*/

        Button delBtn = new Button(this);
        delBtn.setWidth(20);
        delBtn.setText("X");
        delBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(this, "Server Host Saved", Toast.LENGTH_SHORT).show();

                View row = (View) v.getParent().getParent();
                // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
                ViewGroup container = ((ViewGroup)row.getParent());
                //Log.d("", container.get)
                // delete the row and invalidate your view so it gets redrawn
                container.removeView(row);
                container.invalidate();


            }
        });
        //row2.addView(tv);
        row2.addView(qtyEt);
        row2.addView(delBtn);
        itemRowTable.addView(row);
        itemRowTable.addView(row2);
        table_layout.addView(itemRowTable);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_order, menu);
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
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
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
