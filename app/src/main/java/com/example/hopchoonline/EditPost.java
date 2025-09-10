package com.example.hopchoonline;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class EditPost extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 10;
    private ImageView imagePost;
    private ImageView iconUpdate;
    private EditText edtTitle;
    private EditText edtDescription;
    private EditText edtPrice;
    private EditText edtAddress;
    private ImageView imgProduct;
    private Uri selectedImageUri;
    private String imagePathFile = "";
    Bitmap imageProductBitmap;
    private MaterialButton btnSave;
    private TextView txtViewErrTitle;
    private TextView txtViewErrDescription;
    private TextView txtViewErrPrice;
    private TextView txtViewErrAddress;
    private RadioGroup radioGroup;
    RadioButton radioBtnSell;
    RadioButton radioBtnBuy;
    private boolean isValid = true;
    private boolean isBuy;
    private String idUserLogged;
    private String idMyPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy id của user
        idUserLogged = getIntent().getStringExtra("idUserLogged");
        idMyPost = getIntent().getStringExtra("idMyPost");

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        imgProduct = (ImageView) findViewById(R.id.my_image_view);
        iconUpdate = (ImageView) findViewById(R.id.iconUpdateImage);
        btnSave = (MaterialButton) findViewById(R.id.btn_save);
        txtViewErrTitle = (TextView)findViewById(R.id.txtViewErrorTitle);
        txtViewErrDescription = (TextView)findViewById(R.id.txtViewErrorDescription);
        txtViewErrPrice = (TextView)findViewById(R.id.txtViewErrorPrice);
        txtViewErrAddress = (TextView)findViewById(R.id.txtViewErrorAddress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioBtnSell = (RadioButton) findViewById(R.id.radioBtnSell);
        radioBtnBuy = (RadioButton) findViewById(R.id.radioBtnBuy);

        // Ẩn thông báo lỗi khi vừa mở trang
        txtViewErrTitle.setVisibility(View.GONE);
        txtViewErrDescription.setVisibility(View.GONE);
        txtViewErrPrice.setVisibility(View.GONE);
        txtViewErrAddress.setVisibility(View.GONE);

        PostDAO postDAO = new PostDAO(getApplicationContext());
        postDAO.getPost(idMyPost, new MyCallBack<Post>() {
            @Override
            public void onCallback(Post post) {
                edtTitle.setText(post.getTitle());
                edtAddress.setText(post.getLocation());
                edtPrice.setText(post.getPrice() + "");
                edtDescription.setText(post.getDescription());
                Glide.with(EditPost.this).load(post.getImageUrl())
                        .placeholder(R.color.white)
                        .into(imgProduct);
                isBuy = post.isBuy();
                if(isBuy) {
                    radioBtnBuy.setChecked(true);
                    radioBtnSell.setChecked(false);
                } else {
                    radioBtnSell.setChecked(true);
                    radioBtnBuy.setChecked(false);
                }
            }
        });

        iconUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                byte[] imageIntoDB = null;
                // Lấy thông tin bài post
                String title = edtTitle.getText().toString();
                String location = edtAddress.getText().toString();
                String description = edtDescription.getText().toString();
                isBuy = radioBtnBuy.isChecked();
                // Tạo đối tượng Date với thời gian hiện tại
                Date now = new Date();
                // Tạo đối tượng Timestamp từ đối tượng Date
                Timestamp date = new Timestamp(now.getTime() / 1000, (int) ((now.getTime() % 1000) * 1000000));
                int price = Integer.parseInt(edtPrice.getText().toString());

                if(edtPrice.getText().toString().length() < 1) {
                    txtViewErrPrice.setVisibility(View.VISIBLE);
                    isValid = false;
                }

                // Ẩn thông báo lỗi khi submit
                txtViewErrTitle.setVisibility(View.GONE);
                txtViewErrDescription.setVisibility(View.GONE);
                txtViewErrPrice.setVisibility(View.GONE);

                // Kiểm tra dữ liệu có rỗng không
                if(title.length() < 1) {
                    txtViewErrTitle.setVisibility(View.VISIBLE);
                    isValid = false;
                }
                if(description.length() < 1) {
                    txtViewErrDescription.setVisibility(View.VISIBLE);
                    isValid = false;
                }
                if(price < 0) {
                    txtViewErrPrice.setVisibility(View.VISIBLE);
                    isValid = false;
                }
                if(!CommonMethod.isValidAddress(location, getApplicationContext())) {
                    txtViewErrAddress.setVisibility(View.VISIBLE);
                    isValid = false;
                }

                // Kiểm tra đủ và đúng hết thông tin
                if(isValid) {
                    // Trường hợp chọn ảnh
                    if (selectedImageUri != null) {
                        try {
                            // Đọc hình ảnh từ URI
                            Bitmap imageProductBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageIntoDB = DbBitmapUtility.getBytes(imageProductBitmap);

                            // cập nhật bài post
                            PostDAO updatePostDAO = new PostDAO(getApplicationContext());
                            updatePostDAO.updatePost(idMyPost, title, price, description, isBuy, imageIntoDB, imagePathFile, location, new MyCallBack<Boolean>() {
                                @Override
                                public void onCallback(Boolean isSuccess) {
                                    if(isSuccess) {
                                        Toast.makeText(EditPost.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        Intent moveToPostedPosts = new Intent(EditPost.this, PostedPostsActivity.class);
                                        moveToPostedPosts.putExtra("idUserLogged", idUserLogged);
                                        startActivity(moveToPostedPosts);
                                        finish();
                                    } else {
                                        Toast.makeText(EditPost.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // Trường hợp không chọn ảnh
                    else {
                        postDAO.getPost(idMyPost, new MyCallBack<Post>() {
                            @Override
                            public void onCallback(Post post) {
                                String imageUrl = post.getImageUrl();

                                PostDAO updatePostDAO = new PostDAO(getApplicationContext());
                                updatePostDAO.updatePostWithoutSelectingImage(idMyPost, title, price, description, isBuy, imageUrl, location, date,new MyCallBack<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean isSuccess) {
                                        if(isSuccess) {
                                            Toast.makeText(EditPost.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            Intent moveToPostedPosts = new Intent(EditPost.this, PostedPostsActivity.class);
                                            moveToPostedPosts.putExtra("idUserLogged", idUserLogged);
                                            startActivity(moveToPostedPosts);
                                            finish();
                                        } else {
                                            Toast.makeText(EditPost.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }

                } else {
                    Toast.makeText(EditPost.this, "Vui lòng kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Mở dialog cho phép người dùng chọn hình ảnh từ máy
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả của việc chọn hình ảnh từ máy
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the data intent
            selectedImageUri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imagePathFile = cursor.getString(columnIndex);
            cursor.close();
            // Set the image in the ImageView
            imgProduct.setImageURI(selectedImageUri);
            try {
                imageProductBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
