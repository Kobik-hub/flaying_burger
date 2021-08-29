package com.flappy.game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompat {

    private  Button startAppBtn;
    private DatabaseReference dbReference;
    private TextView fullNameDisplay;
    LanguageManger lang;
    Dialog dialogLanguage;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = new LanguageManger(this);
        fullNameDisplay = findViewById(R.id.fullNameDisplay);
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        startAppBtn = findViewById(R.id.lunchGameBtn);


        //custom dialog layout for language pop up
        dialogLanguage = new Dialog(MainActivity.this);
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

//on click play for start the game
        startAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(getApplicationContext(), AndroidLauncher.class);
                startActivity(gameIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //import and display full name of the user from Shared Preferences
        SharedPreferences sharedpreferences =  getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE );
        String fullName = sharedpreferences.getString("fullName","");
        String fullNameMsg = getString(R.string.welcome) + ", " +  fullName;
        fullNameDisplay.setText(fullNameMsg);
    }

    //Action bar menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "You logout successfully", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
//            SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE );
//            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedpreferences.edit();
//            sharedpreferences.edit().remove("fullName").apply();
            startActivity(loginIntent);
            finish();

        }
        if(item.getItemId() == R.id.Top10Score){
            Intent topScoreIntent = new Intent(getApplicationContext(),User10TopScoreActivity.class);
            startActivity(topScoreIntent);
        }
        if(item.getItemId() == R.id.ChangeLanguage) {
               dialogLanguage.show();


        }
        return super.onOptionsItemSelected(item);
    }

}