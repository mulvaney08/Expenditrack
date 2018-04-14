package com.expenditrack.expenditrack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Get Data From Firebase into Android: https://www.captechconsulting.com/blogs/firebase-realtime-database-android-tutorial
//https://stackoverflow.com/questions/39800547/read-data-from-firebase-database
public class viewReceipts extends AppCompatActivity {


    String username = "aaron";

    //Views
    ListView receiptsListView;
    Spinner spinner;
    EditText search;

    ArrayAdapter<Receipt> arrayAdapter;
    ReceiptAdapter adapter;

    //Variables
    ArrayList<Receipt> receiptList;
    int spinnerLocation;
    ArrayList<Receipt> filterList;
    Boolean listIsFiltered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_receipts2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        try {
//            Utils.loadReceipts();
//        } catch (Exception e) {
//            Toast.makeText(this, "Cannot load receipts", Toast.LENGTH_SHORT).show();
//        }

        receiptList = Utils.receipts;
        adapter = new ReceiptAdapter(this, receiptList);

        receiptsListView = findViewById(R.id.receiptsListView);


        //Spinner
        //Spinner spinner  = (Spinner) findViewById(R.id.receiptsFilterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.seQuestionArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);
        //final TextView receiptTv = (TextView) findViewById(R.id.receiptTv);
        //Button viewDbBtn = (Button) findViewById(R.id.dbBtn);

        Toolbar toolbar = findViewById(R.id.viewReceiptsToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        receiptsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent it = new Intent(view.getContext(), editReceipt.class);
                it.putExtra("Supplier", receiptList.get(position).getSupplierName());
                it.putExtra("Total", receiptList.get(position).getTotalSpent());
                it.putExtra("Buyer", receiptList.get(position).getUsername());
                it.putExtra("ID", receiptList.get(position).getId());

                if (listIsFiltered == false) {
                    Receipt item = receiptList.get(position);
                } else {
                    Receipt item = filterList.get(position);
                }

                startActivity(it);
            }
        });

        getContents();
        search = findViewById(R.id.searchReceipts);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//       filter();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        try{
//            Utils.loadReceipts();
//        } catch (Exception e){
//            Toast.makeText(this, "Cannot load receipts",Toast.LENGTH_SHORT).show();
//        }
    }

    public void getContents() {

        receiptsListView.setAdapter(null);
        receiptsListView.setAdapter(adapter);

        if (Utils.receipts.size() > 0) {
            Snackbar receiptsLoaded = Snackbar.make(findViewById(R.id.activity_view_receipts2), "Receipts Loaded", Snackbar.LENGTH_LONG);
            receiptsLoaded.show();
        } else {
            Snackbar notLoaded = Snackbar.make(findViewById(R.id.activity_view_receipts2), "No receipts to show", Snackbar.LENGTH_LONG);
            notLoaded.show();
        }
    }

    public void filter() {
        filterList = new ArrayList<>();
        final ReceiptAdapter filterAdapter = new ReceiptAdapter(this, filterList);

        String searchText = search.getText().toString();
        filterList.clear();
        for (int j = 0; j < receiptList.size(); j++) {
            if (receiptList.get(j).getSupplierName().toLowerCase().contains(searchText.toLowerCase()) ||
                    receiptList.get(j).getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
                    receiptList.get(j).getTimeStamp().toLowerCase().contains(searchText.toLowerCase()) ||
                    receiptList.get(j).getCategory().toLowerCase().contains(searchText.toLowerCase()) ||
                    receiptList.get(j).getTotalSpent().contains(searchText)) {
                Receipt newReceipt = (receiptList.get(j));
                filterList.add(newReceipt);
            } else {

            }
        }

        receiptsListView.setAdapter(null);
        receiptsListView.setAdapter(filterAdapter);

    }

}
