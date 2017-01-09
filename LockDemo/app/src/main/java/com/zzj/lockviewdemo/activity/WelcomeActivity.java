package com.zzj.lockviewdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zzj on 2016/11/19.
 */
public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean isTrue;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private  void  initData(){
        sp = getSharedPreferences("lock", Activity.MODE_PRIVATE);
        editor = sp.edit();
        password = sp.getString("settingpassword2","");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!password.equals("")&&password!=null){
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this,LockActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1500);
    }
}
