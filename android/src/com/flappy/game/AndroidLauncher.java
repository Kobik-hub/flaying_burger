package com.flappy.game;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
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

public class AndroidLauncher extends AndroidApplication implements FlyingBurger.MyGameCallback {

	private DatabaseReference dbReference;
	private String userId;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		FlyingBurger game = new FlyingBurger();
		game.setMyGameCallback(this);


		initialize(new FlyingBurger(), config);
		userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
		dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("10topScore");
	}

	//handle with new scores
	@Override
	public void pushTopScoreList(int score) {

		dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
				if(snapshot.getChildrenCount() == 0 ){
					dbReference.push().setValue(Integer.toString(score));
				}else{
					ArrayList<String> scoresKeys = new ArrayList<>();
					ArrayList<Integer> scores = new ArrayList<>();

					for(DataSnapshot snap : snapshot.getChildren()){
						scoresKeys.add(snap.getKey());
						scores.add(Integer.parseInt(Objects.requireNonNull(snap.getValue(String.class))));
					}
					if(!scores.contains(score)){
						if(snapshot.getChildrenCount() < 10) {
							dbReference.push().setValue(Integer.toString(score));
						}
						else if(snapshot.getChildrenCount() ==10 ){
							int minScore = Collections.min(scores);
							if(minScore < score){
								Log.i("min", Integer.toString(minScore));
								int indexOfMinScore = scores.indexOf(minScore);
								String keyMinScore = scoresKeys.get(indexOfMinScore);
								dbReference.child(keyMinScore).setValue(Integer.toString(score));
							}
						}
					}
				}
			}
			@Override
			public void onCancelled(@NonNull @NotNull DatabaseError error) {
			}
		});
	}
}
