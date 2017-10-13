package com.suribada.rxjavabook.seudo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Naver on 2017. 10. 13..
 */
public class LoginManager {

    private static final Object lock = new Object();

    private static LoginManager instance;

    public static LoginManager getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance =  new LoginManager(context.getApplicationContext());
            }
            return instance;
        }
    }

    private LoginManager(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginMessage.LOGIN_FINISH);
        intentFilter.addAction(LoginMessage.LOGOUT_FINISH);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(LoginMessage.LOGIN_FINISH)) {
                    subject.onNext(true);
                } else if (action.equals(LoginMessage.LOGOUT_FINISH)) {
                    subject.onNext(false);
                }
            }
        }, intentFilter);
    }

    private BehaviorSubject<Boolean> subject = BehaviorSubject.create();

    public Observable<Boolean> getObservable() {
        return subject.distinctUntilChanged();
    }

    public void ssoLogin() {
        // 콜백 등에서 호출
        subject.onNext(true);
    }

    public void startLoginActivity() {
        // 로그인 화면 뛰워서 그 안에서 로그인하고 그 결과를 브로드캐스트
    }

    public void logout() {
        // 콜백 등에서 호출
        subject.onNext(false);
    }

}