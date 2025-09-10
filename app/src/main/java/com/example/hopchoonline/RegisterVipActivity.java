package com.example.hopchoonline;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterVipActivity extends AppCompatActivity {
    private EditText edtBankNumber;
    private EditText edtNameOfBank;
    private EditText edtIdCard;
    private EditText edtExpiredDate;
    private CheckBox checkBoxPolicy;
    private TextView txtViewErrBankNumber;
    private TextView txtViewErrNameOfBank;
    private TextView txtViewErrIdCard;
    private TextView titleRegisterVip;
    private TextView txtViewLabelExpiredDate;
    private TextView txtViewAnnouncementExpiredDate;
    private MaterialButton btnRegisterVip;
    private MaterialButton btnCancelRegisterVip;
    private MaterialButton btnUpdateInformation;
    private MaterialButton btnExpiredDateMore;
    private String idUserLogged;
    private InfoRegisterVip info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_vip_activity);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Lấy id của user
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged","nolog");

        edtBankNumber = (EditText) findViewById(R.id.edtBankNumber);
        edtNameOfBank = (EditText) findViewById(R.id.edtNameOfBank);
        edtIdCard = (EditText) findViewById(R.id.edtIdCard);
        edtExpiredDate = (EditText) findViewById(R.id.edtExpiredDate);
        checkBoxPolicy = (CheckBox) findViewById(R.id.checkboxPolicy);
        titleRegisterVip = (TextView) findViewById(R.id.titleRegisterVip);
        txtViewErrBankNumber = (TextView) findViewById(R.id.txtViewErrorBankNumber);
        txtViewErrNameOfBank = (TextView) findViewById(R.id.txtViewErrorNameOfBank);
        txtViewErrIdCard = (TextView) findViewById(R.id.txtViewErrorIdCard);
        txtViewAnnouncementExpiredDate = (TextView) findViewById(R.id.txtViewAnnouncementExpiredDate);
        txtViewLabelExpiredDate = (TextView) findViewById(R.id.label_expiredDate);
        btnRegisterVip = (MaterialButton) findViewById(R.id.btn_register);
        btnCancelRegisterVip = (MaterialButton) findViewById(R.id.btn_cancel_register_vip);
        btnUpdateInformation = (MaterialButton) findViewById(R.id.btn_update_information);
        btnExpiredDateMore = (MaterialButton) findViewById(R.id.btn_expired_date_more);

        // Ẩn thông báo lỗi khi vừa mở trang
        txtViewErrBankNumber.setVisibility(View.GONE);
        txtViewErrNameOfBank.setVisibility(View.GONE);
        txtViewErrIdCard.setVisibility(View.GONE);
        txtViewAnnouncementExpiredDate.setVisibility(View.GONE);

        // Thay đổi giao diện nếu đã đăng kí thành viên VIP
        updateUIInformation();

        btnRegisterVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegisterVip();
            }
        });

        btnCancelRegisterVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCancelRegisterVip();
            }
        });

        btnUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUpdateInfoRegisterVip();
            }
        });

        btnExpiredDateMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleExpiredDateMore();
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

    void isValidInfo(String numberBank, String fullNameBank, String idCard, MyCallBack<Boolean> callback) {
        UserDAO userDAO = new UserDAO(getApplicationContext());
        userDAO.getUserById2(idUserLogged, new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                boolean isMatchedNameOfBank = true;
                // Kiểm tra tên đăng nhập và tên tài khoản ngân hàng phải trùng khớp với nhau
                if (!user.getFullName().equals(fullNameBank)) {
                    isMatchedNameOfBank = false;
                    txtViewErrNameOfBank.setText("(*) Tên chủ tài khoản phải trùng khớp với tên đăng nhập");
                    txtViewErrNameOfBank.setVisibility(View.VISIBLE);
                    callback.onCallback(false);
                } else {
                    boolean isCorrect = true;
                    if (numberBank.length() < 1) {
                        txtViewErrBankNumber.setVisibility(View.VISIBLE);
                        isCorrect = false;
                    }
                    if (fullNameBank.length() < 1) {
                        txtViewErrNameOfBank.setVisibility(View.VISIBLE);
                        isCorrect = false;
                    }
                    if (idCard.length() < 1) {
                        txtViewErrIdCard.setVisibility(View.VISIBLE);
                        isCorrect = false;
                    }
                    callback.onCallback(isCorrect && isMatchedNameOfBank);
                }
            }
        });
    }

    public void updateUIInformation() {
        UserDAO userDAO = new UserDAO(getApplicationContext());
        userDAO.getUserById2(idUserLogged, new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                if(user.isPriority()) {
                    titleRegisterVip.setText("THÔNG TIN THÀNH VIÊN VIP");
                    btnRegisterVip.setVisibility(View.GONE);

                    InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
                    infoRegisterVipDAO.getInfoRegisterVip(idUserLogged, new MyCallBack<InfoRegisterVip>() {
                        @Override
                        public void onCallback(InfoRegisterVip info) {
                            edtBankNumber.setText(info.getNumberBank());
                            edtNameOfBank.setText(info.getFullNameBank());
                            edtIdCard.setText(info.getIdCard());
                            String expiredDate = CommonConverter.convertDateVN(info.getExpiredDate());
                            edtExpiredDate.setText(expiredDate);
                            // Có thể test bỏ enabled = false để test chức năng này
                            // edtExpiredDate.setEnabled(false);
                            checkBoxPolicy.setText("Tôi đồng ý hủy thành viên VIP");

                            // Lấy ngày hiện tại
                            LocalDate currentDate = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedCurrentDate = currentDate.format(formatter);

                            // Kiểm tra tài khoản VIP đã hết hạn hay chưa
                            int isExpired = compareToBetweenTwoDate(info.getExpiredDate(), formattedCurrentDate);
                            if(isExpired <= 0) {
                                cancelRegisterVipAutomatically();
                            } else {
                                long distanceDays = CommonMethod.distanceBetweenDays(info.getExpiredDate());

                                // Nếu gần hết hạn (3 ngày trỏ xuống) sẽ thông báo
                                if(distanceDays < 4) {
                                    String announcement = "(*) Tài khoản VIP của bạn sắp hết hạn. Vui lòng gia hạn thêm.\n" +
                                            "    Nếu không hệ thống sẽ tự động hủy sau " + distanceDays + " ngày.";
                                    txtViewAnnouncementExpiredDate.setText(announcement);
                                    txtViewAnnouncementExpiredDate.setVisibility(View.VISIBLE);
                                    btnExpiredDateMore.setVisibility(View.VISIBLE);
                                } else {
                                    btnExpiredDateMore.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                } else {
                    txtViewLabelExpiredDate.setVisibility(View.GONE);
                    edtExpiredDate.setVisibility(View.GONE);
                    btnUpdateInformation.setVisibility(View.GONE);
                    btnExpiredDateMore.setVisibility(View.GONE);
                    btnCancelRegisterVip.setVisibility(View.GONE);
                }
            }
        });
    }

    public void handleRegisterVip() {
        String bankNumber = edtBankNumber.getText().toString();
        String nameOfBank = edtNameOfBank.getText().toString();
        String idCard = edtIdCard.getText().toString();

        if(checkBoxPolicy.isChecked()) {
            isValidInfo(bankNumber, nameOfBank, idCard, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isValid) {
                    if(isValid) {
                        InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());

                        // Lấy ngày hiện tại
                        LocalDate currentDate = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String formattedCurrentDate = currentDate.format(formatter);

                        // Tính ngày gia hạn
                        // Thêm 30 ngày vào ngày hiện tại để tính ngày gia hạn
                        LocalDate expirationDate = currentDate.plusDays(30);
                        String formattedExpiredDate = expirationDate.format(formatter);

                        // Kiểm tra số tài khoản ngân hàng đã được liên kết hay chưa
                        infoRegisterVipDAO.checkAlreadyRegisteredVip(bankNumber, new MyCallBack<Boolean>() {
                            @Override
                            public void onCallback(Boolean isAlready) {
                                if(isAlready) {
                                    Toast.makeText(RegisterVipActivity.this, "Số tài khoản ngân hàng đã được đăng kí trước đó", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                InfoRegisterVip info = new InfoRegisterVip(bankNumber, nameOfBank, idCard, idUserLogged, formattedCurrentDate, formattedExpiredDate);
                                infoRegisterVipDAO.addInfoRegisterVip(info, new MyCallBack<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result1) {
                                        if(result1) {
                                            UserDAO userDAO = new UserDAO(getApplicationContext());
                                            userDAO.registerVip(idUserLogged, true, new MyCallBack<Boolean>() {
                                                @Override
                                                public void onCallback(Boolean result2) {
                                                    if(result2) {
                                                        Toast.makeText(RegisterVipActivity.this, "Đăng kí VIP thành công", Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                    } else {
                                                        Toast.makeText(RegisterVipActivity.this, "Đăng kí VIP thất bại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(RegisterVipActivity.this, "Đăng kí VIP thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    public void handleCancelRegisterVip() {
        String bankNumber = edtBankNumber.getText().toString();
        String nameOfBank = edtNameOfBank.getText().toString();
        String idCard = edtIdCard.getText().toString();

        if(checkBoxPolicy.isChecked()) {
            isValidInfo(bankNumber, nameOfBank, idCard, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isValid) {
                    if(isValid) {
                        InfoRegisterVip info = new InfoRegisterVip(bankNumber, nameOfBank, idCard, idUserLogged);
                        InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
                        // Kiểm tra thông tin người dùng đã tồn tại chưa
                        infoRegisterVipDAO.checkExistInfo(info, new MyCallBack<Boolean>() {
                            @Override
                            public void onCallback(Boolean isCheck) {
                                if(isCheck) {
                                    infoRegisterVipDAO.removeInfoRegisterVip(idUserLogged, new MyCallBack<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean result1) {
                                            if(result1) {
                                                UserDAO userDAO = new UserDAO(getApplicationContext());
                                                userDAO.registerVip(idUserLogged, false, new MyCallBack<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean result2) {
                                                        if(result2) {
                                                            Toast.makeText(RegisterVipActivity.this, "Hủy đăng kí VIP thành công", Toast.LENGTH_SHORT).show();
                                                            onBackPressed();
                                                        } else {
                                                            Toast.makeText(RegisterVipActivity.this, "Hủy đăng kí VIP thất bại", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterVipActivity.this, "Thông tin không trùng khớp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void cancelRegisterVipAutomatically() {
        InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
        infoRegisterVipDAO.removeInfoRegisterVip(idUserLogged, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isSuccess) {
                if(isSuccess) {
                    UserDAO userDAO = new UserDAO(getApplicationContext());
                    userDAO.registerVip(idUserLogged, false, new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean result) {
                            if(result) {
                                Toast.makeText(RegisterVipActivity.this, "Tài khoản VIP của bạn đã tự động hủy do hết thời hạn sử dụng. Vui lòng đăng kí lại.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }
                    });

                }
            }
        });
    }

    public void handleUpdateInfoRegisterVip() {
        String bankNumber = edtBankNumber.getText().toString();
        String nameOfBank = edtNameOfBank.getText().toString();
        String idCard = edtIdCard.getText().toString();
        String expiredDate = edtExpiredDate.getText().toString();

        // Kiểm tra số tài khoản ngân hàng đã được liên kết hay chưa
        InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
        infoRegisterVipDAO.checkAlreadyRegisteredVip(bankNumber, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isAlready) {
                if(isAlready && !bankNumber.equals(info.getNumberBank())) {
                    Toast.makeText(RegisterVipActivity.this, "Số tài khoản ngân hàng đã được đăng kí trước đó", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        // Cập nhật thông tin tài khoản VIP
        info = new InfoRegisterVip(bankNumber, nameOfBank, idCard, idUserLogged, "", CommonConverter.convertDateUS(expiredDate));
        infoRegisterVipDAO.updateInfoRegisterVip(info, new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                if(result) {
                    Toast.makeText(RegisterVipActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(RegisterVipActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void handleExpiredDateMore() {
        // Tính ngày gia hạn
        // Thêm 30 ngày vào ngày hiện tại để tính ngày gia hạn
        InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
        infoRegisterVipDAO.getInfoRegisterVip(idUserLogged, new MyCallBack<InfoRegisterVip>() {
            @Override
            public void onCallback(InfoRegisterVip info) {
                if(info != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate currentExpiredDate = LocalDate.parse(info.getExpiredDate());
                    LocalDate expirationDate = currentExpiredDate.plusDays(30);
                    String newExpiredDate = expirationDate.format(formatter);

                    InfoRegisterVipDAO infoRegisterVipDAO = new InfoRegisterVipDAO(getApplicationContext());
                    infoRegisterVipDAO.updateExpiredDateMore(newExpiredDate, idUserLogged, new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean isSuccess) {
                            if(isSuccess) {
                                Toast.makeText(RegisterVipActivity.this, "Gia hạn thành công", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(RegisterVipActivity.this, "Gia hạn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public int compareToBetweenTwoDate(String expiredDate, String currentDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date d1 = format.parse(expiredDate);
            Date d2 = format.parse(currentDate);

            int result = d1.compareTo(d2);
            // < 0: đã hết hạn
            // = 0: đã hết hạn
            // > 0: chưa hết hạn
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
