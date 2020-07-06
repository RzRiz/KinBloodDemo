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
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        navigationView.bringToFront();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TextView arrow = findViewById(R.id.tv_arrow);
        TextView name = findViewById(R.id.tv_name);
        TextView email = findViewById(R.id.tv_email);
        TextView home = findViewById(R.id.tv_home);
        TextView needDonor = findViewById(R.id.tv_need_donor);
        TextView becomeDonor = findViewById(R.id.tv_become_donor);
        TextView request = findViewById(R.id.tv_request);
        TextView profile = findViewById(R.id.tv_profile);
        TextView settings = findViewById(R.id.tv_settings);
        TextView about = findViewById(R.id.tv_about);
        TextView rate = findViewById(R.id.tv_rate);
        TextView logOut = findViewById(R.id.tv_log_out);

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
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        becomeDonor.setOnClickListener(view -> {
            DOCUMENT_REFERENCE
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (Objects.equals(documentSnapshot.getString("donorStatus"), "negative")) {
                    startActivity(new Intent(HomeActivity.this, DonorRegistrationActivity.class));
                } else {
                    Toast.makeText(HomeActivity.this, "You are already a donor", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        request.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, AfterNotifMap.class));
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
