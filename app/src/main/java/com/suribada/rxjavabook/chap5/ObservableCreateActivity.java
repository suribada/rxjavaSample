package com.suribada.rxjavabook.chap5;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suribada.rxjavabook.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by Noh.Jaechun on 2018. 8. 6..
 */
public class ObservableCreateActivity extends Activity {

    private TextView title;
    private View button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.five_buttons);
        title = findViewById(R.id.title);
        button = findViewById(R.id.button1);
        try (PrintWriter out = new PrintWriter(openFileOutput("book.txt", Context.MODE_PRIVATE))) {
            out.println("자바");
            out.println("안드로이드");
            out.println("RxJava");
        } catch (Exception e) {
            e.printStackTrace();
        }

        clickDisposable = RxEventObservable.clicks(title)
                .subscribe(ignored -> {
                    Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();
                    title.setText("클릭 동작");
                });
    }

    private Disposable clickDisposable, fileDisposable;

    public void onClickButton1(View view) {
        if (clickDisposable != null && !clickDisposable.isDisposed()) {
            clickDisposable.dispose();
        }
    }

    /**
     * 다시 등록
     *
     * @param view
     */
    public void onClickButton2(View view) {
        clickDisposable = RxEventObservable.clicks(title)
                .subscribe(ignored -> {
                    Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();
                    title.setText("클릭 동작");
                });
    }

    public void onClickButton3(View view) {
        // first option
        try {
            fileDisposable = RxStreamReader.lines(openFileInput("book.txt")) // (1)
                .scan(new StringBuilder(), (sb, line) ->  sb.append(line + '\n')) // (2)
                .map(StringBuilder::toString) // (3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    title.setText(value);
                }, e -> Toast.makeText(this, "reading file error", Toast.LENGTH_LONG).show(),
                () -> Toast.makeText(this, "reading file complete", Toast.LENGTH_LONG).show());
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not exits", Toast.LENGTH_LONG).show(); // (3)
        }
        // second option
        fileDisposable = Single.fromCallable(() -> openFileInput("book.txt")) // (1)
            .flatMapObservable(RxStreamReader::linesGenerate) // (2)
                .scan(new StringBuilder(), (sb, line) ->  sb.append(line + '\n'))
                .map(StringBuilder::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    title.setText(value);
                }, e -> {
                    if (e instanceof FileNotFoundException) { // (3)
                        Toast.makeText(this, "File not exits", Toast.LENGTH_LONG).show();
                    } else { // (3) 끝
                        Toast.makeText(this, "reading file error", Toast.LENGTH_LONG).show();
                    }
                }, () -> Toast.makeText(this, "reading file complete", Toast.LENGTH_LONG).show());
    }

    /**
     * Observer에서 안드로이드 부분 제거
     */
    public void onClickButton4(View view) {
        // first option
        try {
            fileDisposable = RxStreamReader.lines(openFileInput("book.txt")) // (1)
                    .scan(new StringBuilder(), (sb, line) ->  sb.append(line + '\n')) // (2)
                    .map(StringBuilder::toString) // (3)
                    .subscribeOn(Schedulers.io())
                    .subscribe(System.out::println,
                        e -> System.err.println("reading file error: " + e.getMessage()),
                        () -> System.out.println("reading file complete"));
        } catch (FileNotFoundException e) {
            System.err.println("File not exits"); // (3)
        }
        // second option
        fileDisposable = Single.fromCallable(() -> openFileInput("book.txt")) // (1)
                .flatMapObservable(RxStreamReader::linesGenerate) // (2)
                .scan(new StringBuilder(), (sb, line) ->  sb.append(line + '\n'))
                .map(StringBuilder::toString)
                .subscribeOn(Schedulers.io())
                .subscribe(System.out::println,
                        e -> {
                            if (e instanceof FileNotFoundException) { // (3)
                                System.err.println("File not exits");
                            } else { // (3) 끝
                                System.err.println("reading file error: " + e.getMessage());
                            }},
                        () -> System.out.println("reading file complete"));
    }

    public void onClickButton5(View view) {
        if (fileDisposable != null && !fileDisposable.isDisposed()) {
            fileDisposable.dispose();
        }
    }

    interface StreamReadListener {

    }

    public void lines(InputStream inputStream, boolean firstLine, int fetchLines,
            int batchReadLines, int skipLines, StreamReadListener streamReadListener) {
        //...
    }

}
