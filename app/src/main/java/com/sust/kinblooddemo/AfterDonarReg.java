package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AfterDonarReg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_donar_reg);
    }

    public void afterDonarRegtoHome(View view){
        finish();
    }

}
