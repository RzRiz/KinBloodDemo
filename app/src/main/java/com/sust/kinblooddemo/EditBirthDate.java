package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import java.util.Calendar;


public class EditBirthDate extends AppCompatActivity {

    private Button birthDate;
    private DatePickerDialog.OnDateSetListener dateSetListenerb;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), bDay, bMonth, bYear;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_birth_date);

        birthDate = findViewById(R.id.btn_birth_date);
        Button updateBirthDate = findViewById(R.id.btn_update_birth_Date);
        progressBar = findViewById(R.id.progress_birth_date);
        progressBar.setVisibility(View.GONE);

        String oldBirthDay_ = getIntent().getStringExtra("oldBirthDay");

        birthDate.setText(oldBirthDay_);

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditBirthDate.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListenerb, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        updateBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (bDay == 0){
                    finish();
                }

                BirthDateHelper birthDateHelper = new BirthDateHelper(bDay, bMonth, bYear);

                Profile.DOCUMENT_REFERENCE.set(birthDateHelper, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditBirthDate.this, "Birth date update successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditBirthDate.this, Profile.class);
                        intent.putExtra("newBirthDay", bDay + " / " + bMonth + " / " + bYear);
                        setResult(0, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditBirthDate.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        dateSetListenerb = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String date = dayOfMonth + "/" + month + "/" + year;
                birthDate.setText(date);
                bDay = dayOfMonth;
                bMonth = month;
                bYear = year;
            }
        };
    }
}
