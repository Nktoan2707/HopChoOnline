package com.example.hopchoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.media.Rating;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import kotlinx.coroutines.flow.Flow;

public class OtherProfileActivity extends AppCompatActivity {

    ListView ratingOptionListView;
    ListView sellOrBuyListView;
    TextView usernameTextView;

    TextView fullNameTextView;
    TextView addressTextView;
    ImageButton imgBtnBackOtherProfile;
    ImageView avtProfile;
    Button followBtn;
    Button ratingBtn;

    String idUserLogged;

    ArrayList<Option> ratingOptions = new ArrayList<>();

    ArrayList<Post> listBuyOrSell = new ArrayList<>();
    DatabaseHelper databaseHelper=new DatabaseHelper(OtherProfileActivity.this);
    String id;
    RatingBar ratingBarProfile;
    TextView txtViewBuyOrSell;
    BottomNavigationView bottomNavigationView;
    String usernameLogged = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        ratingOptions.add(new Option("Đánh giá",R.drawable.rating));
        ratingOptions.add(new Option("Xem đánh giá",R.drawable.review));
        followBtn = (Button) findViewById(R.id.followBtn);
        id = getIntent().getStringExtra("FromPostDetailToOtherProfile");
        if(Objects.nonNull(id)){
            showUserInfo(id);
        }
        usernameLogged = Login.loggedUsernamePref.getString("usernameLogged", "nolog");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigateView);
        bottomNavigationView
                .setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addPostItemMenu:
                                // Xử lý sự kiện click trên mục menu 1 ở đây
                                Intent moveToCreatePost = new Intent(OtherProfileActivity.this, CreatePostActivity.class);
                                moveToCreatePost.putExtra("MyProfileToCreatePost", usernameLogged);
                                startActivity(moveToCreatePost);
                                return;
                            case R.id.homeItemMenu:
                                Intent moveToHomePage = new Intent(OtherProfileActivity.this, HomePageActivity.class);
                                startActivity(moveToHomePage);
                                break;
                            case R.id.profileItemMenu:
                                 Intent moveToProfile = new Intent(OtherProfileActivity.this,
                                 MyProfileActivity.class);
                                 startActivity(moveToProfile);
                                return;
                            case R.id.mapItemMenu:
                                Intent moveToFindPostByDistance = new Intent(OtherProfileActivity.this,
                                        FindPostsByDistanceActivity.class);
                                startActivity(moveToFindPostByDistance);
                                break;
                            default:
                                return;
                        }
                    }
                });

        ArrayList<Post> postsOfOtherUser = getPostOfUser();
//        for (Post post: postsOfOtherUser) {
//            Bitmap bitmapImg = DbBitmapUtility.getImage(post.getImageArray());
//            listBuyOrSell.add(new Post(post.getTitle(),post.getPrice(),bitmapImg));
//        }


    }

    private void showUserInfo(String id){
        databaseHelper.getUserById(id, new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                if (user == null) {
                    user = new User();
                }
                if (Objects.nonNull(user)) {
                    usernameTextView = (TextView) findViewById(R.id.usernameTextView);
                    fullNameTextView = (TextView) findViewById(R.id.fullNameTextView);
                    addressTextView = (TextView) findViewById(R.id.addressTextView);
                    imgBtnBackOtherProfile = (ImageButton) findViewById(R.id.imgBtnBackOtherProfile);
                    ratingBarProfile = (RatingBar) findViewById(R.id.ratingBarProfile);
                    txtViewBuyOrSell = (TextView) findViewById(R.id.txtViewBuyOrSell);

                    ratingBtn = (Button) findViewById(R.id.ratingBtn);
                    avtProfile = (ImageView) findViewById(R.id.avatarImg);
                    fullNameTextView.setText(user.getFullName());
                    usernameTextView.setText(user.getUsername());
                    addressTextView.setText(user.getAddress());
                    ratingBarProfile.setRating((float) user.getRating());
                    Glide.with(OtherProfileActivity.this).load(user.getAvatarUrl())
                            .placeholder(R.drawable.avatar_default)
                            .into(avtProfile);
                    Follow followObj = new Follow(idUserLogged, id);

                    ratingBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent moveRatingPage = new Intent(OtherProfileActivity.this, RatingPage.class);
                            moveRatingPage.putExtra("FromOtherProfileToRating", id);
                            startActivity(moveRatingPage);
                        }
                    });
                    txtViewBuyOrSell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent moveToListBuyOrSell = new Intent(OtherProfileActivity.this, BuyOrSellActivity.class);
                            moveToListBuyOrSell.putExtra("IdUserFromOtherActivityToBuyOrSell", id);
                            startActivity(moveToListBuyOrSell);
                        }
                    });
                    imgBtnBackOtherProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
                    databaseHelper.isFollowed(followObj, new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean isFollowed) {
                            if (isFollowed) {
                                followBtn.setText("Đã theo dõi");
                            } else {
                                followBtn.setText("Theo dõi");
                            }
                            followBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isFollowed) {
                                        databaseHelper.unFollowUser(followObj, new MyCallBack<Boolean>() {
                                            @Override
                                            public void onCallback(Boolean success) {
                                                if (success) {
                                                    followBtn.setText("Theo dõi");
                                                    Toast.makeText(OtherProfileActivity.this, "Bỏ theo dõi người dùng thành công", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OtherProfileActivity.this, "Bỏ theo dõi người dùng  thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    } else {
                                        databaseHelper.followUser(followObj, new MyCallBack<Boolean>() {
                                            @Override
                                            public void onCallback(Boolean success) {
                                                if (success) {
                                                    followBtn.setText("Đã theo dõi");
                                                    Toast.makeText(OtherProfileActivity.this, "Theo dõi người dùng thành công", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OtherProfileActivity.this, "Theo dõi người dùng  thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

//                    FollowedDAO followedDAO = new FollowedDAO(OtherProfileActivity.this);
//                    boolean isFollowingUser = followedDAO.isFollowed(idUserLogged, id);

                }
            }
        });
    }
    private ArrayList<Post> getPostOfUser(){
//        int idOtherUser = 7; // hardcode
//         int idOtherUser = id; // Khong hard code
//        Toast.makeText(this, id+"", Toast.LENGTH_SHORT).show();
//        ArrayList<Post> listPost = databaseHelper.getAllPost();
//        ArrayList<Post> myPost = new ArrayList<>();
//        for (Post post: listPost
//             ) {
//            if(post.getAuthor()==idOtherUser){
//                myPost.add(post);
//            }
//        }
//        return myPost;
        return null;
    }


}