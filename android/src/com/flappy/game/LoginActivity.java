package com.flappy.game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompat {

    LanguageManger lang ;
    private Dialog dialogLanguage;
    private EditText editTextEmail, editTextPassword;
    Button loginBtn, registerBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseUser user;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lang = new LanguageManger(this);

        TextView banner = findViewById(R.id.textView3);
        banner.setText(getResources().getString(R.string.flying_burger));


        SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedpreferences.edit();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){

            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
            finish();
        }else{
            mAuth = FirebaseAuth.getInstance();
            loginBtn = findViewById(R.id.loginBtn);
            editTextEmail = findViewById(R.id.emailTextLoginPage);
            editTextPassword = findViewById(R.id.passwordTextLoginPage);
            progressBar = findViewById(R.id.progressBar);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    if(email.isEmpty()){
                        editTextEmail.setError("Email is required");
                        editTextEmail.requestFocus();
                        return;
                    }
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        editTextEmail.setError("Please enter a valid email!");
                        editTextEmail.requestFocus();
                        return;
                    }
                    if (password.length() < 6){
                        editTextPassword.setError("Min password length should be 6 characters!");
                        editTextPassword.requestFocus();
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                               String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        assert user != null;
                                        String  fullName = user.fullName.toString();
                                        editor.putString("fullName",fullName);
                                        editor.commit();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    }
                                });

                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            });
            registerBtn = findViewById(R.id.registerPageBtn);
            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(registerIntent);

                }
            });
        }

        //custom dialog layout for language pop up
        dialogLanguage = new Dialog(LoginActivity.this);
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
        return super.onOptionsItemSelected(item);
    }

}