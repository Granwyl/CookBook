package id.ac.cookbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddRecipeActivity extends AppCompatActivity {
    EditText etNama, etBahan, etLangkah;
    User user;
    Spinner spinner;
    Recipe recipe;

    ProgressDialog progressDialog;
    ActivityResultLauncher<Intent> launcher;
    int GALLERY_IMAGE_REQ_CODE=105;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etNama = findViewById(R.id.etAddRecipeNama);
        etBahan = findViewById(R.id.etAddRecipeBahan);
        etLangkah = findViewById(R.id.etAddRecipeLangkah);
        spinner = findViewById(R.id.spinnerAddRecipe);

        progressDialog = new ProgressDialog(AddRecipeActivity.this);

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
        }


        Button btnBrowse=findViewById(R.id.btnBrowse);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ImagePicker.with(AddRecipeActivity.this)
                        // Crop Image(User can choose Aspect Ratio)
                        .crop()
                        // User can only select image from Gallery
                        .galleryOnly()

                        .galleryMimeTypes(new String[]{"image/png",
                                "image/jpg",
                                "image/jpeg"
                        })
                        // Image resolution will be less than 1080 x 1920
                        .maxResultSize(1080, 1920)
                        // .saveDir(getExternalFilesDir(null))
                        .start(GALLERY_IMAGE_REQ_CODE);


            //Intent intent=ImagePicker.Companion.with(AddRecipeActivity.this).createIntent();
                //launcher.launch(intent);
            }
        });
    }


    Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            uri = data.getData();

            if (requestCode== GALLERY_IMAGE_REQ_CODE)
            {
                ImageView im=(ImageView) findViewById(R.id.gambar);

                Picasso.get()
                        .load(uri)
                        .into(im);

                    //mProfileUri = uri;
                    //ImageViewExtensionKt.setLocalImage(imgProfile, uri, true);
                    //break;

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }



    public void browse(View v)
    {
        //Intent intent = new Intent(this, SomeActivity.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_loginregister, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_logreg_home){
            Intent toHome = new Intent(AddRecipeActivity.this, MainActivity.class);
            toHome.putExtra("user", user);
            startActivity(toHome);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addRecipeClick(View v){
        if (v.getId() == R.id.btnAddRecipe){
//            Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            addRecipe();
        }
    }

    void addRecipe(){
        String nama = etNama.getText().toString();
        String bahan = etBahan.getText().toString();
        String langkah = etLangkah.getText().toString();
        String kategori = spinner.getSelectedItem().toString();

        if (nama.isEmpty()){
            etNama.setError("Nama resep perlu diisi!");
            etNama.requestFocus();
            return;
        }
        if(bahan.isEmpty()){
            etBahan.setError("Bahan-bahan perlu diisi!");
            etBahan.requestFocus();
            return;
        }
        if (langkah.isEmpty()){
            etLangkah.setError("Langkah-langkah perlu diisi!");
            etLangkah.requestFocus();
            return;
        }

        int idKategori = getSpinnerValuePosition(spinner, kategori) + 1;

        doAddRecipeToServer(nama, ""+user.getId(), ""+idKategori, bahan, langkah);
    }

    private RequestQueue rQueue;

    String upload_URL=DbContract.ADD_BARANG;

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    byte[] inputData;
    public void doAddRecipeToServer(final String title, final String idUser, final String idCategory, final String ingredients, final String steps){
        if (checkNetworkConnection()) {
            progressDialog.show();

            //File file = new File(uri.getPath());
            //RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual filename
            //MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            try {
                InputStream iStream =   getContentResolver().openInputStream(uri);
                inputData = getBytes(iStream);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("err");
                Toast.makeText(getApplicationContext(), "Insert image please..", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            //System.out.println(response.data.toString());
                            //String hasil=response.data.toString();
                            try {

                                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                System.out.println("Hasil "+json);
                                System.out.println("aa");

                                progressDialog.dismiss();
//                                AddRecipeActivity.this.finish();
                                Intent toHome = new Intent(AddRecipeActivity.this, MainActivity.class);
                                toHome.putExtra("user", user);
                                toHome.putExtra("fragment", "myrecipe");
                                startActivity(toHome);
                            }
                            catch (Exception ex)
                            {
                                Toast.makeText(getApplicationContext(), "Insert image please..", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.getMessage());
                            System.out.println("bb");
                            Toast.makeText(getApplicationContext(), "Insert image please..", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("function", "addRecipe");
                    params.put("title", title);
                    params.put("id_user", idUser);
                    params.put("id_category", idCategory);
                    params.put("ingredients", ingredients);
                    params.put("steps", steps);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    File fr=new File(uri.getPath());
                    String name=fr.getName();
                    params.put("filename", new DataPart(name ,inputData));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(AddRecipeActivity.this);
            rQueue.add(volleyMultipartRequest);



        }





    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    int getSpinnerValuePosition(Spinner spinner, String value){
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString();
            if (item.equalsIgnoreCase(value)){
                position = i;
                break;
            }
        }
        return position;
    }

    void clearInput(){
        etNama.setText("");
        etBahan.setText("");
        etLangkah.setText("");
        spinner.setSelection(0);
    }
}