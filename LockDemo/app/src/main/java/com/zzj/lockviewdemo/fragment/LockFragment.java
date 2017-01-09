package com.zzj.lockviewdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzj.lockviewdemo.R;
import com.zzj.lockviewdemo.activity.MainActivity;
import com.zzj.lockviewdemo.views.LockView;

/**
 * Created by zzj on 2016/11/18.
 */
public class LockFragment extends Fragment implements LockView.onPatternChangeListener{
    private TextView lockHint;
    private LockView lockView;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean isTrue;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_lock,null);
        initViews(view);
        sp = getActivity().getSharedPreferences("lock", Activity.MODE_PRIVATE);
        editor = sp.edit();
        isTrue = true;
        return view;
    }

    @Override
    public void onPatternChange(String passwordstr) {
        if(passwordstr!=null){
            String settingpass = sp.getString("settingpassword2","");
            if(passwordstr.equals(settingpass)){
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }else {
                lockHint.setText("密码错误，请重新绘制");
            }
        }else {
            lockHint.setText("绘制手势密码,请至少连接四个点");
        }
    }

    @Override
    public void onPatternStart(boolean isClick) {
        if(isClick){
            lockHint.setText("请绘制图案");
        }
    }

    private void initViews (View view ){
        lockHint = (TextView) view.findViewById(R.id.act_main_lockhint);
        lockView = (LockView) view.findViewById(R.id.act_main_lockview);
        lockView.setPatternChangeListener(this);
    }
}
