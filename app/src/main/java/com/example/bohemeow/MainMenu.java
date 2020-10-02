package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class MainMenu extends AppCompatActivity {

    //뒤로가기 두번 시 종료되도록 구현 예정
    private long backKeyPressedTime = 0;
    private Toast toast;
    ImageView windowIV;
    String username;
    int catType;
    String phoneNumber;
    Random rnd;

    public void getRankAndStartActivity(){
        final int[] rank = new int[5];
        final int[] catTypes = new int[5];
        final String[] usernames = new String[5];
        final int[] points = new int[5];
        final String[] introductions = new String[5];

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("user_list");
        final Query[] query = {mPostReference.orderByChild("level").limitToLast(3)};
        query[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int cnt = 0;

                    // get 1st, 2nd, 3rd user's data
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        UserData get = issue.getValue(UserData.class);

                        rank[2 - cnt] = 3 - cnt;
                        catTypes[2 - cnt] = get.catType;
                        usernames[2 - cnt] = get.nickname;
                        points[2 - cnt] = get.level;
                        introductions[2 - cnt] = get.introduction;

                        cnt++;
                    }
                }

                // get player rank
                query[0] = mPostReference.orderByChild("level");
                query[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long size = dataSnapshot.getChildrenCount();
                        int cnt = 0;
                        int user_rank = 0;

                        boolean isArrived = false;

                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                UserData get = issue.getValue(UserData.class);
                                if(get.nickname.equals(username)){

                                    isArrived = true;

                                    rank[4] = (int) (size - cnt);
                                    catTypes[4] = get.catType;
                                    usernames[4] = get.nickname;
                                    points[4] = get.level;
                                    introductions[4] = get.introduction;

                                    user_rank = rank[4];

                                    // 플레이어가 1위면 위에 아무도 없어야함!
                                    if(user_rank == 1){
                                        rank[3] = 0;
                                        catTypes[3] = 1;
                                        usernames[3] = "당신이 1위입니다!";
                                        points[3] = 0;
                                        introductions[3] = get.introduction;

                                        break;
                                    }
                                }
                                // 플레이어 바로 위의 유저 정보
                                else if(isArrived){
                                    rank[3] = user_rank -1;
                                    catTypes[3] = get.catType;
                                    usernames[3] = get.nickname;
                                    points[3] = get.level;
                                    introductions[3] = get.introduction;

                                    break;
                                }
                                cnt++;
                            }
                        }

                        // put information's to intent
                        Intent intent = new Intent(MainMenu.this, RankActivity.class);
                        intent.putExtra("rank", rank);
                        intent.putExtra("catTypes", catTypes);
                        intent.putExtra("points", points);
                        intent.putExtra("usernames", usernames);
                        intent.putExtra("introductions", introductions);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        windowIV = findViewById(R.id.iv_window);


        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH");
        df.setTimeZone(tz);
        int time = Integer.parseInt(df.format(date));

        if(time <= 5 || time >= 20){
            windowIV.setImageResource(R.drawable.main_0005_windownight);
        }
        else if(time >= 18){
            windowIV.setImageResource(R.drawable.windowsunset);
        }
        else if(time <= 8){
            windowIV.setImageResource(R.drawable.windowmorning);
        }

        /*
        rnd = new Random();
        int num = rnd.nextInt(2);
        if(num == 1){
            select_btn.setBackgroundResource(R.drawable.main_cat_scaratch);
        }

         */


        UpdateBackground();

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");
        catType = registerInfo.getInt("userCatType", 1);

        //phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        Intent intent = getIntent();

        Button communityBtn = (Button) findViewById(R.id.btn_community);
        communityBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        // need to change someday
        final int[] icons = {R.drawable.beth_0000, R.drawable._0011_hangang_lay, R.drawable._0008_bamee_sit, R.drawable._0005_chacha_scratch,
                R.drawable._0004_ryoni_scratch, R.drawable._0003_moonmoon_sit, R.drawable._0000_popo_lay,R.drawable._0002_taetae_sit, R.drawable._0001_sessak_lay};

        final Button selectBtn = (Button) findViewById(R.id.btn_to_select);
        selectBtn.setBackgroundResource(icons[catType]);
        selectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SelectActivity.class);
                startActivity(intent);
            }
        });

        Button configBtn = (Button) findViewById(R.id.btn_itemboard);
        configBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData get = dataSnapshot.child(username).getValue(UserData.class);
                        System.out.println(get);
                        Intent intent = new Intent(MainMenu.this, MainConfigActivity.class);
                        intent.putExtra("userdata", get);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final Button rankBtn = findViewById(R.id.btn_rank);
        rankBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getRankAndStartActivity();
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();

            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public void UpdateBackground(){
        ConstraintLayout background = findViewById(R.id.mainmenu_background);
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int backgroundImageCode = userInfo.getInt("backgroundImageCode", 1);
        switch(backgroundImageCode){
            case 1: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0001_blue));
                break;
            case 2: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0000_yellow));
                break;
            case 3: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0002_green));
                break;
            case 4: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0003_red));
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            switch(resultCode){
                case 0: // background case
                    UpdateBackground();
                    break;
                case 1: // logout case
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", "NULL");
                    editor.commit();

                    Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

}
