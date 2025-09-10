package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;

public class CreatePostActivity extends AppCompatActivity {
    String usernameLogged;

    EditText edtTitle;
    EditText edtAddress;
    EditText edtDescription;
    EditText edtPrice;
    ImageView imgProduct;
    Button uploadBtn;
    RadioGroup radioGroup;
    RadioButton radioBtnSell;
    RadioButton radioBtnBuy;
    Button cancelBtn;
    Button saveBtn;

    Bitmap imageProductBitmap;
    DatabaseHelper databaseHelper=new DatabaseHelper(CreatePostActivity.this);
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isValid = true;

    ImageView btnBack;
    MaterialButton btn_cancel;
    Uri imageUri;
    String imagePathFile = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        usernameLogged =  Login.loggedUsernamePref.getString("usernameLogged","nolog");

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        imgProduct = (ImageView) findViewById(R.id.my_image_view);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioBtnSell = (RadioButton) findViewById(R.id.radioBtnSell);
        radioBtnBuy = (RadioButton) findViewById(R.id.radioBtnBuy);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        saveBtn = (Button) findViewById(R.id.btn_save);
        radioGroup.check(R.id.radioBtnSell);

        //Toast.makeText(this, usernameLogged, Toast.LENGTH_SHORT).show();
//        userLogged = getIntent().getStringExtra("MyProfileToCreatePost");
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                // Start the image picker activity
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUploadPost();
            }
        });



    }

    private void handleUploadPost(){
        String title = edtTitle.getText().toString();
        String address = edtAddress.getText().toString();
        String description = edtDescription.getText().toString();
        int price = Integer.parseInt(edtPrice.getText().toString());

        //Dùng SharePreference để lấy usernameLogged -> thay vào getListUserByUsername
        databaseHelper.getListUserByUsername(usernameLogged, new MyCallBack<ArrayList<User>>() {
            @Override
            public void onCallback(ArrayList<User> listUser) {
                User userLogged = listUser.get(0);
                String id = userLogged.getId();
                byte[] imageIntoDB = DbBitmapUtility.getBytes(imageProductBitmap);
                boolean isSell = true;
                if(!radioBtnSell.isChecked()){
                    isSell = false;
                }
                // Kiểm tra thông tin địa chỉ hợp lệ không
                if(CommonMethod.isValidAddress(address, CreatePostActivity.this)) {
                    databaseHelper.addPost(title, price, description, isSell, imageIntoDB, imagePathFile, id, address, new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean isSuccess) {
                            if(isSuccess){
                                Toast.makeText(CreatePostActivity.this, "Tạo bài đăng thành công", Toast.LENGTH_SHORT).show();
//                onBackPressed();
                                Intent moveToHomePage = new Intent(CreatePostActivity.this, HomePageActivity.class);
                                startActivity(moveToHomePage);
                                finish();

//           // Close create activity -> Đến trang post

                            }else{
                                Toast.makeText(CreatePostActivity.this, "Tạo bài đăng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CreatePostActivity.this, "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the data intent
            imageUri = data.getData();
            Log.e("Error","Vao day");
            Log.e("Error",imageUri+"");
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imagePathFile = cursor.getString(columnIndex);
            cursor.close();
            // Set the image in the ImageView
            imgProduct.setImageURI(imageUri);
            try {
                imageProductBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}