package com.expenditrack.expenditrack;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ConfirmReceipt extends AppCompatActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private String supplier_name;

    Button confirm;
    private DatabaseReference mDatabase;
    String supplier;
    String total;
    String buyer;
    String category;
    Receipt receipt;

    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_receipt);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent myIntent = this.getIntent();

        supplier = myIntent.getStringExtra("Supplier");
        total = myIntent.getStringExtra("Total");
        buyer = LoginActivity.username;
        category = myIntent.getStringExtra("Category");


        final TextView supplierName = findViewById(R.id.supplier_name_field_confirm);
        final TextView totalSpent = findViewById(R.id.total_spent_field_confirm);
        final TextView buyerView = findViewById(R.id.buyer_name_field_confirm);
        loading = findViewById(R.id.loading_reg);
        loading.setVisibility(View.GONE);

        supplierName.setText(supplier);
        totalSpent.setText(total);
        buyerView.setText(buyer);

        dateView = findViewById(R.id.showDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        Toolbar toolbar = findViewById(R.id.confirmToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


        final Spinner spinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, R.layout.category_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        try {
            for (int i = 0; i < 17; i++) {
                if (spinner.getAdapter().getItem(i).toString().contains(category)) {
                    spinner.setSelection(i);
                }
            }
        } catch (Exception e) {

        }


        confirm = (Button) findViewById(R.id.confirmReceipt);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (buyerView.getText().toString().matches("") || supplierName.getText().toString().matches("") || totalSpent.getText().toString().matches("") || dateView.getText().toString().matches("")) {
                        Toast.makeText(ConfirmReceipt.this, R.string.all_fields_please, Toast.LENGTH_SHORT).show();
                    } else {
                        receipt = new Receipt(buyerView.getText().toString(), supplierName.getText().toString().toUpperCase(), totalSpent.getText().toString(), dateView.getText().toString(), spinner.getSelectedItem().toString());
                        writeNewReceipt(receipt);
                        Utils.receipts.clear();

                        Utils.loadReceipts();
                        Utils.hideSoftKeyboard(getCurrentFocus(), getSystemService(INPUT_METHOD_SERVICE));
                        loading.setVisibility(View.VISIBLE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ConfirmReceipt.this, viewReceipts.class);
                                loading.setVisibility(View.GONE);
                                Toast.makeText(ConfirmReceipt.this, R.string.added, Toast.LENGTH_SHORT);
                                startActivity(intent);
                            }
                        }, 3000);
                    }
                } catch (Exception e) {
                    Toast.makeText(ConfirmReceipt.this, R.string.issure_adding, Toast.LENGTH_SHORT);
                }

            }
        });

    }

    private void writeNewReceipt(Receipt r) {
        //id = Utils.generateRandomID();
        Utils.writeReceipt(r);
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
