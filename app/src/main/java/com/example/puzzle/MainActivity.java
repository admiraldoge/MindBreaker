package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LinearLayout llMain;

    EditText etName;

    TextView tvBestPlayer,tvBestScore;

    public static final String VIEW_MAIN = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText)findViewById(R.id.etName);
        tvBestPlayer = (TextView)findViewById(R.id.tvBestPlayer);
        tvBestScore = (TextView)findViewById(R.id.tvBestScore);
        llMain = (LinearLayout) findViewById(R.id.llMain);

        ViewCompat.setTransitionName(llMain, VIEW_MAIN);

        //Set the best player
        setBestPlayer();
    }

    public void setBestPlayer() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String score = extras.getString("score");
            //The key argument here must match that used in the other activity
            tvBestPlayer.setText("Player: "+name);
            tvBestScore.setText("Score: "+score);
        }
    }

    public void goToGame(View view) {

        Intent myIntent = new Intent(MainActivity.this,Game.class);
        String name = etName.getText().toString();
        myIntent.putExtra("name",name);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this,

                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<>(view.findViewById(R.id.btStart),
                        Game.VIEW_GAME));
        //ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());
        ActivityCompat.startActivity(MainActivity.this, myIntent, activityOptions.toBundle());
    }

    public void openCredits(View view) {
        CreditsDialog creditsDialog = new CreditsDialog();
        creditsDialog.show(getSupportFragmentManager(), "Credits Dialog");
    }
}
