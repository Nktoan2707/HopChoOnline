package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InfoRegisterVipDAO {
    private FirebaseFirestore firebaseFirestore;
    Context context;
    public InfoRegisterVipDAO(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void addInfoRegisterVip(InfoRegisterVip info, MyCallBack<Boolean> myCallBack) {
        Map<String, Object> data = new HashMap<>();
        data.put("numberBank", info.getNumberBank());
        data.put("fullNameBank", info.getFullNameBank());
        data.put("idCard", info.getIdCard());
        data.put("idUser", info.getIdUser());

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp registeredDateTimeStamps = null;
        try {
            Date date = sdf1.parse(info.getRegisteredDate());
            registeredDateTimeStamps = new Timestamp(date.getTime() / 1000, (int) ((date.getTime() % 1000) * 1000000));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp expiredDateTimeStamps = null;
        try {
            Date date = sdf2.parse(info.getExpiredDate());
            expiredDateTimeStamps = new Timestamp(date.getTime() / 1000, (int) ((date.getTime() % 1000) * 1000000));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        data.put("registeredDate", registeredDateTimeStamps);
        data.put("expiredDate", expiredDateTimeStamps);

        firebaseFirestore.collection("info_register_vip")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        myCallBack.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCallBack.onCallback(false);
                    }
                });
    }

    public void checkExistInfo(InfoRegisterVip info, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("info_register_vip")
                .whereEqualTo("numberBank", info.getNumberBank())
                .whereEqualTo("fullNameBank", info.getFullNameBank())
                .whereEqualTo("idCard", info.getIdCard())
                .whereEqualTo("idUser", info.getIdUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exist = !task.getResult().isEmpty();
                            myCallBack.onCallback(exist);
                        } else {
                            myCallBack.onCallback(false);
                        }
                    }
                });
    }

    public void removeInfoRegisterVip(String idUserLogged, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("info_register_vip")
                .whereEqualTo("idUser", idUserLogged)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                            myCallBack.onCallback(true);
                        } else {
                            myCallBack.onCallback(false);
                        }
                    }
                });
    }

    public void getInfoRegisterVip(String idUser, MyCallBack<InfoRegisterVip> myCallBack) {
        firebaseFirestore.collection("info_register_vip").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String numberBank = "";
                    String fullNameBank = "";
                    String idCard = "";
                    String registeredDateStr = "";
                    String expiredDateStr = "";
                    String idUserDB = "";

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if(entry.getKey().equals("fullNameBank")) {
                                fullNameBank = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("numberBank")) {
                                numberBank = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("idCard")) {
                                idCard = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("idUser")) {
                                idUserDB = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("registeredDate")) {
                                long timestamp;

                                Object timestampObj = entry.getValue();

                                // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                if (timestampObj instanceof Timestamp) {
                                    timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                } else {
                                    timestamp = Long.parseLong(timestampObj.toString());
                                }

                                Date date = new Date(timestamp);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                registeredDateStr = sdf.format(date);
                            }
                            if(entry.getKey().equals("expiredDate")) {
                                long timestamp;

                                Object timestampObj = entry.getValue();

                                // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                if (timestampObj instanceof Timestamp) {
                                    timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                } else {
                                    timestamp = Long.parseLong(timestampObj.toString());
                                }

                                Date date = new Date(timestamp);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                expiredDateStr = sdf.format(date);
                            }

                        }

                        if(idUserDB.equals(idUser)) {
                            myCallBack.onCallback(new InfoRegisterVip(numberBank, fullNameBank, idCard, idUser, registeredDateStr, expiredDateStr));
                            break;
                        }
                    }
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void updateInfoRegisterVip(InfoRegisterVip info, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("info_register_vip")
                .whereEqualTo("idUser", info.getIdUser())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Timestamp expiredDateTimeStamps = null;
                        try {
                            Date date = sdf.parse(info.getExpiredDate());
                            expiredDateTimeStamps = new Timestamp(date.getTime() / 1000, (int) ((date.getTime() % 1000) * 1000000));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference()
                                    .update("numberBank", info.getNumberBank(),
                                            "fullNameBank", info.getFullNameBank(),
                                            "idCard", info.getIdCard(),
                                            "expiredDate", expiredDateTimeStamps)
                                    .addOnSuccessListener(aVoid -> myCallBack.onCallback(true))
                                    .addOnFailureListener(e -> {
                                        myCallBack.onCallback(false);
                                        Log.e("Error", "Error updating info register vip: " + e.getMessage());
                                    });
                        }
                    } else {
                        myCallBack.onCallback(false);
                        Log.e("Error", "Error retrieving info register vip: " + task.getException());
                    }
                });
    }

    public void updateExpiredDateMore(String expiredDate, String idUser, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("info_register_vip")
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Timestamp expiredDateTimeStamps = null;
                        try {
                            Date date = sdf.parse(expiredDate);
                            expiredDateTimeStamps = new Timestamp(date.getTime() / 1000, (int) ((date.getTime() % 1000) * 1000000));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference()
                                    .update("expiredDate", expiredDateTimeStamps)
                                    .addOnSuccessListener(aVoid -> myCallBack.onCallback(true))
                                    .addOnFailureListener(e -> {
                                        myCallBack.onCallback(false);
                                        Log.e("Error", "Error updating info register vip: " + e.getMessage());
                                    });
                        }
                    } else {
                        myCallBack.onCallback(false);
                        Log.e("Error", "Error retrieving info register vip: " + task.getException());
                    }
                });
    }

    public void checkAlreadyRegisteredVip(String numberBank, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("info_register_vip")
                .whereEqualTo("numberBank", numberBank)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isRegistered = !task.getResult().isEmpty();
                        myCallBack.onCallback(isRegistered);
                    } else {
                        myCallBack.onCallback(false);
                        Log.e("Error", "Error checking registration: " + task.getException());
                    }
                });
    }

}
