package com.expenditrack.expenditrack;

/**
 * Created by Aaron on 23/10/2017.
 * <p>
 * Reference to project developed by Fung LAM
 * Fung LAM, Cloud-Vision, (), Github repository, https://github.com/GoogleCloudPlatform/cloud-vision.git
 */

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.common.io.BaseEncoding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Provides utility logic for getting the app's SHA1 signature. Used with restricted API keys.
 *
 */
public class Utils {

    private static String uniqueID;
    public static DatabaseReference mRef;
    private static FirebaseDatabase mDatabase;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    public static DatabaseReference receiptRef;
    public static DatabaseReference usersRef;
    public static DatabaseReference receiptIDReference;

    static ArrayList<String> usernames = new ArrayList<>();
    static ArrayList<String> secAnswers = new ArrayList<>();
    static ArrayList<String> secQs = new ArrayList<>();
    static ArrayList<String> pWords = new ArrayList<>();
    static ArrayList<Receipt> receipts = new ArrayList<>();

    static Receipt r1 = new Receipt();
    ;

    //public static String updateKey = receiptRef.child().getKey();


    /**
     * Gets the SHA1 signature, hex encoded for inclusion with Google Cloud Platform API requests
     *
     * @param packageName Identifies the APK whose signature should be extracted.
     * @return a lowercase, hex-encoded
     */
    public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static void loadUserInfo() {
        try {
            Utils.setUserReference();
            Utils.usersRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        usernames.add(ds.child("username").getValue().toString());
                        secQs.add(ds.child("secQuestion").getValue().toString());
                        pWords.add(ds.child("passwd").getValue().toString());
                        secAnswers.add(ds.child("secAnswer").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
        }
    }

    public static void loadReceipts(){
        try {
            Utils.initialiseFBase(LoginActivity.username);
            Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        r1 = new Receipt();
                        r1.setUsername(ds.child("username").getValue().toString());
                        r1.setSupplierName(ds.child("supplierName").getValue().toString());
                        r1.setTotalSpent(ds.child("totalSpent").getValue().toString());
                        r1.setTimeStamp(ds.child("timeStamp").getValue().toString());
                        r1.setCategory(ds.child("category").getValue().toString());
                        r1.setId(ds.child("id").getValue().toString());
                        receipts.add(r1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
        }
    }

    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static void initialiseFBase(String username) {
        receiptRef = databaseReference.child("users").child(username).child("receipts");
    }

    public static void setUserReference(){
        getDatabase();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://expenditrack-184010.firebaseio.com/");
        usersRef = databaseReference.child("Users");
    }

    public static FirebaseDatabase getDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase;
    }

    public static void writeReceipt(Receipt r) {
        try {
            receiptRef.child(r.getId()).setValue(r);
        }catch (Exception e){

        }
    }

    public static void writeUser(User u) {
        usersRef.child(u.getUsername()).setValue(u);
    }

    public static void hideSoftKeyboard(View view, Object o) {
        if(view!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) o;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}