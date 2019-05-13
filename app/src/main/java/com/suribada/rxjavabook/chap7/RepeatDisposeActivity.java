package com.suribada.rxjavabook.chap7;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suribada.rxjavabook.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RepeatDisposeActivity extends Activity {

    private static final int DELAY_TIME = 5;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_and_two_buttons);
        title = findViewById(R.id.title);
    }

    public void onClickButton1(View view) {
        Observable.interval(DELAY_TIME, DELAY_TIME, TimeUnit.SECONDS)
                .takeUntil(Observable.timer(30, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> title.setText("currentDate1=" + new Date()),
                        Throwable::printStackTrace,
                        () -> Toast.makeText(this, "onComplete1", Toast.LENGTH_LONG).show());
    }

    public void onClickButton2(View view) {
        Observable.interval(DELAY_TIME, DELAY_TIME, TimeUnit.SECONDS)
                .takeUntil(destroySubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> title.setText("currentDate2=" + new Date()),
                        Throwable::printStackTrace,
                        () -> Toast.makeText(this, "onComplete2", Toast.LENGTH_LONG).show());
    }

    private Subject<String> destroySubject = PublishSubject.create();

    @Override
    protected void onDestroy() {
        destroySubject.onNext("onDestroy");
        super.onDestroy();
    }
}
