package com.expenditrack.expenditrack;

/**
 * Created by Aaron on 23/10/2017.
 *
 * Reference to project developed by Fung LAM
 * Fung LAM, Cloud-Vision, (), Github repository, https://github.com/GoogleCloudPlatform/cloud-vision.git
 *
 */

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;

import com.google.common.io.BaseEncoding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
    public static DatabaseReference receiptIDReference;
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

    public static void initialiseFBase(){
        getDatabase();

        databaseReference = firebaseDatabase.getReferenceFromUrl("https://expenditrack-184010.firebaseio.com/");
        receiptRef = databaseReference.child("users").child("aaron").child("receipts");
        receiptIDReference = databaseReference.child("receiptIDs");
    }

    public static FirebaseDatabase getDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase;
    }

    public static void writeReceipt(Receipt r){
        receiptRef.child(r.getId()).setValue(r);
    }

//    public static String generateRandomID(){
//        uniqueID = UUID.randomUUID().toString();
//        addReceiptIDToList();
//        return uniqueID;
//    }
//
//    public static String getID(){
//        return uniqueID;
//    }

//    public static void addReceiptIDToList(){
//        receiptIDReference.child(getID()).setValue(getID());
//    }
}