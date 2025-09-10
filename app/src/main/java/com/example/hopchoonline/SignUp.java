package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;

public class SignUp extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText rePassword;
    EditText fullname;
    EditText address;
    Button signUpBtn;

    String usernameStr;
    String passwordStr;
    String rePasswordStr;
    String fullnameStr;
    String addressStr;
    DatabaseHelper databaseHelper=new DatabaseHelper(SignUp.this);
    User userSignUp = null;

    TextView btnToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        rePassword = (EditText) findViewById(R.id.rePasswordEditText);
        fullname = (EditText) findViewById(R.id.nameEditText);
        address = (EditText) findViewById(R.id.addressEditText);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameStr = username.getText().toString();
                passwordStr = password.getText().toString();
                rePasswordStr = rePassword.getText().toString();
                fullnameStr = fullname.getText().toString();
                addressStr = address.getText().toString();

                boolean isTypedFullFields = !usernameStr.equals("") && !passwordStr.equals("") && !rePasswordStr.equals("") && !fullnameStr.equals("") && !addressStr.equals("");
                boolean isSameBetweenPassAndRePass = passwordStr.equals(rePasswordStr);
                if(isTypedFullFields){
                    if(isSameBetweenPassAndRePass){
//                        databaseHelper.isExistUsername(usernameStr, new MyCallBack<Boolean>() {
//
//                            @Override
//                            public void onCallback(Boolean isExistsUsername) {
//
//                                if(isExistsUsername){
//                                    Toast.makeText(SignUp.this, "Đã tồn tại username", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }else{
//
//                                }
//                            }
//
//
//
//                        });
                        User user = new User(usernameStr, passwordStr, fullnameStr,addressStr);
                        databaseHelper.addUser(user, new MyCallBack<Boolean>() {
                            @Override
                            public void onCallback(Boolean check) {
                                if(check){
                                    Toast.makeText(SignUp.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                    userSignUp = user;
                                    switchToHomePageActivity();
                                }else{
                                    Toast.makeText(SignUp.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(SignUp.this, "Mật khẩu và nhập lại mật khẩu không giống nhau", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUp.this, "Bạn hãy điền đẩy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnToLogin = findViewById(R.id.btnToLogin);
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callLoginIntent = new Intent(SignUp.this, Login.class);
                startActivity(callLoginIntent);
            }
        });
    }

    private void switchToHomePageActivity(){
        Intent switchActivityIntent = new Intent(this, Login.class);
        startActivity(switchActivityIntent);
        finish();
    }
}