package com.example.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostPopupActivity extends Activity {

    String username;
    int pos;

    post pst;
    ImageView iconIV;
    ImageView contentIV;
    ImageView privateIV;

    TextView usernameTV;
    TextView contentTV;
    TextView tagTV;
    TextView timeTV;
    TextView levelTV;

    Button edit_btn;
    Button del_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_community_popup);

        final Intent intent = getIntent();
        pst = (post)intent.getSerializableExtra("post");
        username = intent.getStringExtra("username");
        pos = intent.getIntExtra("num", 0);


        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(pst.getUsername().equals(username)){
                    Intent intent1 = new Intent(PostPopupActivity.this, EditPostActivity.class);
                    intent1.putExtra("post", pst);
                    intent1.putExtra("num", pos);
                    startActivity(intent1);
                    finish();
                }
                else {
                    Toast.makeText(PostPopupActivity.this, "수정 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        del_btn = (Button) findViewById(R.id.del_btn);
        del_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(pst.getUsername().equals(username)){
                    Intent intent = new Intent( PostPopupActivity.this, DelPopupActivity.class);
                    intent.putExtra("post", pst);
                    startActivityForResult(intent, 2);


                }
                else {
                    Toast.makeText(PostPopupActivity.this, "삭제 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });




        iconIV = (ImageView)findViewById(R.id.user_icon);
        contentIV = (ImageView)findViewById(R.id.content_image);
        privateIV = findViewById(R.id.private_mark);
        usernameTV = (TextView)findViewById(R.id.user_name);
        contentTV = (TextView)findViewById(R.id.content);
        tagTV = (TextView)findViewById(R.id.tags);
        timeTV = (TextView)findViewById(R.id.time);
        levelTV = (TextView)findViewById(R.id.user_level);

        contentTV.setMovementMethod(new ScrollingMovementMethod());
        tagTV.setMovementMethod(new ScrollingMovementMethod());

        String username =pst.getUsername();
        String content =pst.getContent();
        String tag =pst.getTags();
        String time =pst.getTime();
        String uri =pst.getUri();
        int level =pst.getLevel();
        int catType =pst.getCatType();
        boolean isPublic = pst.isPublic();

        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};

        StorageReference mStorageRef;
        StorageReference islandRef;
        final long ONE_MEGABYTE = 2048 * 2048;

        if (!uri.equals("")){
            mStorageRef = FirebaseStorage.getInstance().getReference("Post_images");
            islandRef = mStorageRef.child(time + ".jpg");

            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    contentIV.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else contentIV.setImageResource(R.drawable.photoempty);

        level = calculateLevel(level);

        usernameTV.setText(username);
        contentTV.setText(content);
        tagTV.setText(tag);
        timeTV.setText(Date(time));
        levelTV.setText("Lv." + Integer.toString(level));
        iconIV.setImageResource(icons[catType]);

        if(isPublic)
            privateIV.setImageResource(R.drawable.public_mark);
        else
            privateIV.setImageResource(R.drawable.private_mark);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                //데이터 받기
                finish();
            }
        }
    }

    String Date(String time){
        String t = time.substring(0,4) + "년";

        if(time.substring(5,6).equals("0")){
            t = t + time.substring(6,7) + "월";
        }
        else t = t + time.substring(5,7) + "월";

        if(time.substring(8,9).equals("0")){
            t = t + time.substring(9,10) + "일 ";
        }
        else t = t + time.substring(8,10) + "일 ";

        t = t + time.substring(11,16);

        return t;
    }

    int calculateLevel(int score){
        int level;
        if(score >= 10000){
            score -= 10000;
            level = (score / 1500) + 11;
        }
        else{
            level = score/1000 + 1;
        }
        return level;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}