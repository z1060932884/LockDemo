package com.zzj.lockviewdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zzj.lockviewdemo.R;
import com.zzj.lockviewdemo.fragment.LockFragment;

public class LockActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        initData();
    }



    private void initData() {
        LockFragment lockFragment = new LockFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.act_lock_layout,lockFragment).commit();
    }


}
