package com.example.smartchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    Animation animation;
    TextView tvSignIn,tvRegister,tvAppName;
    ImageView ivAppLogo;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSignIn=findViewById(R.id.tvSignIn);
        tvRegister=findViewById(R.id.tvRegister);
        tvAppName=findViewById(R.id.tvAppName);
        ivAppLogo=findViewById(R.id.ivAppLogo);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_down);
        tvSignIn.startAnimation(animation);
        tvRegister.startAnimation(animation);
        tvAppName.startAnimation(animation);
        ivAppLogo.startAnimation(animation);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }

}
