package com.sust.kinblooddemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Dicision extends AppCompatActivity {

    Button buttonPositive, buttonNegetive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicision);

        buttonPositive = findViewById(R.id.btn_positive);
        buttonNegetive = findViewById(R.id.btn_negetive);

        buttonPositive.setOnClickListener(v -> {
            startActivity(new Intent(Dicision.this, DonarRegistration.class));
            finish();
        });
        buttonNegetive.setOnClickListener(v -> {
            startActivity(new Intent(Dicision.this, Home.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Dicision.this);
        builder.setTitle("Information").setMessage("We would really appreciate your dicision.\nDo you want to continue without it?").setPositiveButton("Yes", (dialog, which) -> {
            startActivity(new Intent(Dicision.this, Home.class));
            finish();
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
