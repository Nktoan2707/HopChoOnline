package com.example.hopchoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {

    private int currentPage = -1;
    public static final int NUMBER_OF_POSTS_PER_PAGE = 6;

    DatabaseHelper databaseHelper = new DatabaseHelper(HomePageActivity.this);
    PostDAO postDAO;

    ImageView imgBanner;
    RecyclerView postRecView;
    AutoCompleteTextView searchAutocomplete;
    PostCardRecyclerViewAdapter postRecViewAdapter;

    String usernameLogged;
    String idUserLogged;
    Button btnLoadMorePost;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        databaseHelper.getPosts(currentPage++, NUMBER_OF_POSTS_PER_PAGE, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> appendPosts) {
                if (appendPosts == null || appendPosts.size() <= 0) {
                    btnLoadMorePost.setEnabled(false);
                    btnLoadMorePost.setText("Không còn post nào");
                } else {
                    postRecView = findViewById(R.id.postRecView);
                    postRecViewAdapter = new PostCardRecyclerViewAdapter(HomePageActivity.this, appendPosts);
                    postRecView.setAdapter(postRecViewAdapter);
                    postRecView.setLayoutManager(new GridLayoutManager(HomePageActivity.this, 2, GridLayoutManager.VERTICAL, false));
                }
            }
        });


        btnLoadMorePost = findViewById(R.id.btnLoadMorePost);
        btnLoadMorePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.getPosts(currentPage++, NUMBER_OF_POSTS_PER_PAGE, new MyCallBack<ArrayList<Post>>() {
                    @Override
                    public void onCallback(ArrayList<Post> appendPosts) {
                        if (appendPosts == null || appendPosts.size() == 0) {
                            btnLoadMorePost.setEnabled(false);
                            btnLoadMorePost.setText("Không còn post nào");
                            return;
                        }
                        postRecViewAdapter.appendPosts(appendPosts);
                    }
                });
            }
        });

        databaseHelper.getPosts(currentPage++, NUMBER_OF_POSTS_PER_PAGE, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> appendPosts) {
                if (appendPosts == null || appendPosts.size() <= 0) {
                    btnLoadMorePost.setEnabled(false);
                    btnLoadMorePost.setText("Không còn post nào");
                } else {
                    postRecView = findViewById(R.id.postRecView);
                    postRecViewAdapter = new PostCardRecyclerViewAdapter(HomePageActivity.this, appendPosts);
                    postRecView.setAdapter(postRecViewAdapter);
                    postRecView.setLayoutManager(new GridLayoutManager(HomePageActivity.this, 2, GridLayoutManager.VERTICAL, false));
                }
            }
        });

        // Lấy username của user
        usernameLogged = Login.loggedUsernamePref.getString("usernameLogged", "nolog");
        // Lấy id của user
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

        // Gợi ý khi người dùng nhập vào ô input để search
        searchAutocomplete = (AutoCompleteTextView) findViewById(R.id.search_autocomplete);
        // Các từ khóa để gợi ý
        postDAO = new PostDAO(this);
        postDAO.getAllTitlesProduct(new MyCallBack<ArrayList<String>>() {
            @Override
            public void onCallback(ArrayList<String> keywords) {
                if(keywords.size() != 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomePageActivity.this, R.layout.custom_dropdown_item, keywords);

                    // Tạo PopupWindow để hiển thị danh sách từ khóa
                    View popupView = LayoutInflater.from(HomePageActivity.this).inflate(R.layout.search_suggestions_list, null);
                    ListView listView = popupView.findViewById(android.R.id.list);
                    listView.setAdapter(adapter);

                    PopupWindow popupWindow = new PopupWindow(popupView,
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    // Thiết lập chiều rộng cho PopupWindow bằng chiều rộng của màn hình
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    popupWindow.setWidth(width);

                    // Hiển thị PopupWindow khi người dùng nhập vào AutoCompleteTextView
                    searchAutocomplete.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // Không cần thực hiện hành động trước khi văn bản thay đổi
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Cập nhật danh sách từ khóa mỗi khi văn bản thay đổi
                            if (s.length() > 0) {
                                adapter.getFilter().filter(s);
                            }


                            // TO DO: fix bug chỗ này thường xuyên bị token null => crash app
                            if (!adapter.isEmpty()) {
                                popupWindow.showAsDropDown(searchAutocomplete);
                            }

                            // Nếu độ dài là rỗng thì ẩn pop up đi
                            if (s.length() < 1) {
                                popupWindow.dismiss();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Không cần thực hiện hành động sau khi văn bản thay đổi
                        }
                    });

                    // Đóng PopupWindow khi người dùng chọn một mục trong danh sách
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String keyword = adapter.getItem(position);
                            searchAutocomplete.setText(keyword);
                            searchAutocomplete.setSelection(keyword.length());
                            popupWindow.dismiss();
                        }
                    });
                }
            }
        });

        // Gợi ý khi search
        searchAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchAutocomplete.getText().toString();
                    searchAutocomplete.setText("");

                    Intent intent = new Intent(HomePageActivity.this, SearchResultPosts.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putString("query", query);
                    myBundle.putString("idUserLogged", idUserLogged);
                    myBundle.putString("usernameLogged", usernameLogged);

                    intent.putExtras(myBundle);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addPostItemMenu:
                        Intent moveToCreatePost = new Intent(HomePageActivity.this, CreatePostActivity.class);
                        moveToCreatePost.putExtra("MyProfileToCreatePost", usernameLogged);
                        startActivity(moveToCreatePost);
                        return;
                    case R.id.profileItemMenu:
                        Intent moveToProfile = new Intent(HomePageActivity.this, MyProfileActivity.class);
                        startActivity(moveToProfile);
                        break;

                    case R.id.mapItemMenu:
                        Intent moveToFindPostByDistance = new Intent(HomePageActivity.this, FindPostsByDistanceActivity.class);
                        startActivity(moveToFindPostByDistance);
                        break;
                    default:
                        return;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postRecViewAdapter != null) {
            postRecViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}