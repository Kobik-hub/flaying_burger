package com.flappy.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class User10TopScoreActivity extends AppCompat {


    ListView listView;
    ArrayAdapter<Integer> arrayAdapter;
    private DatabaseReference dbReference;
    ArrayList<Integer> arrayList;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user10_top_score);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("10topScore");

        listView = findViewById(R.id.userScoreListView);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,R.layout.text_center,arrayList);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot snap : snapshot.getChildren()){
                    arrayList.add(Integer.parseInt(Objects.requireNonNull(snap.getValue(String.class))));
                }
                Collections.sort(arrayList, Collections.reverseOrder());
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}