package salestracker.shyamsales.com.salestracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.lang.annotation.Target;


public class MainActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Stetho.initializeWithDefaults(this);

    }

    public void loadCustomerStatusUpdate(View view){
        Intent intent1 = new Intent(this, UpdateCustomerStatusActivity.class);
        startActivity(intent1);
    }

    public void loadDataRefreshActivity(View view){
        Intent intent1 = new Intent(this, DataUpdateActivity.class);
        startActivity(intent1);
    }

    public void addNewCustomer_Click(View view){
        Intent intent1 = new Intent(this, AddNewCustomerActivity.class);
        startActivity(intent1);
    }
/*
    public void listNewCustomer_Click(View view){
        Intent intent = new Intent(this, ListCountersGeneric.class);
        intent.putExtra("ListType", "NewCustomersList");
        startActivity(intent);
    }

    public void listVisitedCustomer_Click(View view){
        Intent intent = new Intent(this, ListCountersGeneric.class);
        intent.putExtra("ListType", "VisitedCustomersList");
        startActivity(intent);
    }

    public void listAllLocationUpdates_click(View view){
        Intent intent = new Intent(this, ListCountersGeneric.class);
        intent.putExtra("ListType", "LocationUpdatesList");
        startActivity(intent);
    }
*/
    public void settingsBtn_click(View view){
        Intent intent1 = new Intent(this, Settings.class);
        startActivity(intent1);
    }
/*
    public void listAllCustomers_click(View view){
        Intent intent = new Intent(this, ListCountersGeneric.class);
        intent.putExtra("ListType", "AllCustomersList");
        startActivity(intent);
    }

    public void listPendingCustomerVisited_Click(View view){
        Intent intent = new Intent(this, ListCountersGeneric.class);
        intent.putExtra("ListType", "VisitPendingCustomersList");
        startActivity(intent);
    }
*/
    public void loadOrderingActivity_Click(View view){
        //Intent intent = new Intent(this, CreateOrderActivity.class);
        Intent intent = new Intent(this, TargetSelectionActivity.class);
        intent.putExtra("ListType", "VisitPendingCustomersList");
        startActivity(intent);
    }

    public void loadSetTarget_Click(View view){
        //Intent intent = new Intent(this, CreateOrderActivity.class);
        Intent intent = new Intent(this, TargetSelectionActivity.class);
        startActivity(intent);
    }

    public void loadTargetStatus_Click(View view){
        Intent intent = new Intent(this, TargetVisitStatusActivity.class);
        startActivity(intent);
    }


    public void showListCustomersOptions_Click(View view){
        CharSequence colors[] = new CharSequence[] {"New Customers", "Visited Customers", "Location Updates", "", "All Customers", "Pending Visit", "Pending Target", "Nearby Customers"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select list type");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                Log.d("SSM", "Clicked on " + position);
                if(position == 0){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "NewCustomersList");
                    startActivity(intent);
                }
                if(position == 1){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "VisitedCustomersList");
                    startActivity(intent);
                }
                if(position == 2){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "LocationUpdatesList");
                    startActivity(intent);
                }
                if(position == 4){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "AllCustomersList");
                    startActivity(intent);
                }
                if(position == 5){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "VisitPendingCustomersList");
                    startActivity(intent);
                }
                if(position == 6){
                    Intent intent = new Intent(MainActivity.this, TargetVisitStatusActivity.class);
                    startActivity(intent);
                }
                if(position == 7){
                    Intent intent = new Intent(MainActivity.this, ListCountersGeneric.class);
                    intent.putExtra("ListType", "NearbyCustomersList");
                    startActivity(intent);
                }
                // the user clicked on colors[which]
            }
        });
        builder.show();
    }


}
