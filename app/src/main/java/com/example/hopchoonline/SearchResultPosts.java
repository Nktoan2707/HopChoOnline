package com.example.hopchoonline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchResultPosts extends AppCompatActivity implements OnSortingCompleteListener{
    AutoCompleteTextView searchAutocomplete;
    TextView txtViewNoResult;
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;
    View overlay;
    String idUserLogged;
    String usernameLogged;
    String query;
    private boolean isDataLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_posts);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        //edtSearch = (TextInputEditText)findViewById(R.id.edt_search);
        searchAutocomplete = (AutoCompleteTextView) findViewById(R.id.search_autocomplete);
        txtViewNoResult = (TextView)findViewById(R.id.txtViewNoResult);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        overlay = (View)findViewById(R.id.view_overlay);

        Intent intent = getIntent();
        idUserLogged = intent.getStringExtra("idUserLogged");
        usernameLogged = intent.getStringExtra("usernameLogged");
        query = intent.getStringExtra("query");

        searchPosts(query);

        bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addPostItemMenu:
                        // Xử lý sự kiện click trên mục menu 1 ở đây
                        Intent moveToCreatePost = new Intent(SearchResultPosts.this, CreatePostActivity.class);
                        moveToCreatePost.putExtra("MyProfileToCreatePost", usernameLogged);
                        startActivity(moveToCreatePost);
                        return;
                    case R.id.homeItemMenu:
                        Intent moveToHomePage = new Intent(SearchResultPosts.this, HomePageActivity.class);
                        startActivity(moveToHomePage);
                        break;
                    case R.id.profileItemMenu:
                         Intent moveToProfile = new Intent(SearchResultPosts.this,
                         MyProfileActivity.class);
                         startActivity(moveToProfile);
                        return;
                    case R.id.mapItemMenu:
                        Intent moveToFindPostByDistance = new Intent(SearchResultPosts.this,
                                FindPostsByDistanceActivity.class);
                        startActivity(moveToFindPostByDistance);
                        break;
                    default:
                        return;
                }
            }
        });
    }

    public interface OnSortingCompleteListener {
        void onSortingStart();
        void onSortingComplete();
    }

    public void searchPosts(String query) {
        PostDAO postDAO = new PostDAO(this);
        postDAO.searchPosts(query, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> searchedResults) {

                if(searchedResults.size() > 0) {
                    txtViewNoResult.setVisibility(View.GONE);
                } else {
                    txtViewNoResult.setText("Không tìm thấy sản phẩm");
                    txtViewNoResult.setVisibility(View.VISIBLE);
                }

                // Lấy vị trí của người dùng hiện tại đã đăng kí hồ sơ trước đó
                UserDAO userDAO = new UserDAO(SearchResultPosts.this);
                userDAO.getPriority_Rating_Address(idUserLogged, new MyCallBack<User>() {
                    @Override
                    public void onCallback(User userCurrent) {
                        String addressCurrent = userCurrent.getAddress();
                        RecyclerView recyclerView = findViewById(R.id.recycler_view_search_result_posts);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultPosts.this));
                        SearchResultPostItemAdapter adapter = new SearchResultPostItemAdapter(searchedResults, idUserLogged, SearchResultPosts.this, SearchResultPosts.this);
                        adapter.sortItemsAsync(SearchResultPosts.this, searchedResults, addressCurrent);
                        recyclerView.setAdapter(adapter);

                        // Gợi ý khi người dùng nhập vào ô input để search
                        searchAutocomplete = (AutoCompleteTextView) findViewById(R.id.search_autocomplete);
                        // Các từ khóa để gợi ý
                        PostDAO postDAO = new PostDAO(SearchResultPosts.this);
                        postDAO.getAllTitlesProduct(new MyCallBack<ArrayList<String>>() {
                            @Override
                            public void onCallback(ArrayList<String> keywords) {
                                if(keywords.size() != 0) {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResultPosts.this, R.layout.custom_dropdown_item, keywords);

                                    // Tạo PopupWindow để hiển thị danh sách từ khóa
                                    View popupView = LayoutInflater.from(SearchResultPosts.this).inflate(R.layout.search_suggestions_list, null);
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

                        searchAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                if (i == EditorInfo.IME_ACTION_SEARCH) {
                                    String query = searchAutocomplete.getText().toString();
                                    searchAutocomplete.setText("");
                                    searchAutocomplete.setHint("Tìm kiếm sản phẩm...");

                                    // Hide the soft keyboard
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(searchAutocomplete.getWindowToken(), 0);

                                    searchPosts(query);

                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                });
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
    public void onSortingStart() {
        progressBar.setVisibility(View.VISIBLE); overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSortingComplete() {
        progressBar.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }
}