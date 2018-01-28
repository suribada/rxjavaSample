package com.suribada.rxjavabook.seudo;

import android.app.Activity;
import android.widget.Toast;

import com.suribada.rxjavabook.error.DateFormatter;

/**
 * Created by lia on 2018-01-21.
 */

public class DateFormatterClient extends Activity {

    public void saveSchedule(Schedule schedule) {
        DateFormatter formatter = new DateFormatter();
        long timeInMillis = formatter.getTimeInMillis2(schedule.date);
        if (timeInMillis == -1L) {
            return;
        }
        //...
    }

    public void saveScheduleRxJava(Schedule schedule) {
        DateFormatter formatter = new DateFormatter();
        formatter.getTimeInMillis(schedule.date).subscribe(timeInMillis -> {
            //...
        }, e -> {
            Toast.makeText(this, "Input Error", Toast.LENGTH_LONG).show();
        });
    }

    public void saveScheduleRxJavaBlocking(Schedule schedule) {
        DateFormatter formatter = new DateFormatter();
        long timeInMillis = formatter.getTimeInMillis(schedule.date)
                .onErrorReturnItem(System.currentTimeMillis())
                .blockingGet();
        //...
    }
}