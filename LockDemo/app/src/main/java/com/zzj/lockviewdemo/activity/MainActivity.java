package com.zzj.lockviewdemo.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zzj.lockviewdemo.R;
import com.zzj.lockviewdemo.fragment.LockFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView settingpassword,changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.act_main_settingpassword:
                intent.setClass(MainActivity.this,SettingPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.act_main_updatapassword:
                intent.setClass(MainActivity.this,ChangePassWordActivity.class);
                startActivity(intent);
                break;
        }
    }
    private void initView(){
        settingpassword = (TextView) findViewById(R.id.act_main_settingpassword);
        changePassword = (TextView) findViewById(R.id.act_main_updatapassword);
        settingpassword.setOnClickListener(this);
        changePassword.setOnClickListener(this);
    }


}
