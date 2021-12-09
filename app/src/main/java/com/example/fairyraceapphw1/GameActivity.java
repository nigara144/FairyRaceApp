package com.example.fairyraceapphw1;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements SensorEventListener{
    private static final int ADD_SCORE_FROM_ENEMY = 1;
    private int NUM_OF_COL = 5;
    private final int COINS_SCORE =5;
    private final String GPS_ON="gps";
    public final String CHECK_BOX = "check_box";
    private final String SCORE = "score";
    private final String NAME="name";
    private final String TEXT_SCORE = "SCORE: ";
    private final String SPEED = "speed";
    private final static int MAX_VOLUME = 100;
    private final float volume = (float) (1 - (Math.log(MAX_VOLUME - 5) / Math.log(MAX_VOLUME)));
    private RelativeLayout relativeLayout;
    private View player;
    private View[] enemies;
    private View[] coins;
    private ValueAnimator[] enemyArr;
    private ValueAnimator[] coinsArr;
    private ImageView[] life_status;
    private int life = 3;
    private int countOfPressedBack=0;
    private ImageView btnLeft;
    private ImageView btnRight;
    private int screenHeight;
    private int screenWidth;
    private int score=0;
    private TextView scoreView;
    private MediaPlayer crashSound;
    private SensorManager sensorManager;
    static int x = 360;
    private boolean isSensor;
    private Sensor accelerometer;
    private int speed;
    private Vibrator vibrator;
    private static final long[] VIBRATE_PATTERN = { 500, 500 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //UI starts
        setContentView(R.layout.activity_game);

        Toast.makeText(GameActivity.this,
                "Get away from the fire", Toast.LENGTH_SHORT).show();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sendStatusGpsToEndActivity();
        initialViews();
        screenHeightAndWidth();
        enemyArr = new ValueAnimator[5];
        coinsArr = new ValueAnimator[5];
        Intent intent = getIntent();
        speed = intent.getIntExtra(SPEED, 3500);

        //Always make screen light
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onClickResumeOrPauseOrStop();
        buttonsOrSensor();
        //create bonus animations and create enemies animation
        coinsAnimate();
        enemiesAnimate();
    }

        private void initialViews(){ //initial views
            relativeLayout = (RelativeLayout) findViewById(R.id.rl);
            player = (View) findViewById(R.id.player);
            enemies = new View[5];
            enemies[0] = (View) findViewById(R.id.enemy1);
            enemies[1] = (View) findViewById(R.id.enemy2);
            enemies[2] = (View) findViewById(R.id.enemy3);
            enemies[3] = (View) findViewById(R.id.enemy4);
            enemies[4] = (View) findViewById(R.id.enemy5);

            coins = new View[5];
            coins[0] = (View) findViewById(R.id.coin1);
            coins[1] = (View) findViewById(R.id.coin2);
            coins[2] = (View) findViewById(R.id.coin3);
            coins[3] = (View) findViewById(R.id.coin4);
            coins[4] = (View) findViewById(R.id.coin5);

            life_status = new ImageView[3];
            life_status[0] = (ImageView) findViewById(R.id.life_status1);
            life_status[1] = (ImageView) findViewById(R.id.life_status2);
            life_status[2] = (ImageView) findViewById(R.id.life_status3);
            scoreView = findViewById(R.id.score_view);

            //initial Buttons
            btnLeft = (ImageView) findViewById(R.id.move_left);
            btnRight = (ImageView) findViewById(R.id.move_right);

            //initial score
            scoreView.setText(TEXT_SCORE + '0');

            //initial position
            for (int i = 0; i < NUM_OF_COL; i++) {
                enemies[i].setTranslationY(-170);
                coins[i].setTranslationY(-320);
            }
        }

    //--------Methods----------//
    private void sendStatusGpsToEndActivity() {

    }

    private void screenHeightAndWidth(){
        //get screenHeight and width
        WindowManager wm=getWindowManager();
        Display disp= wm.getDefaultDisplay();
        Point size=new Point();
        disp.getSize(size);
        screenHeight=size.y;
        screenWidth =size.x;
    }

        //create animation
        public void coinsAnimate() {
            coinsArr[0] = ValueAnimator.ofInt(-260, screenHeight + 400);
            coinsArr[0].setDuration(speed).setRepeatCount(Animation.INFINITE);
            coinsArr[0].setStartDelay(10000);
            coinsArr[0].start();
            coinsArr[0].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    int animatedValue = (int) updatedAnimation.getAnimatedValue();
                    coins[0].setTranslationY(animatedValue);
                    if (isCollision(coins[0], player)) {
                        addBonusScore();
                        coins[0].setY(-170);
                        updatedAnimation.start();
                    }
                }
            });
            coinsArr[1] = ValueAnimator.ofInt(-260, screenHeight + 400);
            coinsArr[1].setDuration(speed).setRepeatCount(Animation.INFINITE);
            coinsArr[1].setStartDelay(22000);
            coinsArr[1].start();
            coinsArr[1].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    int animatedValue = (int) updatedAnimation.getAnimatedValue();
                    coins[1].setTranslationY(animatedValue);
                    if (isCollision(coins[1], player)) {
                        addBonusScore();
                        coins[1].setY(-170);
                        updatedAnimation.start();
                    }
                }
            });

            coinsArr[2] = ValueAnimator.ofInt(-260, screenHeight + 400);
            coinsArr[2].setDuration(speed).setRepeatCount(Animation.INFINITE);
            coinsArr[2].setStartDelay(10000);
            coinsArr[2].start();
            coinsArr[2].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    int animatedValue = (int) updatedAnimation.getAnimatedValue();
                    coins[2].setTranslationY(animatedValue);
                    if (isCollision(coins[2], player)) {
                        addBonusScore();
                        coins[2].setY(-170);
                        updatedAnimation.start();
                    }
                }
            });

            coinsArr[3] = ValueAnimator.ofInt(-260, screenHeight + 400);
            coinsArr[3].setDuration(speed).setRepeatCount(Animation.INFINITE);
            coinsArr[3].setStartDelay(17000);
            coinsArr[3].start();
            coinsArr[3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    int animatedValue = (int) updatedAnimation.getAnimatedValue();
                    coins[3].setTranslationY(animatedValue);
                    if (isCollision(coins[3], player)) {
                        addBonusScore();
                        coins[3].setY(-170);
                        updatedAnimation.start();
                    }
                }
            });

            coinsArr[4] = ValueAnimator.ofInt(-260, screenHeight + 400);
            coinsArr[4].setDuration(speed).setRepeatCount(Animation.INFINITE);
            coinsArr[4].setStartDelay(12000);
            coinsArr[4].start();
            coinsArr[4].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    int animatedValue = (int) updatedAnimation.getAnimatedValue();
                    coins[4].setTranslationY(animatedValue);
                    if (isCollision(coins[4], player)) {
                        addBonusScore();
                        coins[4].setY(-170);
                        updatedAnimation.start();
                    }
                }
            });
        }

    private void enemiesAnimate() {
        enemyArr[0] = ValueAnimator.ofInt(-130, screenHeight + 400);
        enemyArr[0].setDuration(speed).setRepeatCount(Animation.INFINITE);
        enemyArr[0].setStartDelay(200);
        enemyArr[0].start();
        enemyArr[0].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                int animatedValue = (int) updatedAnimation.getAnimatedValue();
                enemies[0].setTranslationY(animatedValue);
                if (isCollision(enemies[0], player)) {
                    enemies[0].setY(-170);
                    hitCheck();
                    crashSound = MediaPlayer.create(getApplicationContext(),R.raw.crash);
                    crashSound.setLooping(false);
                    crashSound.setVolume(volume, volume);
                    crashSound.start();
                    Toast.makeText(getApplicationContext(), "Burned!", Toast.LENGTH_SHORT).show();
                    updatedAnimation.start();
                }
                addScore(enemies[0], updatedAnimation);

            }
        });

        enemyArr[1] = ValueAnimator.ofInt(-130, screenHeight + 400);
        enemyArr[1].setDuration(speed).setRepeatCount(Animation.INFINITE);
        enemyArr[1].setStartDelay(2200);
        enemyArr[1].start();
        enemyArr[1].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                int animatedValue = (int) updatedAnimation.getAnimatedValue();

                enemies[1].setTranslationY(animatedValue);
                if (isCollision(enemies[1], player)) {
                    enemies[1].setY(-170);
                    hitCheck();
                    crashSound = MediaPlayer.create(getApplicationContext(),R.raw.crash);
                    crashSound.setLooping(false);
                    crashSound.setVolume(volume, volume);
                    crashSound.start();
                    Toast.makeText(getApplicationContext(), "Burned!", Toast.LENGTH_SHORT).show();
                    updatedAnimation.start();
                }
                addScore(enemies[1], updatedAnimation);
            }
        });

        enemyArr[2] = ValueAnimator.ofInt(-130, screenHeight + 400);
        enemyArr[2].setDuration(speed).setRepeatCount(Animation.INFINITE);
        enemyArr[2].setStartDelay(1100);
        enemyArr[2].start();
        enemyArr[2].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                int animatedValue = (int) updatedAnimation.getAnimatedValue();
                enemies[2].setTranslationY(animatedValue);
                if (isCollision(enemies[2], player)) {
                    enemies[2].setY(-170);
                    hitCheck();
                    crashSound = MediaPlayer.create(getApplicationContext(),R.raw.crash);
                    crashSound.setLooping(false);
                    crashSound.setVolume(volume, volume);
                    crashSound.start();
                    Toast.makeText(getApplicationContext(), "Burned!", Toast.LENGTH_SHORT).show();
                    updatedAnimation.start();
                }
                addScore(enemies[2], updatedAnimation);
            }
        });


        enemyArr[3] = ValueAnimator.ofInt(-130, screenHeight + 400);
        enemyArr[3].setDuration(speed).setRepeatCount(Animation.INFINITE);
        enemyArr[3].setStartDelay(1500);
        enemyArr[3].start();
        enemyArr[3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                int animatedValue = (int) updatedAnimation.getAnimatedValue();
                enemies[3].setTranslationY(animatedValue);
                if (isCollision(enemies[3], player)) {
                    enemies[3].setY(-170);
                    hitCheck();
                    crashSound = MediaPlayer.create(getApplicationContext(),R.raw.crash);
                    crashSound.setLooping(false);
                    crashSound.setVolume(volume, volume);
                    crashSound.start();
                    Toast.makeText(getApplicationContext(), "Burned!", Toast.LENGTH_SHORT).show();
                    updatedAnimation.start();
                }
                addScore(enemies[3], updatedAnimation);
            }
        });


        enemyArr[4] = ValueAnimator.ofInt(-130, screenHeight + 400);
        enemyArr[4].setDuration(speed).setRepeatCount(Animation.INFINITE);
        enemyArr[4].setStartDelay(1300);
        enemyArr[4].start();
        enemyArr[4].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                int animatedValue = (int) updatedAnimation.getAnimatedValue();
                enemies[4].setTranslationY(animatedValue);
                if (isCollision(enemies[4], player)) {
                    enemies[4].setY(-170);
                    hitCheck();
                    crashSound = MediaPlayer.create(getApplicationContext(),R.raw.crash);
                    crashSound.setLooping(false);
                    crashSound.setVolume(volume, volume);
                    crashSound.start();
                    Toast.makeText(getApplicationContext(), "Burned!", Toast.LENGTH_SHORT).show();
                    updatedAnimation.start();
                }
                addScore(enemies[4], updatedAnimation);
            }
        });
    }

    private synchronized void addScore(View enemy,ValueAnimator updatedAnimation){
        if(enemy.getY()>player.getY()+player.getHeight()){
            score +=ADD_SCORE_FROM_ENEMY;
            scoreView.setText(TEXT_SCORE + score);
            updatedAnimation.start();
        }
    }

    private synchronized void addBonusScore(){
        score += COINS_SCORE;
        scoreView.setText(TEXT_SCORE + score +" +5");
    }

    private synchronized  void hitCheck() {
            this.life--;
        if (life == 0) {
            life_status[0].setVisibility(View.INVISIBLE);
            sendIntent();
            finish();
        } else if (life == 1) {
            life_status[1].setVisibility(View.INVISIBLE);

        } else if (life == 2) {
            life_status[2].setVisibility(View.INVISIBLE);
        }
        return;
    }

    private boolean isCollision(View e,View p) {
        int[] enemy_locate = new int[2];
        int[] player_locate = new int[2];

        e.getLocationOnScreen(enemy_locate);
        p.getLocationOnScreen(player_locate);

        Rect rect1=new Rect(enemy_locate[0],enemy_locate[1],(int)(enemy_locate[0]+ e.getWidth()),(int)(enemy_locate[1]+e.getHeight()));
        Rect rect2=new Rect(player_locate[0],player_locate[1],(int)(player_locate[0]+ p.getWidth()),(int)(player_locate[1]+p.getHeight()));

        return Rect.intersects(rect1,rect2);
    }

    private void buttonsOrSensor(){
        Intent intent=getIntent();
        //play with buttons
        if(!(intent.getBooleanExtra(CHECK_BOX,false))) {
            isSensor=false;
            //move right
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player.getX() < (getResources().getDisplayMetrics().widthPixels * (NUM_OF_COL - 1) / NUM_OF_COL))
                        player.setX(player.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
                    scoreView.setTextColor(Color.WHITE);
                }
            });
            //move left
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player.getX() >= (getResources().getDisplayMetrics().widthPixels * 1 / NUM_OF_COL))
                        player.setX(player.getX() - getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
                    scoreView.setTextColor(Color.WHITE);
                }
            });
        }else{//play with Motion Sensors

            btnLeft.setVisibility(View.INVISIBLE);
            btnRight.setVisibility(View.INVISIBLE);
            isSensor=true;
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void sendIntent(){
        Intent intent=getIntent();
        Intent gameActivityIntent = new Intent(GameActivity.this, EndActivity.class);
        gameActivityIntent.putExtra(SCORE,score);
        gameActivityIntent.putExtra(CHECK_BOX,intent.getBooleanExtra(CHECK_BOX,false));
        gameActivityIntent.putExtra(NAME,intent.getStringExtra(NAME));
        gameActivityIntent.putExtra(GPS_ON,intent.getBooleanExtra(GPS_ON,false));
        startActivity(gameActivityIntent);
    }

    private void onClickResumeOrPauseOrStop(){
        //resume
        findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<NUM_OF_COL;i++){
                    enemyArr[i].resume();
                    coinsArr[i].resume();
                }

                findViewById(R.id.move_left).setEnabled(true);
                findViewById(R.id.move_right).setEnabled(true);
                if(isSensor) {
                    sensorManager.registerListener(GameActivity.this, accelerometer,
                            SensorManager.SENSOR_DELAY_FASTEST);
                }
            }
        });

        //pause
        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

        //stop
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                sendIntent();
                finish();
            }
        });
    }



    public void clickToMoveRight(View view) {
        if (player.getX() < (getResources().getDisplayMetrics().widthPixels * 2 / NUM_OF_COL))
            player.setX(player.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void clickToMoveLeft(View view) {
        if (player.getX() >= (getResources().getDisplayMetrics().widthPixels * 1 / NUM_OF_COL))
            player.setX(player.getX() - getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(int i=0;i<NUM_OF_COL;i++){
            enemyArr[i].pause();
            coinsArr[i].pause();
        }
        findViewById(R.id.move_left).setEnabled(false);
        findViewById(R.id.move_right).setEnabled(false);
        if (isSensor) {
            sensorManager.unregisterListener((SensorEventListener) this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        countOfPressedBack=0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(int i=0;i<NUM_OF_COL;i++){
            enemyArr[i].pause();
            coinsArr[i].pause();
        }
    }


    @Override
    public void onBackPressed() {
        if(countOfPressedBack==0) {
            onPause();
            Toast.makeText(GameActivity.this,
                    "Press again to exit", Toast.LENGTH_SHORT).show();
            countOfPressedBack++;
        }else{
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x -= (int) event.values[0];
            if (x >= 0 && x < screenWidth - player.getWidth()) {
                player.setX(x);
            }
            else if(x<0 ){
                x=0;
            }else if(x> screenWidth - player.getWidth()){
                x= screenWidth - player.getWidth();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


