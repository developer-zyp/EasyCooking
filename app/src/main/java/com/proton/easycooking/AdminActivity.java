package com.proton.easycooking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.proton.easycooking.models.Category;
import com.proton.easycooking.models.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int MY_PERMISSIONS_REQUEST_READ_ExternalStorage = 100;
    private static final String API_KEY = "be8e8230e823841d8306ecde624a1a22";
    private static final String Expire = "0";
    APIInterface apiInterface;

    EditText edtId, edtName, edtDesc, edtImage;
    Button btnUpload, btnUploadImg, btnDeleteImg;
    ImageView previewImg;
    Spinner spnCategory, spnPost;
    RadioButton rdoR, rdoC, rdoP, rdoSet, rdoSnack;

    String result, delete_url;
    int status;
    File uploadFile;

    ArrayAdapter<SpnItem> adapter;
    List<Category> categoryList = new ArrayList<>();
    List<Category> postList = new ArrayList<>();
    String categoryId = "0", postId = "0";

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        apiInterface = APIClient.getService().create(APIInterface.class);

        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtImage = findViewById(R.id.edtImage);
        previewImg = findViewById(R.id.imgUpload);
        btnUpload = findViewById(R.id.btnUpload);
        btnUploadImg = findViewById(R.id.btnUploadImg);
        btnDeleteImg = findViewById(R.id.btnDeleteImg);
        spnCategory = findViewById(R.id.spnCategory);
        spnPost = findViewById(R.id.spnPost);
        rdoR = findViewById(R.id.rdoR);
        rdoC = findViewById(R.id.rdoC);
        rdoP = findViewById(R.id.rdoP);
        rdoSet = findViewById(R.id.rdoSet);
        rdoSnack = findViewById(R.id.rdoSnack);

        NestedScrollView scrollDesc = findViewById(R.id.scroll_desc);

        rdoR.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edtId.setVisibility(View.VISIBLE);
                scrollDesc.setVisibility(View.VISIBLE);
                spnCategory.setVisibility(View.VISIBLE);
                spnPost.setVisibility(View.VISIBLE);
            } else {
                edtId.setVisibility(View.GONE);
                scrollDesc.setVisibility(View.GONE);
                spnCategory.setVisibility(View.GONE);
                spnPost.setVisibility(View.GONE);
            }
        });

        GetCategoryList();
        GetPostList();

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpnItem item = (SpnItem) adapterView.getSelectedItem();
                categoryId = item.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnPost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpnItem item = (SpnItem) adapterView.getSelectedItem();
                postId = item.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtId.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (rdoR.isChecked()) {
                    GetRecipeById(textView.getText().toString());
                } else if (rdoC.isChecked()) {
                    GetCategoryList();
                } else {
                    GetPostList();
                }
            }
            return false;
        });

        edtImage.setOnClickListener(view -> {
            if (!edtImage.getText().toString().equals(""))
                AppTools.openURL(this, edtImage.getText().toString());
        });

        previewImg.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(AdminActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_ExternalStorage);
            } else {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, REQUEST_GALLERY_CODE);
            }
        });

        btnUploadImg.setOnClickListener(view -> {
            try {
                AppTools.showProgressDialog(this, "Uploading...");
                uploadImgBB(uploadFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnDeleteImg.setVisibility(View.GONE);
        btnDeleteImg.setOnClickListener(view -> {
            AppTools.openURL(this, delete_url);
            btnDeleteImg.setVisibility(View.GONE);
        });

        btnUpload.setOnClickListener(view -> {
            AppTools.showProgressDialog(this, "Uploading...");
            if (rdoR.isChecked()) {
                uploadRecipe();
            } else if (rdoC.isChecked() || rdoSet.isChecked() || rdoSnack.isChecked()) {
                uploadCategory();
            } else if (rdoP.isChecked()) {
                uploadPost();
            }
        });

    }

    private void GetRecipeById(String id) {
        int recipeId;
        try {
            recipeId = Integer.parseInt(id);
        } catch (Exception ex) {
            AppTools.showToast(this, ex.getMessage());
            return;
        }

        Call<List<Recipe>> callRecipe = apiInterface.getRecipeById(recipeId);
        callRecipe.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                if (response.body() != null && response.body().size() > 0) {
                    Recipe recipe = response.body().get(0);
                    edtName.setText(recipe.getRecipeName());
                    edtDesc.setText(recipe.getRecipeDescription());
                    edtImage.setText(recipe.getRecipeImage());
                    Glide.with(AdminActivity.this).load(recipe.getRecipeImage())
                            .placeholder(R.drawable.simple_img1)
                            .into(previewImg);
                    setSpinnerSelection(recipe);
                } else {
                    AppTools.showToast(AdminActivity.this, "Recipe not found!\nID:" + id);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                AppTools.showToast(AdminActivity.this, t.getMessage());
            }
        });

    }

    private void setSpinnerSelection(Recipe recipe) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getCategoryId().equals(recipe.getCategoryId())) {
                spnCategory.setSelection(i + 1);
                break;
            }
        }

        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getCategoryId().equals(recipe.getPostId())) {
                spnPost.setSelection(i + 1);
                break;
            }
        }
    }

    private void GetCategoryList() {
        Call<List<Category>> callCategory = apiInterface.getCategory();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null) {
                    categoryList = response.body();
                    categoryList.add(new Category(
                            "1000",
                            "### ဟင်းချက်သုတ ###",
                            "", "0"));
                    BindCategory(categoryList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "ERROR : Fail to load category!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });
    }

    private void GetPostList() {
        Call<List<Category>> callCategory = apiInterface.getNewPost();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null) {
                    postList = response.body();
                    BindPost(postList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "ERROR : Fail to load Post!", Toast.LENGTH_SHORT).show();
                System.out.println("Fail: " + t.getMessage());

            }
        });
    }

    private void uploadRecipe() {
        if (edtName.getText().toString().equals("") || edtImage.getText().toString().equals("")
                || edtDesc.getText().toString().equals("")) {
            AppTools.hideProgressDialog();
            Toast.makeText(AdminActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe upload_data = new Recipe(
                edtId.getText().toString().trim(),
                categoryId,
                edtName.getText().toString(),
                edtDesc.getText().toString(),
                edtImage.getText().toString().trim(),
                "", "", postId);

        Log.i("upload_data", new Gson().toJson(upload_data));
        Call<JsonObject> callApi = apiInterface.uploadRecipe(upload_data);
        callApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                AppTools.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        status = jsonObject.getInt("status");
                        result = jsonObject.getString("data");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AdminActivity.this, result, Toast.LENGTH_SHORT).show();
                    if (status == 200) {
                        clearInput();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppTools.hideProgressDialog();
                Toast.makeText(AdminActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void uploadCategory() {
        if (edtName.getText().toString().equals("") || edtImage.getText().toString().equals("")) {
            AppTools.hideProgressDialog();
            Toast.makeText(AdminActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rdoSnack.isChecked() ? "1" : rdoSet.isChecked() ? "2" : "0";

        Category upload_data = new Category(
                "1",
                edtName.getText().toString(),
                edtImage.getText().toString().trim(), type);

        Call<JsonObject> callApi = apiInterface.uploadCategory(upload_data);
        callApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                AppTools.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        status = jsonObject.getInt("status");
                        result = jsonObject.getString("data");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 200) {
                        Toast.makeText(AdminActivity.this, "success", Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtImage.setText("");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category>>() {
                        }.getType();
                        categoryList = gson.fromJson(result, listType);
                        BindCategory(categoryList);
                    } else {
                        Toast.makeText(AdminActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppTools.hideProgressDialog();
                Toast.makeText(AdminActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void uploadPost() {
        if (edtName.getText().toString().equals("") || edtImage.getText().toString().equals("")) {
            AppTools.hideProgressDialog();
            Toast.makeText(AdminActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category upload_data = new Category(
                "1",
                edtName.getText().toString(),
                edtImage.getText().toString().trim(), "0");

        Log.i("upload_data", new Gson().toJson(upload_data));
        Call<JsonObject> callApi = apiInterface.uploadPost(upload_data);
        callApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                AppTools.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        status = jsonObject.getInt("status");
                        result = jsonObject.getString("data");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 200) {
                        Toast.makeText(AdminActivity.this, "success", Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtImage.setText("");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category>>() {
                        }.getType();
                        postList = gson.fromJson(result, listType);
                        BindPost(postList);
                    } else {
                        Toast.makeText(AdminActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppTools.hideProgressDialog();
                Toast.makeText(AdminActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void uploadImgBB(File file) throws IOException {
        if (file == null) {
            AppTools.hideProgressDialog();
            Toast.makeText(AdminActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<JsonObject> callApi = APIClient.getUploadService().create(APIInterface.class).uploadImage(Expire, API_KEY, body);
        callApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                AppTools.hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        result = jsonObject.getString("data");
                        status = jsonObject.getInt("status");

                        JSONObject jsonData = new JSONObject(result);
                        edtImage.setText(jsonData.getString("url"));

                        delete_url = jsonData.getString("delete_url");
                        Log.i("imgLink", jsonData.getString("url"));
                        Log.i("display_url", jsonData.getString("display_url"));
                        Log.i("delete_url", delete_url);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 200) {
                        Toast.makeText(AdminActivity.this, "Photo uploaded!", Toast.LENGTH_SHORT).show();
                        btnUpload.setEnabled(true);
                        btnDeleteImg.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(AdminActivity.this, "Photo upload fail!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppTools.hideProgressDialog();
                Toast.makeText(AdminActivity.this, "ERROR : Fail to response data!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void clearInput() {
        edtId.setText("");
        edtName.setText("");
        edtImage.setText("");
        edtDesc.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY_CODE) {
//            imageUri = data.getData();
//            imgCategory.setImageURI(imageUri);

            assert data != null;
            Uri uri = data.getData();
            String imagePath = getRealPathFromURIPath(uri, AdminActivity.this);
            Toast.makeText(AdminActivity.this, imagePath, Toast.LENGTH_LONG).show();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.i("bitmap", bitmap.getWidth() + "," + bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            Log.i("bitmap", bitmap.getWidth() + "," + bitmap.getHeight());
            uploadFile = new File(imagePath);

            previewImg.setImageBitmap(bitmap);
            btnUploadImg.setEnabled(true);
        }
    }

    //region image process
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }

    }
    //endregion image process

    private void BindCategory(List<Category> categoryList) {
        List<SpnItem> spnItemList = new ArrayList<>();
        spnItemList.add(new SpnItem("0", "Category", ""));
        for (Category c : categoryList) {
            spnItemList.add(new SpnItem(c.getCategoryId(), c.getCategoryName(), c.getCategoryImage()));
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spnItemList);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategory.setAdapter(adapter);
    }

    private void BindPost(List<Category> postList) {
        List<SpnItem> spnItemList = new ArrayList<>();
        spnItemList.add(new SpnItem("0", "Post", ""));
        for (Category c : postList) {
            spnItemList.add(new SpnItem(c.getCategoryId(), c.getCategoryName(), c.getCategoryImage()));
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spnItemList);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnPost.setAdapter(adapter);
    }

    public class SpnItem {
        private String id;
        private String name;
        private String value;

        public SpnItem(String id, String name, String value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}