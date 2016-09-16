package salestracker.shyamsales.com.salestracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

    public void settingsBtn_click(View view){
        Intent intent1 = new Intent(this, Settings.class);
        startActivity(intent1);
    }

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

    public void loadOrderingActivity_Click(View view){
        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra("ListType", "VisitPendingCustomersList");
        startActivity(intent);
    }


}
