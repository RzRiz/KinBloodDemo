package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AfterDonorReg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_donor_reg);
    }

    public void afterDonorRegtoHome(View view){
        finish();
    }

}
