package com.zzj.lockviewdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zzj.lockviewdemo.R;
import com.zzj.lockviewdemo.fragment.SettingLockFragment;

/**  设置密码
 * Created by zzj on 2016/11/18.
 */
public class SettingPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settingpassword);
        initData();
    }
    private void initData() {
        SettingLockFragment lockFragment = new SettingLockFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.act_setting_lockfrag,lockFragment).commit();
    }

}
