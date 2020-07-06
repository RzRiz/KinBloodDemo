package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sust.kinblooddemo.notification.APIService;
import com.sust.kinblooddemo.notification.Client;
import com.sust.kinblooddemo.notification.Data;
import com.sust.kinblooddemo.notification.MyResponse;
import com.sust.kinblooddemo.notification.NotificationSender;
import com.sust.kinblooddemo.notification.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification extends AppCompatActivity {

    private RadioGroup radioGroupPositive, radioGroupNegetive;
    private int bG = 0;
    private boolean isChecking = true;
    private RadioButton radioButtonBg;
    private APIService apiService;
    private EditText hospital, condition, noOfBags;
    private String hospital_, condition_, noOfBags_, blood_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        hospital = findViewById(R.id.et_hospital);
        condition = findViewById(R.id.et_condition);
        noOfBags = findViewById(R.id.et_noOfBags);
        radioGroupPositive = findViewById(R.id.rgPositiveN);
        radioGroupNegetive = findViewById(R.id.rgNegetiveN);
        Button send = findViewById(R.id.btn_sendNotif);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        send.setOnClickListener(v -> {
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
                Toast.makeText(Notification.this, "Please select your blood group", Toast.LENGTH_SHORT).show();
                return;
            }
            blood_group = radioButtonBg.getText().toString();


            FirebaseDatabase.getInstance().getReference().child("Tokens").child("TzewxuRq0pYGf0ORmliUzruV8Qu1").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String usertoken = dataSnapshot.getValue(String.class);
                    sendNotifications(usertoken, blood_group, hospital_, condition_, noOfBags_);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });

        radioGroupPositive.setOnCheckedChangeListener((group, checkedId) -> {
            bG = 1;
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                radioGroupNegetive.clearCheck();
                radioButtonBg = findViewById(checkedId);
            }
            isChecking = true;
        });
        radioGroupNegetive.setOnCheckedChangeListener((group, checkedId) -> {
            bG = 1;
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                radioGroupPositive.clearCheck();
                radioButtonBg = findViewById(checkedId);
            }
            isChecking = true;
        });

        updateToken();

    }

    private void updateToken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public void sendNotifications(String userToken, String bloodGroup, String hospital, String condition, String noOfBags) {
        Data data = new Data(bloodGroup, hospital, condition, noOfBags);
        NotificationSender sender = new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(Notification.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(Notification.this, AfterNotifMap.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
