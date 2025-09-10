package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");
    private ImageView imageView;

    private EditText username;

    private EditText fullName;

    private EditText phone;

    private EditText address;

    private EditText password;

    private boolean isValid;

    DatabaseHelper databaseHelper = new DatabaseHelper(EditProfile.this);
    private MaterialButton btnSave;
    private MaterialButton btn_cancel;

    private ImageView imgAvatar;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_edit_profile_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, scrollView.getBottom());

        imageView = findViewById(R.id.avatar_image);
        Button chooseImageButton = findViewById(R.id.btn_choose_image);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to launch the device's image picker
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                // Start the image picker activity
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        username = (EditText) findViewById(R.id.username_input);
        fullName = (EditText) findViewById(R.id.fullname_input);
        phone = (EditText) findViewById(R.id.phone_input);
        address = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        btnSave = (MaterialButton) findViewById(R.id.btn_save);
        imgAvatar = (ImageView) findViewById(R.id.avatar_image);
        // Lấy id user để edit
//        Integer idUser = Integer.parseInt(idUserLogged);
        databaseHelper.getUserById(idUserLogged, new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                username.setText(user.getUsername());
                fullName.setText(user.getFullName());
                phone.setText(user.getPhone());
                address.setText(user.getAddress());
                password.setText(user.getPassword());


                Glide.with(EditProfile.this).load(user.getAvatarUrl())
                        .placeholder(R.drawable.avatar_default)
                        .into(imgAvatar);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isValid = true;
                        String usernameVal = username.getText().toString();
                        String fullNameVal = fullName.getText().toString();
                        String phoneVal = phone.getText().toString();
                        String addressVal = address.getText().toString();
                        String passwordVal = password.getText().toString();
                        if (usernameVal.isEmpty() || fullNameVal.isEmpty() || phoneVal.isEmpty() || addressVal.isEmpty() || passwordVal.isEmpty()) {
                            isValid = false;
                        }

                        byte[] imageIntoDB = null;
                        if (selectedImageUri != null) {
                            try {
                                // Đọc hình ảnh từ URI
                                Bitmap imageProductBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                imageIntoDB = DbBitmapUtility.getBytes(imageProductBitmap);
                                if (isValid) {
                                    User userNew = new User(idUserLogged, usernameVal, passwordVal, fullNameVal, addressVal, phoneVal, user.isPriority(), user.getRating(), "");
                                    databaseHelper.updateUser(userNew,selectedImageUri.toString(), imageIntoDB, new MyCallBack<Boolean>() {
                                                @Override
                                                public void onCallback(Boolean success) {
                                                    if(success){
                                                        Toast.makeText(EditProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                                                        Intent moveToMyProfile = new Intent(EditProfile.this, MyProfileActivity.class);
                                                        startActivity(moveToMyProfile);
                                                        finish();
                                                    } else{
                                                        Toast.makeText(EditProfile.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }

                                    );
                                }
                            } catch (IOException e){
                                e.printStackTrace();
                            }

                        }

                    }
                });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            // Get the image URI from the data intent
//            Uri imageUri = data.getData();
//
//            // Set the image in the ImageView
//            imageView.setImageURI(imageUri);
//        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvatar.setImageURI(selectedImageUri);
        }
    }
}