package com.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

import jdk.internal.jline.internal.Log;

public class FlyingBurger extends ApplicationAdapter  {

	public interface MyGameCallback {

		 void pushTopScoreList(int score);

	}

	private static MyGameCallback myGameCallback;
	public void setMyGameCallback(MyGameCallback callback) {
		myGameCallback = callback;
	}


	Sound flapSound,gameOverSound;
	SpriteBatch batch;
	Texture background,gameOver,frice;

	ShapeRenderer shapeRenderer;
	//initialize the burger player, chips and animation

	float gap = 550;
	float maxFriceOffset;
	Random randomGenertor;
	float friceVelocity = 4;

	TextureRegion []burgers;
	TextureRegion[] animationFrames;
	Animation<TextureRegion> animation;

	float elapsedTime;
	float burgerY;
	float velocity = 0;
	int gameState = 0;
	float gravity=0.5f ;
	int numberOfTubes = 4;
	float friceX[] = new float[numberOfTubes];
	float[] friceOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	//Circle and Rectangle for Collision dictation
	Circle burgerCircle  = new Circle();
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int score = 0;
	int scoringTube = 0;
	BitmapFont scoreFont;
	boolean scoreFlagFireBase = true;
	@Override
	public void create () {
//		shapeRenderer  = new ShapeRenderer();
		flapSound = Gdx.audio.newSound(Gdx.files.internal("tap_sound.wav"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game_over_sound.wav"));

		batch = new SpriteBatch();
		background = new Texture("background.png");
		gameOver = new Texture("gameOver.png");

		//Setup the burger and set up the  animate duration to 0.2sec
		burgers = new TextureRegion[2];
		burgers[0] = new TextureRegion(new Texture("flying_burger_1.png"));
		burgers[1] = new TextureRegion(new Texture("flying_burger_2.png"));
		animation = new Animation<>(0.2f, burgers[0], burgers[1]);


		//tubes
		frice = new Texture("chips.png");
		maxFriceOffset = Gdx.graphics.getHeight()/2f - gap/2 - 100;
		randomGenertor = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * (3f/4f);
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
		//score
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(10);
	}
	public void startGame(){
		burgerY = Math.round(Gdx.graphics.getHeight()/2f - burgers[0].getRegionHeight()/2f);

		//positioning the tubes and initial the rectangles for collision detection
		for (int i = 0; i < numberOfTubes; i++) {
			friceOffset[i] = (randomGenertor.nextFloat() -0.5f )* (Gdx.graphics.getHeight() - gap -500) ;
			friceX[i]  = Gdx.graphics.getWidth()/2f -  frice.getWidth()/2f + Gdx.graphics.getWidth() +  (i * distanceBetweenTubes);
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
			scoreFlagFireBase = true;
		}

	}
	@Override
	public void render () {
		//game state  = 0 -> initial state of the game
		// game state = 1 -> game is running
		// game state = 2 -> game over
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState == 1 ){
			// check if tube is out of the screen and update the score and set the next tube to be the Collision tube
			if(friceX[scoringTube % numberOfTubes] < Gdx.graphics.getWidth()/2f) {
				score++;
				scoringTube++;
			}
			if(Gdx.input.justTouched()){
				velocity= -15;
				flapSound.play();
			}
			for (int i = 0; i < numberOfTubes; i++) {
				//check for each tube if it out of the screen, is yes it will reposition the tube X, else it will decrease the tube velocity
				if (friceX[i] < -frice.getWidth()){
					friceX[i] += numberOfTubes * distanceBetweenTubes;
					friceOffset[i] = (randomGenertor.nextFloat() -0.5f )* (Gdx.graphics.getHeight() - gap -500) ;
				}else {
					friceX[i] -= friceVelocity;
				}
				//draw the tubes on the screen
				batch.draw(frice,friceX[i],Gdx.graphics.getHeight()/2f+ gap/2 + friceOffset[i]);
				batch.draw(frice, friceX[i], Gdx.graphics.getHeight()/2f - gap/2 - frice.getHeight() + friceOffset[i]);
				//draw the rectangle that match the tube size  to detect collisions on the screen
				topTubeRectangles[i] = new Rectangle(friceX[i],Gdx.graphics.getHeight()/2f+ gap/2 + friceOffset[i]-40,frice.getWidth(),frice.getHeight());
				bottomTubeRectangles[i] = new Rectangle(friceX[i], Gdx.graphics.getHeight()/2f - gap/2 - frice.getHeight() + friceOffset[i]+40,frice.getWidth(),frice.getHeight());
			}
			if(burgerY>0){
				velocity+=Gdx.graphics.getDeltaTime()*70;
				burgerY -= velocity;
			}else{
				gameState = 2;
			}
		} else if( gameState == 0){
			if(Gdx.input.justTouched()){
				gameState = 1;
			}

		}else if (gameState == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2f - gameOver.getWidth() / 2f, Gdx.graphics.getHeight() /2f - gameOver.getHeight()/2f);
			burgerY = Math.round(Gdx.graphics.getHeight()/2f - burgers[0].getRegionHeight()/2f + gameOver.getHeight());
			if(Gdx.input.justTouched()){
				gameState =1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}
		elapsedTime += Gdx.graphics.getDeltaTime();
		batch.draw(animation.getKeyFrame(elapsedTime,true),Math.round(Gdx.graphics.getWidth()/2f)-burgers[0].getRegionWidth()/2f,burgerY);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

		// crate circle for detect Collision
		burgerCircle.set(Gdx.graphics.getWidth()/2f, burgerY + burgers[0].getRegionHeight()/2f,burgers[0].getRegionHeight()/2f);
//		shapeRenderer.circle(burgerCircle.x, burgerCircle.y, burgerCircle.radius);
		for (int i=0; i<numberOfTubes; i++){
//			shapeRenderer.rect(friceX[i],Gdx.graphics.getHeight()/2f+ gap/2 + friceOffset[i],frice.getWidth(),frice.getHeight());
//			shapeRenderer.rect(friceX[i], Gdx.graphics.getHeight()/2f - gap/2 - frice.getHeight() + friceOffset[i],frice.getWidth(),frice.getHeight());
			if(Intersector.overlaps(burgerCircle,topTubeRectangles[i]) || Intersector.overlaps(burgerCircle,bottomTubeRectangles[i])){
				gameState = 2;
				//IF statment
				if (scoreFlagFireBase == true){
					gameOverSound.play(0.5f);
					scoreFlagFireBase = false;
					myGameCallback.pushTopScoreList(score);
				}
			}
		}
		scoreFont.draw(batch, String.valueOf(score), 100, 200);
		batch.end();
//		shapeRenderer.end();
}

	@Override
	public void dispose () {
	}

}
