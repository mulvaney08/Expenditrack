package com.expenditrack.expenditrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;
import java.lang.*;

/**
 * Created by Aaron on 23/10/2017.
 * <p>
 * Reference to project developed by Fung LAM
 * Fung LAM, Cloud-Vision, (), Github repository, https://github.com/GoogleCloudPlatform/cloud-vision.git
 */
public class Main extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private String username = "Aaron";

    private Uri file;
    public String filePath = "";
    String timeStamp = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss").format(new Date());
    public String FILE_NAME = "IMG_" + timeStamp + ".jpg";

    private File currentImage;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("Aaron/Receipts/" + FILE_NAME);

    String message = "";
    private String supplier = "";
    private String category = "";
    String apiResponse = "\n\nThis is what we found:\n\n";
    String totalAmount = "";
    String cardAmount = "";

    int indexStartOfCard = 0;
    int indexEndOfCard;
    int indexOfTansaction;

    private static final String CLOUD_VISION_API_KEY = "AIzaSyA526OGaeqpaM0yIHtImKRRuSDzr_N0eDA";

    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = Main.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private String speechResponse;

    private TextView mImageDetails;


    private ImageView mMainImage;

    protected static final int RESULT_SPEECH = 5;


    private DatabaseReference mDatabase;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public void uploadToFBase(File image) {
        Uri file = Uri.fromFile(image);
        UploadTask uploadTask = storageRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        Log.d("find me",""+Utils.receiptRef.toString());
        setContentView(R.layout.activity_main);

        final Intent speech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        TextView userWelcome = findViewById(R.id.userWelcome);
        userWelcome.setText(username);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final Intent viewReceiptsIntent = new Intent(this, viewReceipts.class);
        final Intent viewGraphsIntent = new Intent(this, viewGraphs.class);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //Menu menu = navigationView.getMenu();
                        // set item as selected to persist highlight
                        // close drawer when item is tapped

                        if (menuItem.getItemId() == R.id.viewReceipts) {
                            menuItem.setChecked(true);
                            startActivity(viewReceiptsIntent);
                        } else if (menuItem.getItemId() == R.id.viewGraphs) {
                            menuItem.setChecked(true);
                            startActivity(viewGraphsIntent);
                        } else if (menuItem.getItemId() == R.id.speechAdd){
                            menuItem.setChecked(true);
                            try {
                                startActivityForResult(speech, RESULT_SPEECH);
                            } catch (ActivityNotFoundException a) {
                                Toast t = Toast.makeText(getApplicationContext(),
                                        "Opps! Your device doesn't support Speech to Text",
                                        Toast.LENGTH_SHORT);
                                t.show();
                            }
                        }


                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        return true;
                    }
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        final Intent addAlertIntent = new Intent(this, addAlert.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder
                        .setMessage("Choose an action")
                        .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        })
                        .setNeutralButton("Add Alert", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(addAlertIntent);
                            }

                        });

                builder.create().show();
            }
        });

        //response = (LinearLayout) findViewById(R.id.);
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    public void startGalleryChooser() {
        if (CheckPermissions.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void startCamera() {
        if (CheckPermissions.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            currentImage = getCameraFile();
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        } else if (requestCode == RESULT_SPEECH && null != data){
            ArrayList<String> text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            speechResponse = text.get(0);
            convertSpeech(speechResponse);
        }
    }

    private void convertSpeech(String speechResponse){

        String supplier = "";
        String totalAmount = "";
        String category = "";

        if(speechResponse != null){
            for (int i = 0; i <= speechResponse.length(); i++){
                supplier = speechResponse.substring(speechResponse.lastIndexOf(" in ") + 4, speechResponse.length());
                totalAmount = speechResponse.substring(speechResponse.lastIndexOf("€") + 1, speechResponse.lastIndexOf(" in "));
            }
        }

        callConfirmReceipt(supplier,totalAmount);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (CheckPermissions.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (CheckPermissions.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                uploadToFBase(currentImage);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Error with image", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Error with image", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.scan_image);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = Utils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature textDetection = new Feature();
                            textDetection.setType("TEXT_DETECTION");
                            textDetection.setMaxResults(10);
                            add(textDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);


                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {


        String card;
        String tempString;
        String totalPrefix = "TRANSACTION";
        message = "";
        cardAmount = "";
        supplier = "";

        List<EntityAnnotation> text = response.getResponses().get(0).getTextAnnotations();
        if (text != null) {
            //Check if response is null
            for (EntityAnnotation words : text) {
                //Loop through words in response
//                apiResponse += String.format(Locale.ENGLISH, words.getDescription());
                apiResponse += words.getDescription();

                apiResponse += "\n";
                //Start Powercity example, testing on Powercity receipt -----------------------------------------------------------------------------------------------------------------
                if (words.getDescription().contains("POWERCITY")) {
                    //If the receipt contains POWERCITY then set the shop name to be POWERCITY
                    supplier = "POWERCITY";
                    category = "Electronics";
                    //Handling different variations of how the api extracts the line containing Card
                    //Variation 1 card captured with colon afterwards Card:
                    if (words.getDescription().contains("Card:")) {
                        //If the payment type card was found
                        tempString = words.getDescription();
                        card = "Card:";
                        //Getting the index at the start of the sequence Card:
                        indexStartOfCard = words.getDescription().indexOf(card);
                        //Getting the index at the end of the sequence Card:
                        indexEndOfCard = indexStartOfCard + 5;
                        //storing the expected double after Card: expected to be 6 characters long including the decimal to separate euros and cents
                        tempString = words.getDescription().substring(indexEndOfCard, indexEndOfCard + 6);
                        //Call method to check if the string value in tempString can be parsed to double to ensure the storage of random strings does not occur.
                        if (checkIfDouble(tempString)) {
                            totalAmount = tempString;
                        } else {
                            totalAmount = "0";
                        }
                    }
                    //Variation 2 card captured with space afterwards Card
                    else if (words.getDescription().contains("Card ")) {
                        //If the payment type card was found
                        tempString = words.getDescription();
                        card = "Card ";
                        //Getting the index at the start of the sequence Card with space
                        indexStartOfCard = words.getDescription().indexOf(card);
                        //Getting the index at the end of the sequence Card with space
                        indexEndOfCard = indexStartOfCard + 5;
                        //storing the expected double after Card with space expected to be 6 characters long including the decimal to separate euros and cents
                        tempString = words.getDescription().substring(indexEndOfCard, indexEndOfCard + 7);
                        //Call method to check if the string value in tempString can be parsed to double to ensure the storage of random strings does not occur.
                        if (checkIfDouble(tempString)) {
                            totalAmount = tempString;
                        } else {
                            totalAmount = "0";
                        }
                    }
                    //Variation 3 card captured without colon or space afterwards just the number needed to capture total spent on card
                    else if (!words.getDescription().contains("Card ") && !words.getDescription().contains("Card:") && words.getDescription().contains("Card")) {
                        //If the payment type card was found
                        tempString = words.getDescription();
                        card = "Card";
                        //Getting the index at the start of the sequence Card without space
                        indexStartOfCard = words.getDescription().indexOf(card);
                        //Getting the index at the end of the sequence Card without space
                        indexEndOfCard = indexStartOfCard + 4;
                        //storing the expected double after Card without space expected to be 6 characters long including the decimal to separate euros and cents
                        tempString = words.getDescription().substring(indexEndOfCard, indexEndOfCard + 6);
                        //Call method to check if the string value in tempString can be parsed to double to ensure the storage of random strings does not occur.
                        if (checkIfDouble(tempString)) {
                            totalAmount = tempString;
                        } else {
                            totalAmount = "0";
                        }
                    }
                }
                //End Powercity example, testing on Powercity receipt -----------------------------------------------------------------------------------------------------------
                //Start Copan example, testing on Copan receipt -----------------------------------------------------------------------------------------------------------------
                else if (words.getDescription().contains("copan") || words.getDescription().contains("COPAN") || words.getDescription().contains("Copan")) {

                    supplier = "Copan Limited";
                    int indexOfDecimal = 0;
                    int indexOfEndOfDecimal = 0;
                    category = "Alcohol";

                    //tempString = words.getDescription().substring(words.getDescription().indexOf("TOTAL") - 6, words.getDescription().indexOf("TOTAL") - 5);

                    //if (checkIfDouble(tempString) && checkIfInt(tempString)) {
                    if (words.getDescription().contains("TOTAL")) {

                        indexOfDecimal = words.getDescription().indexOf("TOTAL") - 26;
                        indexOfEndOfDecimal = words.getDescription().indexOf("TOTAL") - 22;

//                                if (checkIfDouble(words.getDescription().substring(indexOfDecimal, indexOfEndOfDecimal))) {
//                                }

                        totalAmount = words.getDescription().substring(indexOfDecimal, indexOfEndOfDecimal);

                    }
                }
                //End Copan example, testing on Copan receipt -----------------------------------------------------------------------------------------------------------
                //Start Subway example, testing on Subway receipt -----------------------------------------------------------------------------------------------------------------
                else if (words.getDescription().contains("subway") || words.getDescription().contains("Subway") || words.getDescription().contains("SUBWAY")) {

                    supplier = "Subway";
                    category = "Take-Aways/Restaurants";
                    String currency = "EUR";
                    for (int i = 0; i < words.getDescription().lastIndexOf(currency); i++) {
                        if (words.getDescription().contains(currency)) {
//                                totalAmount = words.getDescription().substring(words.getDescription().lastIndexOf(currency), words.getDescription().lastIndexOf(currency +5));
                            totalAmount = words.getDescription().substring(words.getDescription().lastIndexOf(currency) - 15, words.getDescription().lastIndexOf(currency) - 10);
                        }
                    }
                }
                //End Subway example, testing on Subway receipt -----------------------------------------------------------------------------------------------------------
                //Start Halfords example, testing on Halfords receipt -----------------------------------------------------------------------------------------------------------------

                else if (words.getDescription().contains("halfords") || words.getDescription().contains("Halfords") || words.getDescription().contains("HALFORDS")) {

                    supplier = "Halfords";
                    category = "Other";
                    String currency = "EUR";
                    for (int i = 0; i < words.getDescription().lastIndexOf(currency); i++) {
                        if (words.getDescription().contains(currency)) {
//                                totalAmount = words.getDescription().substring(words.getDescription().lastIndexOf(currency), words.getDescription().lastIndexOf(currency +5));
                            totalAmount = words.getDescription().substring(words.getDescription().lastIndexOf(currency) - 5, words.getDescription().lastIndexOf(currency) - 10);
                        }
                    }
                }
                //End Halfords example, testing on Halfords receipt -----------------------------------------------------------------------------------------------------------
                //Start - example, testing on - receipt -----------------------------------------------------------------------------------------------------------------
            }
        } else {

            message += "nothing";
        }

        message += "Shop Name: " + supplier + "\nTotal spent: " + totalAmount + "\nTime: " + timeStamp;


        callConfirmReceipt(supplier,totalAmount);

        return message + apiResponse;
        //return message;

    }

    public Intent pushValuesToReceipt(Intent intent, String supplierName, String totalAmount, String category) {
        intent.putExtra("Supplier", supplierName);
        intent.putExtra("Total", totalAmount);
        intent.putExtra("Buyer", username);
        intent.putExtra("Category", category);

        return intent;
    }
//
//    public void callEditReceipt(String supplierName, String totalAmount){
//        Intent intent = new Intent(this,editReceipt.class);
//
//        startActivity(pushValuesToReceipt(intent,supplierName,totalAmount));
//    }

    public void callConfirmReceipt(String supplierName, String totalAmount) {
        Intent intent = new Intent(this, ConfirmReceipt.class);
        startActivity(pushValuesToReceipt(intent, supplierName, totalAmount, category));
    }

    boolean checkIfDouble(String stringIn) {
        try {
            Double.parseDouble(stringIn);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    boolean checkIfInt(String stringIn) {
        try {
            Integer.parseInt(stringIn);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}