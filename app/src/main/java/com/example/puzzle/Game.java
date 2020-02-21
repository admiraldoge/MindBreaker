package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class Game extends AppCompatActivity {

    LinearLayout llGame;

    TextView tvPlayerName,tvTries,tvScore;

    Chronometer cnTimer;

    Boolean cnTimerRunning = false;

    Integer activeBackground;

    ArrayList<Button> buttons = new ArrayList<>();
    
    Integer empty;
    
    ArrayList<Integer> dx = new ArrayList<>();
    ArrayList<Integer> dy = new ArrayList<>();

    ArrayList<String> matrixColors = new ArrayList<>();


    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_GAME = "game";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        //Init directions array
        dx.add(1);dx.add(-1);dx.add(0);dx.add(0);
        dy.add(0);dy.add(0);dy.add(1);dy.add(-1);

        //Init matrix colors
        matrixColors.add("#5F6274");
        matrixColors.add("#2F4858");
        matrixColors.add("#8A7F8C");
        matrixColors.add("#3B665F");
        matrixColors.add("#8A9785");
        matrixColors.add("#5885AF");
        matrixColors.add("#767370");
        matrixColors.add("#C15B78");
        matrixColors.add("#5BB0BA");
        matrixColors.add("#B5B1AF");

        activeBackground = 9;


        Resources r = getResources();
        String pkgName = getPackageName();


        llGame = (LinearLayout)findViewById(R.id.llGame);
        tvPlayerName = (TextView)findViewById(R.id.tvPlayerName);
        tvTries = (TextView)findViewById(R.id.tvTries);
        tvScore = (TextView)findViewById(R.id.tvScore);
        cnTimer = (Chronometer)findViewById(R.id.cnTimer);

        ViewCompat.setTransitionName(llGame, VIEW_GAME);

        String btPrefix = "btMatrix";
        for(int i = 0; i<9; i++) {
            buttons.add((Button)findViewById(r.getIdentifier(btPrefix+ i,"id",pkgName)));
            System.out.println("Adding node "+btPrefix+i+" to array");
        }

        //Retrieve player name
        generatePlayerName();

        //Generate Random Matrix at start
        randomizeMatrix();

        //Start Timer
        startChronometer();

        //Show Toast
        showToast("Ordena los dígitos, de acuerdo al dibujo");

    }

    public void generatePlayerName() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            System.out.println("Receiving: "+name);
            //The key argument here must match that used in the other activity
            tvPlayerName.setText("Player: "+name);
        }
    }

    public void startChronometer() {
        if(!cnTimerRunning) {
            cnTimer.start();
            cnTimerRunning = true;
        }
    }

    public void pauseChronometer() {
        if(cnTimerRunning) {
            cnTimer.stop();
            cnTimerRunning = false;
        }

    }

    public void restartChronometer() {
        if(cnTimerRunning) {
            cnTimer.setBase(SystemClock.elapsedRealtime());
            cnTimerRunning = false;
        }
    }

    //Show Toast
    public void showToast(String message) {
        Toast toast=Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER,0,540);
        toast.show();
    }

    //Generate Random Numbers
    public void randomizeMatrix() {
        System.out.println("ArrrayList: "+buttons.toString());
        Random rand = new Random();
        HashSet<Integer> taken = new HashSet<>();
        for(int i = 0; i<9; i++) {
            int randomNumber;
            while(true) {
                randomNumber = rand.nextInt(9);
                if (!taken.contains(randomNumber)) {
                    taken.add(randomNumber);
                    break;
                }
            }
            System.out.println("Setting "+buttons.get(i).getId()+" to: "+randomNumber);
            if(randomNumber == 0){
                buttons.get(i).setText("");
                empty = i;
            }else{
                buttons.get(i).setText(String.valueOf(randomNumber));
            }
        }
    }

    public void resetGame(View view) {
        tvTries.setText(String.valueOf(Integer.parseInt(tvTries.getText().toString())+1));
        restartChronometer();
        for(int i = 0; i < 4; i++) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    randomizeMatrix();
                }
            }, 100*i);
        }
        changeMatrixBackgroundColor();
    }

    public void changeMatrixBackgroundColor() {
        Random rand = new Random();
        Integer colorIndex = rand.nextInt(9);
        activeBackground = colorIndex;
        for(int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackgroundColor(Color.parseColor(matrixColors.get(colorIndex)));
        }
    }
    
    public void onClickButton(View view) {

        int x = -1,y= -1;

        Button button = (Button)view;
        String buttonText = (String) button.getText();
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                if(button.equals(buttons.get(i*3+j))) {
                    y = i;
                    x = j;
                    break;
                }
            }
            if(x!=-1)break;
        }

        int clickedHash = y*3+x;

        System.out.println("Clicked: "+clickedHash+" :: "+y+"-"+x);

        int ey = empty/3;
        int ex = empty%3;

        System.out.println("Empty: "+empty+" :: "+ey+"-"+ex);
        
        for(int i = 0; i < 4; i++) {

            int ny = ey + dy.get(i);
            int nx = ex + dx.get(i);

            if(ny >= 0 && nx >= 0 && ny <= 2 && nx <= 2) {
                int hash = ny * 3 + nx;
                System.out.println("Empty can move to: "+hash+" :: "+ny+"-"+nx);
                if(clickedHash == hash) {
                    //button.setBackgroundColor(Color.parseColor("#DFCAB9"));
                    buttons.get(empty).setText(String.valueOf(buttonText));
                    //buttons.get(empty).setBackgroundColor(Color.parseColor(matrixColors.get(activeBackground)));
                    button.setText("");
                    empty = hash;
                    break;
                }

            }
        }

        boolean finished = true;
        System.out.println("Probando matriz...");
        for(int i = 0; i < buttons.size(); i++) {
            int number;
            if(buttons.get(i).getText().toString() != ""){
                number = Integer.parseInt(buttons.get(i).getText().toString());
            }else{
                number = 0;
            }
            if(number != (i+1)%9) {
                finished = false;
                System.out.println("Cell wrong: "+i);
                break;

            }
        }
        if(finished){
            tvScore.setText(String.valueOf(Integer.parseInt(tvScore.getText().toString())+1));
            restartChronometer();
            changeMatrixBackgroundColor();

            for(int i = 0; i < 4; i++) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        randomizeMatrix();
                    }
                }, 100*i);
            }

            Toast toast=Toast.makeText(getApplicationContext(),"Congratulations!, it took you: "+cnTimer.getText(), Toast.LENGTH_SHORT);

            toast.setGravity(Gravity.CENTER,0,540);
            toast.show();
        }
        
    }

    public void goToMain(View view) {
        Intent myIntent = new Intent(Game.this,MainActivity.class);
        String score = tvScore.getText().toString();
        Bundle extras = getIntent().getExtras();
        String name = "";
        if (extras != null) {
            name = extras.getString("name");
            //The key argument here must match that used in the other activity
        }
        myIntent.putExtra("name",name);
        myIntent.putExtra("score",score);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                Game.this,

                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<>(view.findViewById(R.id.btGoBack),
                        MainActivity.VIEW_MAIN));
        //ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());
        ActivityCompat.startActivity(Game.this, myIntent, activityOptions.toBundle());
    }
}
