package com.expenditrack.expenditrack;

import android.content.Context;
import android.content.Intent;
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

        //setContentView(R.layout.activity_view_receipts);
        setContentView(R.layout.activity_view_receipts2);

        //Add back button
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        receiptList = new ArrayList<>();
        adapter = new ReceiptAdapter(this, receiptList);

        //Firebase - Setup & Paths Target specific sections of Firebase - First target the actual DB, then the specified table "Dealers"


        receiptsListView = (ListView) findViewById(R.id.receiptsListView);

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
                it.putExtra("ID",receiptList.get(position).getId());

                if(listIsFiltered == false){
                    Receipt item = receiptList.get(position);
                }

                else {
                    Receipt item = filterList.get(position);
                }

                startActivity(it);
            }
        });
       getContents();
       search = (EditText) findViewById(R.id.searchReceipts);
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
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void getContents(){
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange (DataSnapshot dataSnapshot){
                receiptList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String supplierName = ds.child("supplierName").getValue().toString();
                    String timeStamp = ds.child("timeStamp").getValue().toString();
                    String totalSpent = "€" + ds.child("totalSpent").getValue().toString();
                    String username = ds.child("username").getValue().toString();
//                    String id = ds.getValue().toString();
                    String category = ds.child("category").getValue().toString();


                      Receipt newReceipt = new Receipt(username, supplierName, totalSpent, timeStamp, category);
                      receiptList.add(newReceipt);
                }
                receiptsListView.setAdapter(null);
                receiptsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
        Snackbar receiptsLoaded = Snackbar.make(findViewById(R.id.activity_view_receipts2), "Receipts Loaded", Snackbar.LENGTH_LONG);
        receiptsLoaded.show();
        //Toast.makeText(this,"Receipts Loaded",Toast.LENGTH_SHORT).show();

    }

    public void filter(){
        filterList = new ArrayList<>();
        final ReceiptAdapter filterAdapter = new ReceiptAdapter(this, filterList);

                String searchText = search.getText().toString();
                Log.d("Search Query:" , searchText);
                filterList.clear();
                for (int j = 0; j < receiptList.size();j++){
                    if(receiptList.get(j).getSupplierName().toLowerCase().contains(searchText.toLowerCase()) ||
                            receiptList.get(j).getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
                            receiptList.get(j).getTimeStamp().toLowerCase().contains(searchText.toLowerCase()) ||
                            receiptList.get(j).getCategory().toLowerCase().contains(searchText.toLowerCase()) ||
                            receiptList.get(j).getTotalSpent().contains(searchText)){
                        Receipt newReceipt = (receiptList.get(j));
                        filterList.add(newReceipt);
                        Log.d("Test", receiptList.get(j).getSupplierName());
                    }
                    else {

                    }
                }

        receiptsListView.setAdapter(null);
        receiptsListView.setAdapter(filterAdapter);

    }



//    //Spinner
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
//        if(parent.getSelectedItemPosition() == 0){
//            spinnerLocation = 0;
//            listIsFiltered = false;
//            getContents();
//        }
//
//        if(parent.getSelectedItemPosition() == 1){
//            spinnerLocation = 1;
//            listIsFiltered = true;
//            filter();
//        }
//
//        if(parent.getSelectedItemPosition() == 2){
//            spinnerLocation = 2;
//            listIsFiltered = true;
//            filter();
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

}
