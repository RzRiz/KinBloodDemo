package com.sust.kinblooddemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity{

    public static  FirebaseAuth FIREBASE_AUTH;
    public static FirebaseUser FIREBASE_USER;
    public static DocumentReference DOCUMENT_REFERENCE;

    private DrawerLayout drawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FIREBASE_AUTH = FirebaseAuth.getInstance();
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        DOCUMENT_REFERENCE = FirebaseFirestore.getInstance().collection("Users").document(FIREBASE_USER.getUid());

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.activity_home_nav_view);
        Toolbar toolbar = findViewById(R.id.activity_home_Toolbar);

        navigationView.bringToFront();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TextView arrow = findViewById(R.id.activity_home_arrow_TextView);
        TextView name = findViewById(R.id.activity_home_name_TextView);
        TextView email = findViewById(R.id.activity_home_email_TextView);
        TextView home = findViewById(R.id.activity_home_home_TextView);
        TextView needDonor = findViewById(R.id.activity_home_need_donor_TextView);
        TextView becomeDonor = findViewById(R.id.activity_home_become_donor_TextView);
        TextView request = findViewById(R.id.activity_home_request_TextView);
        TextView profile = findViewById(R.id.activity_home_profile_TextView);
        TextView settings = findViewById(R.id.activity_home_settings_TextView);
        TextView about = findViewById(R.id.activity_home_about_TextView);
        TextView rate = findViewById(R.id.activity_home_rate_TextView);
        TextView logOut = findViewById(R.id.activity_home_log_out_TextView);

        DOCUMENT_REFERENCE.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()){
                    name.setText(Objects.requireNonNull(document.get("fullName")).toString());
                    email.setText(Objects.requireNonNull(document.get("email")).toString());

                }
            }
        });

        arrow.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.START));

        home.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.START));

        needDonor.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, NeedDonorActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        becomeDonor.setOnClickListener(view -> {
            DOCUMENT_REFERENCE
                    .get().addOnSuccessListener(documentSnapshot -> {
                Intent intent = new Intent(HomeActivity.this, DonorRegistrationActivity.class);
                if (Objects.equals(documentSnapshot.getString("donorStatus"), "negative")) {
                    intent.putExtra("donorStatus", "negative");
                } else {
                    intent.putExtra("donorStatus", "positive");
                }
                startActivity(intent);
            }).addOnFailureListener(e -> Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        request.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, AfterNotifTemp.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        profile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        settings.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.START));

        about.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.START));

        rate.setOnClickListener(view -> drawerLayout.closeDrawer(GravityCompat.START));

        logOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(HomeActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(isTaskRoot()){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Confirm exit").setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                super.onBackPressed();
            }
        }
    }
}
