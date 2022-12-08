package com.example.whatschat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import  com.example.whatschat.databinding.ActivityPhonenumberBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class phonenumberActivity extends AppCompatActivity {

        ActivityPhonenumberBinding binding;
        FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhonenumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Intent intent= new Intent(phonenumberActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        binding.phonebox.requestFocus();
        binding.continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(phonenumberActivity.this,OtpActivity.class);
                intent.putExtra("phoneNumber",binding.phonebox.getText().toString());
                startActivity(intent);
            }
        });
    }
}