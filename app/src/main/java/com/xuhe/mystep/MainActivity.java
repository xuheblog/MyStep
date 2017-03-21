package com.xuhe.mystep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private SensorManager sensorManager;
    private int mWidth;
    private int mHeight;
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;
    private float count = 0;
    private String step_count = Float.toString(count);
    private boolean mRunning = true;
    private Sensor step_counter;
    private boolean heart_beat = true;
    private String tip = "本机没有计步传感器。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取步数传感器
        step_counter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(listener,step_counter,SensorManager.SENSOR_DELAY_FASTEST);

        //check the step counter
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList){
            if (sensor.getName().contains("step counter")){
                tip = "本机有计步传感器。";
            }
        }

        mWidth = windowManager.getDefaultDisplay().getWidth();
        mHeight = windowManager.getDefaultDisplay().getHeight();
        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setImageBitmap(drawFace());
        new stepTask().execute();
    }

    private Bitmap drawFace() {
        Bitmap bm = Bitmap.createBitmap(mWidth,mHeight,mConfig);
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(Color.parseColor("#fd11a34d"));
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(mWidth * 0.056f);
        canvas.drawText("计步",mWidth/2,mHeight * 0.1f,textPaint);
        textPaint.setTextSize(mWidth * 0.074f);
        canvas.drawText("今日步数",mWidth/2,mHeight * 0.27f,textPaint);

        textPaint.setTextSize(mWidth * 0.148f);
        canvas.drawText(step_count,mWidth/2,mHeight * 0.40f,textPaint);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawCircle(mWidth/2,mHeight * 1.7f,mHeight * 1.0f,paint);
        /*textPaint.setColor(Color.RED);
        if (heart_beat){
            textPaint.setTextSize(160);
            canvas.drawText("❤",mWidth/2,mHeight * 0.8f,textPaint);
        }else{
            textPaint.setTextSize(200);
            canvas.drawText("❤",mWidth/2,mHeight * 0.75f,textPaint);
        }*/

        //check the step counter
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(mWidth * 0.074f);
        canvas.drawText(tip,mWidth/2,mHeight * 0.85f,textPaint);

        return bm;
    }

    SensorEventListener listener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                step_count = Float.toString(event.values[0]);
                setStepCount(step_count);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void setStepCount(String stepCount) {
        this.step_count = stepCount;
    }

    private class stepTask extends AsyncTask<Objects,Objects,Objects>{

        @Override
        protected Objects doInBackground(Objects... params) {
            while (mRunning){
                if (heart_beat){
                    heart_beat = false;
                }else {
                    heart_beat = true;
                }
                publishProgress();
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Objects... values) {
            mImageView.setImageBitmap(drawFace());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
        //注销监听器
        if (sensorManager != null){
            sensorManager.unregisterListener(listener);
        }

    }
}
