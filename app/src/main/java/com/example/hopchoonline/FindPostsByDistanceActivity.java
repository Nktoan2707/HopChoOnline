package com.example.hopchoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.CursorWindow;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.location.Location;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class FindPostsByDistanceActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(FindPostsByDistanceActivity.this);
    SupportMapFragment mapPost;
    GoogleMap mMap;
    EditText edtTextFindPostByDistance;
    RadioGroup radioGroupChoosePostType;
    RadioButton checkAllPost;
    RadioButton checkSellPost;
    RadioButton checkBuyPost;
    Button findPostBtn;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_find_posts_by_distance);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 5MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
//        userLogged = Login.loggedUsernamePref.getString("usernameLogged","nolog");
//        idUserLogged = Integer.parseInt(Login.loggedUsernamePref.getString("idUserLogged",""));
        edtTextFindPostByDistance = (EditText) findViewById(R.id.edtTextFindPostByDistance);
        radioGroupChoosePostType = (RadioGroup) findViewById(R.id.radioGroupChoosePostType);
        checkAllPost = (RadioButton) findViewById(R.id.checkAllPost);
        checkSellPost = (RadioButton) findViewById(R.id.checkSellPost);
        checkBuyPost = (RadioButton) findViewById(R.id.checkBuyPost);
        findPostBtn = (Button) findViewById(R.id.findPostBtn);
        radioGroupChoosePostType.check(R.id.checkAllPost);
        mapPost = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPost);
        databaseHelper.getUserById(Login.loggedUsernamePref.getString("idUserLogged", "nolog"), new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                User loggedInUser = user;
                mapPost.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        Log.e("MAp", "");
                        Address myLocation = getLocation(loggedInUser.getAddress());
                        findPostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mMap.clear();
                                int distanceToFind = !edtTextFindPostByDistance.getText().toString().equals("") ? Integer.parseInt(edtTextFindPostByDistance.getText().toString()) : 0;
                                String typePost = "";
                                if (checkAllPost.isChecked()) {
                                    typePost = "all";
                                } else if (checkBuyPost.isChecked()) {
                                    typePost = "buy";
                                } else {
                                    typePost = "sell";
                                }
                                if (distanceToFind == 0) {
                                    Toast.makeText(FindPostsByDistanceActivity.this, "Hãy nhập một số thích hợp", Toast.LENGTH_SHORT).show();
                                }
                                ArrayList<Post> listPost = new ArrayList<>();
                                if (typePost.equals("all")) {
                                    databaseHelper.getAllPost(new MyCallBack<ArrayList<Post>>() {
                                        @Override
                                        public void onCallback(ArrayList<Post> listPost) {
                                            for (Post post : listPost) {
                                                Address postAddress = getLocation(post.getLocation());
                                                if (postAddress == null){
                                                    continue;
                                                }

                                                if (distance(postAddress.getLatitude(), postAddress.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()) < distanceToFind) {
                                                    LatLng latLng = new LatLng(postAddress.getLatitude(), postAddress.getLongitude());
                                                    Log.e("Location", latLng + "");
                                                    if (mMap != null) {
                                                        Marker maker = mMap.addMarker(new MarkerOptions().position(latLng).title(post.getTitle()));
                                                        maker.showInfoWindow();
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                                    } else {
                                                        Log.d("TAG", "Map is null");
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else if (typePost.equals("sell")) {
                                    databaseHelper.getPostByType(true, new MyCallBack<ArrayList<Post>>() {
                                        @Override
                                        public void onCallback(ArrayList<Post> listPost) {
                                            for (Post post : listPost) {
                                                Address postAddress = getLocation(post.getLocation());
                                                if (postAddress == null){
                                                    continue;
                                                }

                                                if (distance(postAddress.getLatitude(), postAddress.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()) < distanceToFind) {
                                                    LatLng latLng = new LatLng(postAddress.getLatitude(), postAddress.getLongitude());
                                                    Log.e("Location", latLng + "");
                                                    if (mMap != null) {
                                                        Marker maker = mMap.addMarker(new MarkerOptions().position(latLng).title(post.getTitle()));
                                                        maker.showInfoWindow();
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                                    } else {
                                                        Log.d("TAG", "Map is null");
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    databaseHelper.getPostByType(false, new MyCallBack<ArrayList<Post>>() {
                                        @Override
                                        public void onCallback(ArrayList<Post> listPost) {
                                            for (Post post : listPost) {
                                                Address postAddress = getLocation(post.getLocation());
                                                if (postAddress == null){
                                                    continue;
                                                }

                                                if (distance(postAddress.getLatitude(), postAddress.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude()) < distanceToFind) {
                                                    LatLng latLng = new LatLng(postAddress.getLatitude(), postAddress.getLongitude());
                                                    Log.e("Location", latLng + "");
                                                    if (mMap != null) {
                                                        Marker maker = mMap.addMarker(new MarkerOptions().position(latLng).title(post.getTitle()));
                                                        maker.showInfoWindow();
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                                    } else {
                                                        Log.d("TAG", "Map is null");
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public Address getLocation(String address) {
        // hardcode
        // Khi nguoi dung dang nhap, lay username, id, dia chi cua ho. Tu do co duoc dia chi--> truyen vao address
        // address = "KTX 135B Trần Hưng Đạo, Quận 1, TPHCM";
        Geocoder geocoder = new Geocoder(FindPostsByDistanceActivity.this);
        Address location = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                location = addresses.get(0);
                return location;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    public static double distance(double lat1, double long1, double lat2, double long2) {
        double R = 6371; // bán kính của trái đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(long2 - long1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
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