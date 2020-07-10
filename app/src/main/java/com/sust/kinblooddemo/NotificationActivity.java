package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hitomi.cmlibrary.CircleMenu;
import com.sust.kinblooddemo.notification.APIService;
import com.sust.kinblooddemo.notification.Client;
import com.sust.kinblooddemo.notification.Data;
import com.sust.kinblooddemo.notification.MyResponse;
import com.sust.kinblooddemo.notification.NotificationSender;
import com.sust.kinblooddemo.notification.Token;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseAuth FIREBASE_AUTH;
    private FirebaseUser FIREBASE_USER;
    private String uid;
    private DocumentReference DOCUMENT_REFERENCE;

    private Dialog toastMessageDialog;
    private int bG = 0;
    private APIService apiService;
    private EditText hospital, condition, noOfBags;
    private String hospital_, condition_, noOfBags_, blood_group;
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        FIREBASE_AUTH = FirebaseAuth.getInstance();
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        uid = FIREBASE_USER.getUid();
        DOCUMENT_REFERENCE = FirebaseFirestore.getInstance().collection("Users").document(uid);

        toastMessageDialog = new Dialog(NotificationActivity.this);

        CircleMenu circleMenu = findViewById(R.id.circleMenu);
        hospital = findViewById(R.id.et_hospital);
        condition = findViewById(R.id.et_condition);
        noOfBags = findViewById(R.id.et_noOfBags);
        ImageView home= findViewById(R.id.iv_home);
        TextView selectedBloodGroup = findViewById(R.id.tv_blood);
        Button send = findViewById(R.id.btn_sendNotif);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        home.setOnClickListener(view -> startActivity(new Intent(NotificationActivity.this, HomeActivity.class)));

        circleMenu.setMainMenu(Color.parseColor("#EEF4DC"), R.drawable.ic_blood, R.drawable.ic_app_logo_40dp)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_negative)
                .setOnMenuSelectedListener(index -> {
                    bG = 1;
                    blood_group = bloodGroups[index];
                    Toast.makeText(NotificationActivity.this, blood_group + " selected", Toast.LENGTH_LONG).show();
                    selectedBloodGroup.setText(blood_group);
                });

        send.setOnClickListener(v -> {
            if (isOnline()){
                hospital_ = hospital.getText().toString().trim();
                if (hospital_.isEmpty()) {
                    hospital.setError("Field cannot be empty");
                    hospital.requestFocus();
                    return;
                }
                condition_ = condition.getText().toString().trim();
                if (condition_.isEmpty()) {
                    condition.setError("Field cannot be empty");
                    condition.requestFocus();
                    return;
                }
                noOfBags_ = noOfBags.getText().toString().trim();
                if (noOfBags_.isEmpty()) {
                    noOfBags.setError("Field cannot be empty");
                    noOfBags.requestFocus();
                    return;
                }
                if (bG == 0) {
                    Toast.makeText(NotificationActivity.this, "Please select your blood group", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Tokens").child("5OF7T9cyDHXfvvQor8P1weYZ1qw2").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userToken = dataSnapshot.getValue(String.class);
                        DOCUMENT_REFERENCE.get().addOnSuccessListener(documentSnapshot -> {
                            SignupHelper signupHelper = documentSnapshot.toObject(SignupHelper.class);
                            String fullName = signupHelper.getFullName();
                            String phoneNumber = signupHelper.getPhoneNumber();
                            sendNotifications(userToken, blood_group, hospital_, condition_, noOfBags_, uid, fullName, phoneNumber);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                showToastMessage("No Internet Connection!");
            }
        });
        updateToken();
    }

    private void updateToken() {
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(uid).setValue(token);
    }

    public void sendNotifications(String userToken, String bloodGroup, String hospital, String condition, String noOfBags, String uid, String fullName, String phoneNumber) {
        Data data = new Data(bloodGroup, hospital, condition, noOfBags, uid, fullName, phoneNumber);
        NotificationSender sender = new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                Log.d("fire", "Response: " + response.toString());
                if (response.code() == 200) {
                    if(response.body() != null) {
                        if (response.body().success != 1) {
                            Toast.makeText(NotificationActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(NotificationActivity.this, AfterNotifTemp.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showToastMessage(final String message) {
        toastMessageDialog.setContentView(R.layout.item_toast_message);
        toastMessageDialog.setCanceledOnTouchOutside(true);

        TextView toast_TextView = toastMessageDialog.findViewById(R.id.item_toast_message_toast_TextView);
        toast_TextView.setText(message);

        toastMessageDialog.show();
        Objects.requireNonNull(toastMessageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Handler().postDelayed(() -> toastMessageDialog.cancel(), 5000);
    }
}
