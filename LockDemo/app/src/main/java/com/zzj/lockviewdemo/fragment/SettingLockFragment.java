package com.zzj.lockviewdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zzj.lockviewdemo.R;
import com.zzj.lockviewdemo.views.LockView;

/**
 * Created by zzj on 2016/11/18.
 */
public class SettingLockFragment extends Fragment implements LockView.onPatternChangeListener{
    private TextView lockHint;
    private LockView lockView;
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
            //两次都输对的情况下
            if(isTrue){
                editor.putString("settingpassword1",passwordstr);
                editor.commit();
                isTrue = false;
                lockHint.setText("再次绘制图案进行确认");
            }else {
                String pass = sp.getString("settingpassword1","");
                if(pass.equals(passwordstr)){
                    Toast.makeText(getActivity(),"密码设置成功-------",Toast.LENGTH_SHORT).show();
                    lockHint.setText("密码设置成功");
                    editor.putString("settingpassword2",passwordstr);
                    editor.commit();
                    getActivity().finish();
                }else {
                    lockHint.setText("与上一次绘制不一致，请重新绘制");
                }
            }


        }else {
            lockHint.setText("绘制手势密码,请至少连接四个点");
        }
    }

    @Override
    public void onPatternStart(boolean isClick) {
        if(isClick){
//            lockHint.setText("请绘制图案");
        }
    }

    private void initViews (View view ){
        lockHint = (TextView) view.findViewById(R.id.act_main_lockhint);
        lockView = (LockView) view.findViewById(R.id.act_main_lockview);
        lockView.setPatternChangeListener(this);
    }
}
