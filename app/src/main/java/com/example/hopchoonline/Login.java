package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Login extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginBtn;
    DatabaseHelper databaseHelper = new DatabaseHelper(Login.this);
    User userLogged = null;
    TextView btnToSignup;

    public static SharedPreferences loggedUsernamePref;

    ImageView logoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = (EditText) findViewById(R.id.usernameLoginEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordLoginEditText);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                boolean isValidForm = !username.equals("") && !password.equals("");
                if (isValidForm) {
                    databaseHelper.getListUserByUsername(username, new MyCallBack<ArrayList<User>>() {
                        @Override
                        public void onCallback(ArrayList<User> listUser) {
                            if (listUser.size() > 0) {
                                userLogged = listUser.get(0);
                            } else {
                                Toast.makeText(Login.this, "Username hoặc password không đúng", Toast.LENGTH_SHORT).show();
                            }

                            if (userLogged != null && username.equals(userLogged.getUsername()) && password.equals(userLogged.getPassword())) {
                                Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                switchToHomePageActivity();
//                        passInfoToMyProfileActivity();
                                loggedUsernamePref = getSharedPreferences("usernameLogged", MODE_PRIVATE);
                                SharedPreferences.Editor editor = loggedUsernamePref.edit();
                                editor.putString("usernameLogged", userLogged.getUsername());
                                editor.putString("idUserLogged", userLogged.getId() + "");

                                editor.apply();
                            } else {
                                Toast.makeText(Login.this, "Username hoặc password không đúng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Bạn hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnToSignup = findViewById(R.id.btnToSignup);
        btnToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callSignupIntent = new Intent(Login.this, SignUp.class);
                startActivity(callSignupIntent);
            }
        });


    }

    private void switchToHomePageActivity() {
        Intent switchActivityIntent = new Intent(this, HomePageActivity.class);
//       Truyền dữ liệu user đã đăng nhập thành công vào đây

//        switchActivityIntent.putExtra("username",userLogged.getUsername());
        startActivity(switchActivityIntent);
        finish();
    }

    private void passInfoToMyProfileActivity() {
        Intent intent = new Intent(this, MyProfileActivity.class);
//        intent.putExtra("username",userLogged.getUsername());
        startActivity(intent);
        finish();
    }
}