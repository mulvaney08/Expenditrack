package com.expenditrack.expenditrack;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editReceipt extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private String supplier_name;

    Button edit;
    private DatabaseReference mDatabase;
    String supplier;
    String total;
    String buyer;
    String id;
    Receipt oldReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_receipt);

//        Main.initialiseFBase(mDatabase);
        //Utils.initialiseFBase();
        Intent myIntent = this.getIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//
//        supplier_name = myIntent.getStringExtra("supplier");
//        supplier_text_field = (EditText)findViewById(R.id.supplier_name_field);
//        supplier_text_field.setText(supplier_name);
//        if(myIntent.hasExtra("supplier")){
//            EditText mText = (EditText) findViewById(R.id.supplier_name_field);
//            mText.setText(myIntent.getStringExtra("supplier"));
//        }


        supplier = myIntent.getStringExtra("Supplier");
        total = myIntent.getStringExtra("Total");
        buyer = myIntent.getStringExtra("Buyer");
        id = myIntent.getStringExtra("ID");

        final EditText supplierName = findViewById(R.id.supplier_name_field);
        final EditText totalSpent = findViewById(R.id.total_spent_field);
        final EditText buyerView = findViewById(R.id.buyer_name_field);

        supplierName.setText(supplier);
        totalSpent.setText(total);
        buyerView.setText(buyer);

        dateView = findViewById(R.id.showDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        Spinner spinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, R.layout.category_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener();

        edit = findViewById(R.id.editReceipt);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        oldReceipt = new Receipt(buyerView.getText().toString(), supplierName.getText().toString(), totalSpent.getText().toString(), dateView.getText().toString(), id);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Utils.receiptRef.child(id).setValue(oldReceipt);
                        }
//                        if(dataSnapshot.getValue() != null){
//                            Utils.writeReceipt(oldReceipt,Utils.getID());
//
//                        } else{
//                            writeNewReceipt(buyerView.getText().toString(),supplierName.getText().toString(),totalSpent.getText().toString(),dateView.getText().toString(), Utils.generateRandomID());
//
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //writeNewReceipt(buyerView.getText().toString(),supplierName.getText().toString(),totalSpent.getText().toString(),dateView.getText().toString(),);
                viewReceipts();
            }
        });

    }

    private void viewReceipts() {
        Intent viewReceipt = new Intent(this, viewReceipts.class);
        startActivity(viewReceipt);
    }

    private void writeNewReceipt(String username, String supplierName, String totalAmount, String timeStamp, String id, String category) {
        Receipt r1 = new Receipt(username, supplierName, totalAmount, timeStamp, category);

        Utils.writeReceipt(r1);
//        mDatabase.child("users").child(username).child("receipts").child("item12").setValue(r1);
//        mDatabase.child("users").child("receipts").setValue(r1);
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Select Purchase Date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


}
