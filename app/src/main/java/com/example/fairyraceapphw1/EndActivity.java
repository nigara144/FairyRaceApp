package com.example.fairyraceapphw1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class EndActivity extends AppCompatActivity {
    private TextView scoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        scoreView=findViewById(R.id.your_score);
        Intent intent=getIntent();
        scoreView.setText("YOUR SCORE : " + intent.getIntExtra("score",0));
    }

    //press End Game to finish the game and destroy the progress
    public void clickExit(View view) {
        moveTaskToBack(true);
        System.exit(1);
    }

    public void clickToRestart(View view) {
        Intent gameActivityIntent=new Intent(EndActivity.this,GameActivity.class);
        startActivity(gameActivityIntent);
    }
}
