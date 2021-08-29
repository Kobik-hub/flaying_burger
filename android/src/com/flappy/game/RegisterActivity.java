package com.flappy.game;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PatternMatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompat {
    private FirebaseAuth mAuth;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private LanguageManger lang;
    private Dialog dialogLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        lang = new LanguageManger(this);

        progressBar = findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        Button registerUser = findViewById(R.id.RegisterBtn);
        editTextFullName = findViewById(R.id.fullNameTextRegisterPage);
        editTextAge = findViewById(R.id.ageTextRegisterPage);
        editTextEmail = findViewById(R.id.emailTextRegisterPage);
        editTextPassword = findViewById(R.id.passwordTextRegisterPage);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        //custom dialog layout for language pop up
        dialogLanguage = new Dialog(RegisterActivity.this);
        dialogLanguage.setContentView(R.layout.custom_dialog);
        dialogLanguage.getWindow().setBackgroundDrawable(getDrawable(R.drawable.shape_dialog_background));
        dialogLanguage.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogLanguage.setCancelable(true);
        Button englishBtn = dialogLanguage.findViewById(R.id.englishBtn);
        Button hebrewBtn = dialogLanguage.findViewById(R.id.hebrewBtn);

        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang.updateResource("us");
                recreate();
            }
        });
        hebrewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang.updateResource("iw");
                recreate();
            }
        });
    }

    private void registerUser(){
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        // Validate the user inputs
        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is requierd");
            editTextFullName.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextAge.setError("Age is requierd");
            editTextAge.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is requierd");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is requierd");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(fullName, age, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // save the full name in SharedPreferences
                                        SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE );
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("fullName",fullName);
                                        editor.commit();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "User has been registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    }else{
                                        Toast.makeText(getApplicationContext(), "Failed to register! please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed to register! please try again111", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    //Action bar menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_and_register_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.ChangeLanguage) {
            dialogLanguage.show();
        }
        if(item.getItemId() == R.id.Top10Score){
            Intent topScoreIntent = new Intent(getApplicationContext(),User10TopScoreActivity.class);
            startActivity(topScoreIntent);
        }
        return super.onOptionsItemSelected(item);
    }


}