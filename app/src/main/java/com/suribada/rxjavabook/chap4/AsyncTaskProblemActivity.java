package com.suribada.rxjavabook.chap4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suribada.rxjavabook.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Noh.Jaechun on 2018. 4. 2..
 */
public class AsyncTaskProblemActivity extends Activity {

    private static final String TAG = "AsyncTaskProblem";

    private TextView title;
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_and_three_buttons);
        title = (TextView) findViewById(R.id.title);
        image = (ImageView) findViewById(R.id.image);
    }

    public void onClickButton1(View view) {
        new BitmapDownloadTask().execute("http://suribada.com/profile.png");
    }

    private class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                return downloadBitmap(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) { // (1) 시작
                image.setImageBitmap(null);
                Toast.makeText(AsyncTaskProblemActivity.this, "에러 발생",
                        Toast.LENGTH_LONG).show();
                return;
            } // (1)  끝
            image.setImageBitmap(bitmap);
        }

    }

    private class BitmapDownloadTask2 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                return downloadBitmap(urls[0]);
            } catch (Exception e) {
                runOnUiThread(() -> { // (1)
                    image.setImageBitmap(null);
                    Toast.makeText(AsyncTaskProblemActivity.this, "에러 발생",
                            Toast.LENGTH_LONG).show();
                }); // (1) 끝
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            }
        }

    }

    private static final int BITMAP_DOWNLOAD_ERROR = 26;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) { // (1) 시작
            switch (msg.what) {
                case BITMAP_DOWNLOAD_ERROR:
                    image.setImageBitmap(null);
                    Toast.makeText(AsyncTaskProblemActivity.this, "에러 발생",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } // (1) 끝
    };

    private class BitmapDownloadTask3 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                return downloadBitmap(urls[0]);
            } catch (Exception e) {
                handler.sendEmptyMessage(BITMAP_DOWNLOAD_ERROR); // (2)
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            }
        }

    }

    private class BitmapDownloadTask4 extends AsyncTask<String, Void, Boolean> {

        private Bitmap bitmap;

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                bitmap = downloadBitmap(urls[0]);
                return Boolean.TRUE;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // (1) 시작
                image.setImageBitmap(null);
                Toast.makeText(AsyncTaskProblemActivity.this, "에러 발생",
                        Toast.LENGTH_LONG).show();
                return;
            } // (1)  끝
            image.setImageBitmap(bitmap);
        }

    }

    private class BitmapDownloadTask5 extends AsyncTask<String, Void, Pair<Bitmap, Exception>> {

        private Pair<Bitmap, Exception> pair;

        @Override
        protected Pair<Bitmap, Exception> doInBackground(String... urls) {
            try {
                return Pair.create(downloadBitmap(urls[0]), null);
            } catch (Exception e) {
                return Pair.create(null, e);
            }
        }

        @Override
        protected void onPostExecute(Pair<Bitmap, Exception> result) {
            if (pair.second != null) { // (1) 시작
                image.setImageBitmap(null);
                Toast.makeText(AsyncTaskProblemActivity.this, "에러 발생",
                        Toast.LENGTH_LONG).show();
                return;
            } // (1)  끝
            image.setImageBitmap(pair.first);
        }

    }

    /**
     * RuntimeException을 던지면 크래시 발생
     */
    private class BitmapDownloadTask6 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                return downloadBitmap(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            }
        }

    }

    public void onClickButton2(View view) {
        new BitmapDownloadTask6().execute("http://suribada.com/profile.png");
    }

    private Bitmap downloadBitmap(String url) throws Exception {
        Random random = new Random();
        boolean success = random.nextBoolean();
        if (success) {
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawRect(0, 0, 300, 300, paint);
            return bitmap;
        }
        throw new FileNotFoundException("file does not exist");
    }

    public void onClickButton3(View view) {
        composedList.clear();
        title.setText(null);
        AsyncTaskCompat.executeParallel(new AsyncTaskA());
        AsyncTaskCompat.executeParallel(new AsyncTaskB());
    }

    private ArrayList<String> composedList = new ArrayList<>();

    private CountDownLatch latch = new CountDownLatch(1);

    private class AsyncTaskA extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            SystemClock.sleep(3000);
            return Arrays.asList("spring", "summer", "fall", "winter");
        }

        @Override
        protected void onPostExecute(List<String> result) {
            try {
                composedList.addAll(result);
                title.setText(TextUtils.join(", ", composedList));
            } catch (Exception e) {
                Toast.makeText(AsyncTaskProblemActivity.this, "Error=" + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                latch.countDown();
            }
        }
    }

    private class AsyncTaskB extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                SystemClock.sleep(1000);
                //SystemClock.sleep(5000);
                return Arrays.asList("east", "south", "west", "north");
            } catch (Exception e) {
                Log.d(TAG, "exception = " + e.getMessage());
                return null;
            } finally {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                composedList.addAll(result);
                title.setText(TextUtils.join(", ", composedList));
            }
        }

    }

}
