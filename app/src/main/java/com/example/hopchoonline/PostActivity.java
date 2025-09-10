package com.example.hopchoonline;

import static android.Manifest.permission.CALL_PHONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    public static final int NUMBER_OF_RELATED_POSTS = 6;
    DatabaseHelper databaseHelper = new DatabaseHelper(PostActivity.this);
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");
    TextView txtTitle;
    TextView txtPrice;
    ImageView btnBack;

    ImageView imgPost;
    MaterialButton btnFavorite;

    TextView txtAddress;
    ShapeableImageView imgAvatar;
    TextView txtUserFullName;
    MaterialButton btnToProfile;

    TextView ratingLabel;
    LinearLayout holderRating;

    TextView txtDescription;

    RecyclerView relatedPostRecView;
    RelatedPostCardRecyclerViewAdapter relatedPostRecViewAdapter;

    Button callBtn;
    Button btnComment;

    Button btnOpenMaps;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        imgPost = findViewById(R.id.imgPost);
        Intent callerIntent = getIntent();
        if (callerIntent != null) {
            String postIdFromHomePage = Objects.nonNull(callerIntent.getStringExtra("postId")) ? callerIntent.getStringExtra("postId") : "";
            String postIdFromOtherProfile = callerIntent.getStringExtra("postAtOtherProfile_PostDetail");
            postId = "";
            if (!Objects.equals(postIdFromHomePage, "")) {
                postId = postIdFromHomePage;
            } else {
                postId = postIdFromOtherProfile;
            }

            databaseHelper.getPostById(postId, new MyCallBack<Post>() {
                @Override
                public void onCallback(Post post) {
                    databaseHelper.getUserById(post.getAuthor(), new MyCallBack<User>() {
                        @Override
                        public void onCallback(User user) {
                            if (user == null) {
                                user = new User();
                            }

                            imgAvatar = findViewById(R.id.imgAvatar);
                            Glide.with(PostActivity.this).load(user.getAvatarUrl())
                                    .placeholder(R.drawable.avatar_default)
                                    .into(imgAvatar);

                            txtUserFullName = findViewById(R.id.txtUserFullName);
                            txtUserFullName.setText(user.getFullName());

                            btnToProfile = findViewById(R.id.btnToProfile);
                            User finalUser = user;
                            btnToProfile.setOnClickListener(v -> {
                                if (finalUser.getId().equals(idUserLogged)) {
                                    Intent callMyProfileActivityIntent = new Intent(PostActivity.this, MyProfileActivity.class);
                                    startActivity(callMyProfileActivityIntent);
                                } else {

                                    Intent moveToOtherProfile = new Intent(PostActivity.this, OtherProfileActivity.class);
                                    moveToOtherProfile.putExtra("FromPostDetailToOtherProfile", finalUser.getId());
                                    startActivity(moveToOtherProfile);
                                }
                            });

                            ratingLabel = findViewById(R.id.ratingLabel);
                            holderRating = findViewById(R.id.holderRating);
                            displayRatingStars(user.getRating());

                            callBtn = (Button) findViewById(R.id.callBtn);
                            if (user.getPhone() == null) {
                                callBtn.setVisibility(View.GONE);
                            } else {
                                callBtn.setText("GỌI: " + user.getPhone());
                                callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + finalUser.getPhone()));
                                        if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            startActivity(callIntent);
                                        } else {
                                            requestPermissions(new String[]{CALL_PHONE}, 1);
                                        }

                                        startActivity(callIntent);
                                    }
                                });
                            }
                        }
                    });


                    Glide.with(PostActivity.this).load(post.getImageUrl())
                            .placeholder(R.color.white)
                            .into(imgPost);


                    txtTitle = findViewById(R.id.txtTitle);
                    txtTitle.setText(post.getTitle());


                    Locale locale = new Locale("vi", "VN");
                    NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
                    // Format the amount as VND currency
                    String formattedAmount = nf.format(post.getPrice());
                    // Remove the currency symbol and replace the decimal separator with a comma
                    formattedAmount = formattedAmount.substring(0, formattedAmount.length() - 2)  // Remove the currency symbol
                            .concat(" đ");  // Add the VND symbol
                    txtPrice = findViewById(R.id.txtPrice);
                    txtPrice.setText(formattedAmount);

                    btnFavorite = findViewById(R.id.btnFavorite);
                    changeIconSavedPost(btnFavorite, postId);
                    btnFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SavedPostDAO savedPostDAO = new SavedPostDAO(PostActivity.this);
                            savedPostDAO.isSavedPost(idUserLogged, postId, new MyCallBack<Boolean>() {
                                @Override
                                public void onCallback(Boolean isCorrectSavedPost) {
                                    if (isCorrectSavedPost) {
                                        removeSavedPost(btnFavorite, postId);
                                    } else {
                                        addSavedPost(btnFavorite, postId);
                                    }
                                }
                            });
                        }
                    });


                    txtAddress = findViewById(R.id.txtAddress);
                    txtAddress.setText(post.getLocation());


                    txtDescription = findViewById(R.id.txtDescription);
                    txtDescription.setText(post.getDescription());


                    databaseHelper.getRelatedPosts(post.getId(), NUMBER_OF_RELATED_POSTS, new MyCallBack<ArrayList<Post>>() {
                        @Override
                        public void onCallback(ArrayList<Post> posts) {
                            if (posts == null) {
                                Log.e("Related posts", "related posts value is null!");
                                posts = new ArrayList<Post>();
                            }

                            relatedPostRecView = findViewById(R.id.relatedPostRecView);
                            relatedPostRecViewAdapter = new RelatedPostCardRecyclerViewAdapter(PostActivity.this, posts);
                            relatedPostRecView.setAdapter(relatedPostRecViewAdapter);
                            relatedPostRecView.setLayoutManager(new LinearLayoutManager(PostActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        }
                    });


                    btnComment = findViewById(R.id.btnComment);
                    btnComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callPostCommentsActivityIntent = new Intent(PostActivity.this, PostCommentsActivity.class);
                            callPostCommentsActivityIntent.putExtra("postId", post.getId());
                            startActivity(callPostCommentsActivityIntent);
                        }
                    });
                }
            });
        }


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnOpenMaps = (Button) findViewById(R.id.btn_open_gg_maps);
        btnOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Address phụ thuộc vào bài post
                // Lấy idPost từ bài post
                Intent intent = getIntent();
                String idPostViewing = intent.getStringExtra("postId");

                PostDAO postDAO = new PostDAO(getApplicationContext());
                postDAO.getPost(idPostViewing, new MyCallBack<Post>() {
                    @Override
                    public void onCallback(Post post) {
                        String address = post.getLocation();
                        openGoogleMaps(address);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeIconSavedPost(btnFavorite, postId);
        if (relatedPostRecViewAdapter != null) {
            relatedPostRecViewAdapter.notifyDataSetChanged();
        }
    }

    private void displayRatingStars(double rating) {
        holderRating.removeAllViews();
        holderRating.addView(ratingLabel);

        int count = 5;
        while (rating >= 1) {
            ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate, holderRating, false);
            holderRating.addView(imageView);
            rating--;
            count--;
        }

        if (rating >= 0.5) {
            ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate_half, holderRating, false);
            holderRating.addView(imageView);
            rating--;
            count--;
        }

        while (count >= 1) {
            ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate_empty, holderRating, false);
            holderRating.addView(imageView);
            count--;
        }
    }

    public void changeIconSavedPost(MaterialButton iconSavedPost, String postId) {
        if (iconSavedPost == null){
            return;
        }

        // Kiểm tra post có được người dùng lưu chưa
        SavedPostDAO savedPostDAO = new SavedPostDAO(PostActivity.this);
        savedPostDAO.isSavedPost(idUserLogged, postId, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isCorrectSavedPost) {
                if (isCorrectSavedPost) {
                    iconSavedPost.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite, 0);
                } else {
                    iconSavedPost.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_not_favorite, 0);
                }
            }
        });
    }

    public void addSavedPost(MaterialButton iconSavePost, String idPost) {

        SavedPostDAO savedPostDAO = new SavedPostDAO(PostActivity.this);
        savedPostDAO.savePost(idUserLogged, idPost, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isSuccess) {
                if (isSuccess) {
                    changeIconSavedPost(iconSavePost, idPost);
                } else {
                    Toast.makeText(PostActivity.this, "Lưu tin thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeSavedPost(MaterialButton iconSavePost, String idPost) {

        SavedPostDAO savedPostDAO = new SavedPostDAO(PostActivity.this);
        savedPostDAO.removeSavedPost(idUserLogged, idPost, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isSuccess) {
                if (isSuccess) {
                    changeIconSavedPost(iconSavePost, idPost);
                } else {
                    Toast.makeText(PostActivity.this, "Bỏ lưu tin thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openGoogleMaps(String address) {
        // Tạo Uri với địa chỉ để mở Google Maps
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        // Tạo Intent với hành động và Uri
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Đặt gói ứng dụng Google Maps cho Intent
        mapIntent.setPackage("com.google.android.apps.maps");

        // Kiểm tra xem ứng dụng Google Maps có sẵn trên thiết bị không
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            // Mở ứng dụng Google Maps
            startActivity(mapIntent);
        } else {
            // Hiển thị thông báo nếu ứng dụng Google Maps không có sẵn
            Toast.makeText(this, "Google Maps không có sẵn trên thiết bị của bạn.", Toast.LENGTH_SHORT).show();
        }
    }
}