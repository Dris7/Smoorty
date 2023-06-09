package me.driss.test1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.android.volley.Response;


import android.Manifest;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static  String URL = "";
    private static  String CALCULATE_URL = URL+"/calculate?equation=";
    private static final int REQUEST_IMAGE_SELECT = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static String NGROK_KEY = "";
    private static final int STORAGE_PERMISSION_CODE = 1;
    private boolean isProcessRunning = false;
    private EditText resultTextView;
    private StringBuilder equationBuilder;
    private ImageView equationView;
    private Button uploadImage;
    private String equtionString ="";
    private boolean isResultShown;
    private ImageButton takeImage;
    private ProgressDialog progressDialog;
    private  Button buttonEqual;
    private AlertDialog.Builder builder ;
    private  AlertDialog alertDialog;
    private ActivityResultLauncher<Intent> cropActivityResultLauncher;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String key = sharedPreferences.getString("ngrok_key", "");
        NGROK_KEY = key;
        URL = "https://"+NGROK_KEY+".ngrok-free.app";
        CALCULATE_URL = URL+"/calculate?equation=";

        resultTextView = findViewById(R.id.resultTextView);

        buttonEqual = findViewById(R.id.solve);
        takeImage = findViewById(R.id.takeImage);

    equationBuilder = new StringBuilder();
        isResultShown = false;

        // Set click listeners for numeric buttons
        setNumericButtonListeners();
        progressDialog = new ProgressDialog(MainActivity.this, R.style.CustomProgressDialog);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        progressDialog.setCancelable(false);
        Button ac = findViewById(R.id.delete);
        ac.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showKeyDialog();
                return true;
            }
        });


        // Set click listener for the equal button
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the app has permission to access external storage
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, open image gallery
                    openImageGallery();
                } else {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);
                }
            }
        });

    }


    private void showKeyDialog() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Key");

        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_key, null);
        builder.setView(dialogView);


        // Set up dialog views
        EditText keyEditText = dialogView.findViewById(R.id.editTextKey);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the entered key
                String key = keyEditText.getText().toString();

                // Process the entered key as needed
                // For example, you can save it or use it for ngrok server

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = keyEditText.getText().toString();
                NGROK_KEY = key;
                URL = "https://"+NGROK_KEY+".ngrok-free.app";
                CALCULATE_URL = URL+"/calculate?equation=";
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ngrok_key", NGROK_KEY);
                editor.apply();

                dialog.dismiss();
            }
        });
    }


    public void onSolve(View view){

        if(!resultTextView.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            progressDialog.setMessage("Calculating Please wait ...");
            performCalculation(resultTextView.getText().toString());
        }
    }
    private void setNumericButtonListeners() {
        // Numeric Buttons
        Button button0 = findViewById(R.id.button0);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button buttonDot = findViewById(R.id.button13);
        Button buttonPlus = findViewById(R.id.button15);
        Button buttonMin = findViewById(R.id.button12);
        Button buttonMul = findViewById(R.id.button11);
        Button buttonDiv = findViewById(R.id.button10);
        Button buttonX = findViewById(R.id.button16);
        Button buttonY = findViewById(R.id.button18);
        Button buttonZ = findViewById(R.id.button17);
        equationView = findViewById(R.id.equationImage);
        uploadImage = findViewById(R.id.uploadImage);

        View.OnClickListener numericClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();

                if (isResultShown) {
                    equationBuilder.setLength(0);
                    isResultShown = false;
                }

                equationBuilder.append(buttonText);
                resultTextView.setText(equationBuilder);

            }
        };

        button0.setOnClickListener(numericClickListener);
        button1.setOnClickListener(numericClickListener);
        button2.setOnClickListener(numericClickListener);
        button3.setOnClickListener(numericClickListener);
        button4.setOnClickListener(numericClickListener);
        button5.setOnClickListener(numericClickListener);
        button6.setOnClickListener(numericClickListener);
        button7.setOnClickListener(numericClickListener);
        button8.setOnClickListener(numericClickListener);
        button9.setOnClickListener(numericClickListener);
        buttonDot.setOnClickListener(numericClickListener);
        buttonPlus.setOnClickListener(numericClickListener);
        buttonMin.setOnClickListener(numericClickListener);
        buttonDiv.setOnClickListener(numericClickListener);
        buttonMul.setOnClickListener(numericClickListener);
        buttonX.setOnClickListener(numericClickListener);
        buttonY.setOnClickListener(numericClickListener);
        buttonZ.setOnClickListener(numericClickListener);
    }


    private void openCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA_PERMISSION);
        }else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        else {
            // Permission already granted, open the camera
            startCamera();
        }
    }



    private void startCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMG_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the cam");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }

    }


        public void performCalculation(String equation) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
          String encodedEquation ="";
        try {
            encodedEquation = URLEncoder.encode(equation, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }        // Create a StringRequest to send a GET request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CALCULATE_URL+encodedEquation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        Log.d("Response", response);
                        resultTextView.setText(response);
                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("Error", error.toString());
                    }
                });

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    private void openImageGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_SELECT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            if(data!=null && data.getData()!=null) {
                CropImage.activity(data.getData())
                        .start(this);

            }
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {

            //startCropActivity(image_uri);
            CropImage.activity(image_uri)
                    .start(this);

            }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                equationView.setImageBitmap(bitmap);
                progressDialog.setMessage("Uploading Please wait ...");
                progressDialog.show();
                UploadTask uploadTask = new UploadTask(getApplicationContext(), resultTextView , progressDialog);  // Pass the appropriate context
                uploadTask.execute(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, open image gallery
                openImageGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                startCamera();

            } else {
                Toast.makeText(MainActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startCamera();
            } else {
                Toast.makeText(MainActivity.this, "write to storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDelete(View view) {

        equationBuilder.setLength(0);
        resultTextView.setText("");


    }
}