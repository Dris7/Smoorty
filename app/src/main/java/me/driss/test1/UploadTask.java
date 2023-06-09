package me.driss.test1;
import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class UploadTask extends AsyncTask<Uri, Void, String> {
    private static final String TAG = UploadTask.class.getSimpleName();
    private static  String BASE_URL = "";

    private Context context;
    private EditText textView;
    private ProgressDialog progressDialog;

    public UploadTask(Context context , EditText textView ,  ProgressDialog progressDialog) {
        this.context = context;
        this.textView = textView ;
        this.progressDialog = progressDialog;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String key = sharedPreferences.getString("ngrok_key", "");
           BASE_URL =  "https://"+key+".ngrok-free.app/";
    }

    @Override
    protected String doInBackground(Uri... uris) {
        Uri imageUri = uris[0];

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                byte[] fileBytes = getBytes(inputStream);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), fileBytes);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", "image.jpg", requestBody);

                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build();

                // Create an instance of the API service
                ApiService apiService = retrofit.create(ApiService.class);

                // Make the API call
                Call<ResponseBody> call = apiService.uploadImage(imagePart);
                Response<ResponseBody> response = call.execute();
                if (response.isSuccessful()) {
                    // Image upload successful
                    return response.body().string();
                } else {
                    // Image upload failed
                    Log.d(TAG, "Image upload failed");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // Upload successful
            Log.d(TAG, "Upload successful. Response: " + result);
            textView.setText(result);
            progressDialog.hide();
        } else {
            // Upload failed
            Log.d(TAG, "Upload failed.");
        }
    }

}
