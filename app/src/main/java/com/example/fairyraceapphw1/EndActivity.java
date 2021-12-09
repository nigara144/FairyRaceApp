package com.example.fairyraceapphw1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class EndActivity extends AppCompatActivity {
    private final int REQUEST_FINE_LOCATION=1234;
    public final String SHARE_PREFS = "sharedPrefs";
    public final String TEXT = "text";
    public final String GPS_ON="gps";
    private final String SCORE = "score";
    private final String NAME = "name";
    public final String CHECK_BOX = "check_box";
    private TextView scoreView;
    private ArrayList<Player> players_list;
    private Location userLocation;
    private double lat;
    private double lng;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;
    private boolean isDone=false;
    private boolean isClickToHighScoreActivity=false;
    private boolean isGpsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        isGpsOn=getIntent().getBooleanExtra(GPS_ON,false);
        //create list of players
        players_list = new ArrayList<>();
        getScoreFromGameActivity();
        listenerOfButtons();//listen to buttons
        if(!isGpsOn){
            //default location in Afeka
            this.lat=32.113601;
            this.lng=34.817774;
            Intent intent = getIntent();
            loadPlayersData();//load player_list from json
            topTenHighScore(intent.getStringExtra(NAME), lat,lng, intent.getIntExtra(SCORE, 0));
            savePlayersData();//save player_list to json

        }else{
            getLocation();//add current location to userLocation
            if(players_list.size()>=1){
                Intent intent = getIntent();
                loadPlayersData();//load player_list from json
                topTenHighScore(intent.getStringExtra(NAME), lat,lng, intent.getIntExtra(SCORE, 0));
                savePlayersData();//save player_list to json
            }
        }
    }

    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                userLocation=location;
                                lat=location.getLatitude();
                                lng=location.getLongitude();
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                if(isClickToHighScoreActivity){
                                    Intent EndActivityIntent = new Intent(EndActivity.this, ScoreActivity.class);
                                    startActivity(EndActivityIntent);
                                }
                                return;
                            }
                        }
                    });
            callBack();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    // listener to location , and return finally the location
    private void callBack(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    userLocation=location;
                    lat=location.getLatitude();
                    lng=location.getLongitude();
                }
                listenerOfButtons();
                Intent intent = getIntent();
                loadPlayersData();
                topTenHighScore(intent.getStringExtra(NAME), lat,lng, intent.getIntExtra(SCORE, 0));
                savePlayersData();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if(isClickToHighScoreActivity){
                    Intent EndActivityIntent = new Intent(EndActivity.this, ScoreActivity.class);
                    startActivity(EndActivityIntent);
                }
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            };
        };
    }

    private void getScoreFromGameActivity() {
        //catch intent from GameActivity
        scoreView=findViewById(R.id.your_score);
        Intent intent=getIntent();
        scoreView.setText("YOUR SCORE IS:\n" + intent.getIntExtra(SCORE,0));
    }

    private void listenerOfButtons() {
        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameActivityIntent=new Intent(EndActivity.this,StartActivity.class);
                startActivity(gameActivityIntent);
                finish();
            }
        });


        findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EndActivityIntent=new Intent(EndActivity.this,GameActivity.class);
                Intent intent= getIntent();
                boolean check_box= intent.getBooleanExtra(CHECK_BOX,false);
                EndActivityIntent.putExtra(CHECK_BOX,check_box);
                EndActivityIntent.putExtra(NAME,getIntent().getStringExtra(NAME));
                startActivity(EndActivityIntent);
                finish();
            }
        });

        findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                finish();
            }
        });

        findViewById(R.id.btn_high_scores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickToHighScoreActivity=true;
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                if((userLocation !=null||!isGpsOn) && isDone) {
//                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Intent EndActivityIntent = new Intent(EndActivity.this, ScoreActivity.class);
                    startActivity(EndActivityIntent);
                }else{
                    Toast.makeText(EndActivity.this, "We get your location\nPlease wait...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // search if my score is in top ten and place this player in player_list
    private void topTenHighScore(String name,double latitude,double longitude ,int score){

        if(players_list.size()<10 ) {
            findPlace(name,latitude,longitude,score);
        }else{
            if(score >= players_list.get(players_list.size()-1).getScore()) {
                players_list.remove(players_list.size() - 1);
                findPlace(name,latitude,longitude,score);
            }
        }
    }

    private void findPlace(String name,double latitude,double longitude,int score) {
        if(players_list.size()==0){
            players_list.add(new Player(name,latitude,longitude,score));
        }else {
            for (Player p : players_list) {
                if (score > p.getScore()) {
                    players_list.add(players_list.indexOf(p), new Player(name,latitude,longitude,score));
                    break;
                }
            }
            if(score < players_list.get(players_list.size()-1).getScore()){
                players_list.add(players_list.size(),new Player(name,latitude,longitude,score));
            }
            else if(score == players_list.get(players_list.size()-1).getScore()){
                players_list.add(players_list.size()-1,new Player(name,latitude,longitude,score));
            }
        }
    }

    //save data with json
    private void savePlayersData(){
        SharedPreferences sharedPref = getSharedPreferences(SHARE_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json=gson.toJson(players_list);
        editor.putString(TEXT,json);
        editor.apply();
        isDone=true;
    }

    //load data with json
    private void loadPlayersData(){
        SharedPreferences sharedPref = getSharedPreferences(SHARE_PREFS,MODE_PRIVATE);
        Gson gson = new Gson();
        String json=sharedPref.getString(TEXT,null);
        Type type= new TypeToken<ArrayList<Player>>(){}.getType();
        players_list=gson.fromJson(json,type);
        if(players_list ==null){
            players_list=new ArrayList<>();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //return to start activity
        Intent EndActivityIntent=new Intent(EndActivity.this,StartActivity.class);
        startActivity(EndActivityIntent);
    }
}
