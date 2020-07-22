package com.sust.kinblood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public static FirebaseAuth FIREBASE_AUTH;
    public static FirebaseUser FIREBASE_USER;
    public static DocumentReference DOCUMENT_REFERENCE;

    private DrawerLayout drawerLayout;
    private TextView apt, bpt, abpt, opt, ant, bnt, abnt, ont, allTotal, apa, bpa, abpa, opa, ana, bna, abna, ona, allAvailable;
    private DocumentReference counterTotalDoc, counterAvailableDoc;
    private FirebaseFirestore databaseReference;
    private String bloodGroup_;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FIREBASE_AUTH = FirebaseAuth.getInstance();
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();
        DOCUMENT_REFERENCE = databaseReference.collection("Users").document(FIREBASE_USER.getUid());
        counterTotalDoc = databaseReference.collection("Counters").document("Total");
        counterAvailableDoc = databaseReference.collection("Counters").document("Available");

        apt = findViewById(R.id.activity_home_ap_donor_TextView);
        bpt = findViewById(R.id.activity_home_bp_donor_TextView);
        abpt = findViewById(R.id.activity_home_abp_donor_TextView);
        opt = findViewById(R.id.activity_home_op_donor_TextView);
        ant = findViewById(R.id.activity_home_an_donor_TextView);
        bnt = findViewById(R.id.activity_home_bn_donor_TextView);
        abnt = findViewById(R.id.activity_home_abn_donor_TextView);
        ont = findViewById(R.id.activity_home_on_donor_TextView);
        allTotal = findViewById(R.id.activity_home_total_donor_TextView);
        apa = findViewById(R.id.activity_home_ap_available_donor_TextView);
        bpa = findViewById(R.id.activity_home_bp_available_donor_TextView);
        abpa = findViewById(R.id.activity_home_abp_available_donor_TextView);
        opa = findViewById(R.id.activity_home_op_available_donor_TextView);
        ana = findViewById(R.id.activity_home_an_available_donor_TextView);
        bna = findViewById(R.id.activity_home_bn_available_donor_TextView);
        abna = findViewById(R.id.activity_home_abn_available_donor_TextView);
        ona = findViewById(R.id.activity_home_on_available_donor_TextView);
        allAvailable = findViewById(R.id.activity_home_available_donor_TextView);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.activity_home_nav_view);
        Toolbar toolbar = findViewById(R.id.activity_home_Toolbar);

        navigationView.bringToFront();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        DOCUMENT_REFERENCE.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                name.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("fullName")));
                email.setText(Objects.requireNonNull(documentSnapshot.get("email")).toString());
                if (String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("donorStatus")).equals("positive")){
                    bloodGroup_ = Objects.requireNonNull(documentSnapshot.get("bloodGroup")).toString();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        counterTotalDoc.get().addOnSuccessListener(documentSnapshot -> {
             allTotal.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("allTotal")));
             apt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("A+")));
             bpt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("B+")));
             abpt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("AB+")));
             opt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("O+")));
             ant.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("A-")));
             bnt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("B-")));
             abnt.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("AB-")));
             ont.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("O-")));
        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

        counterAvailableDoc.get().addOnSuccessListener(documentSnapshot -> {
            allAvailable.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("availableTotal")));
            apa.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("A+")));
            bpa.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("B+")));
            abpa.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("AB+")));
            opa.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("O+")));
            ana.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("A-")));
            bna.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("B-")));
            abna.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("AB-")));
            ona.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("O-")));
        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

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
                    intent.putExtra("bloodGroup", bloodGroup_);
                    intent.putExtra("donorStatus", "positive");
                }
                startActivity(intent);
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
