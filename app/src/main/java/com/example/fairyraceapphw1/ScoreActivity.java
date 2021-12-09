package com.example.fairyraceapphw1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    public final String SHARE_PREFS = "sharedPrefs";
    public final String TEXT = "text";
    private int count =1;
    private ArrayList<Player> players_list;
    private ArrayList<TextView> textViews;
    private LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        players_list = new ArrayList<>();

        Toast.makeText(ScoreActivity.this,
                "Click on a player to show his location", Toast.LENGTH_LONG).show();
        updateViews();

    }

    private void updateViews() {
        //read from json
        SharedPreferences sharedPref = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(TEXT, null);
        Log.d("json", "updateViews: " + json);
        Type type = new TypeToken<ArrayList<Player>>() {
        }.getType();
        players_list = gson.fromJson(json, type);

        //clear screen and write all again---------
        textViews=new ArrayList<TextView>();
        linearLayout=(LinearLayout)findViewById(R.id.score_container);
        linearLayout.removeAllViews();
        textViews.clear();
        count=1;
        for (final Player player : players_list) {
            //create dynamically textView inside linearLayout
            TextView textView=new TextView(this);
            textView.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
            textView.setText("#" + (count++) + " " + player.toString());
            textViews.add(players_list.indexOf(player),textView);

            placeTextViewOnLayout(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPlayerOnMap(player.getLatitude(),player.getLongitude());
                }
            });

        }
        if (players_list == null) {
            players_list = new ArrayList<>();
        }
    }

    private void placeTextViewOnLayout(TextView textView) {
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(5,20,5,5);
        textView.setTextSize(17);
        textView.setLayoutParams(llp);
        linearLayout.addView(textView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void showPlayerOnMap(double lat,double lng){
        addFragment(new MapFragment(lat,lng),true,"one");
    }


    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.fragment_place, fragment, tag);
        ft.commitAllowingStateLoss();
    }
}



