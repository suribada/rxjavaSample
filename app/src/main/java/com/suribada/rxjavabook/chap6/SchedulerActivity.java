package com.suribada.rxjavabook.chap6;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.view.View;

import com.suribada.rxjavabook.R;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by lia on 2017-10-17.
 */

public class SchedulerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_and_three_buttons);
    }

    public void onClickButton1(View view) {
        Scheduler scheduler = Schedulers.io();
        scheduler.shutdown();
        scheduler.scheduleDirect(() -> {
            printThread("first");
        });
        scheduler.scheduleDirect(() -> {
            printThread("second");
        });
    }

    public void onClickButton2(View view) {
        Scheduler scheduler = Schedulers.io();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            printThread("third");
        });
        worker.schedule(() -> {
            printThread("fourth");
        });
    }


    private void printThread(String value) {
        SystemClock.sleep(2000);
        System.out.println(Thread.currentThread().getName() + ": value=" + value);
    }

    public void onClickButton3(View view) {
        Scheduler scheduler = Schedulers.trampoline();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            printThread("trampoline first");
        });
        worker.schedule(() -> {
            printThread("trampoline second");
        });
    }

}
