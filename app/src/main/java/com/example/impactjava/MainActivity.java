package com.example.impactjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int sum1, sum2 = 1000;
    private volatile boolean isRunning = false;
    private int loopCount;
    private double totalGCRatio;
    private Handler handler;

    Button btnIterator, btnForeach, btnLambda, btnStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIterator = findViewById(R.id.btnIterator);
        btnForeach = findViewById(R.id.btnForeach);
        btnLambda = findViewById(R.id.btnLambda);
        btnStream = findViewById(R.id.btnStream);

        btnIterator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnForeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, ForeachActivity.class);
                startActivity(intent1);
            }
        });

        btnLambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, Lambda.class);
                startActivity(intent2);
            }
        });

        btnStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(MainActivity.this, Stream.class);
                startActivity(intent3);
            }
        });

        Button startButton = findViewById(R.id.btnStart1);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExecution();
            }
        });

        Button stopButton = findViewById(R.id.btnStop1);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopExecution();
            }
        });

    }

    private void stopExecution() {
        isRunning = false;
    }

    private void startExecution() {
        isRunning = true;

        handler = new Handler();

        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(i);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int idChecked = radioGroup.getCheckedRadioButtonId();
        switch (idChecked){
            case R.id.radioButton1:
                loopCount = 0;
                totalGCRatio = 0;

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    while (isRunning){
                        for (int i = 0; i < values.size(); i++) {
                            sum1 += values.get(i);
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum1);
                        System.out.println("Thời gian chạy: " + duration + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration + " msec");
                        Toast.makeText(MainActivity.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();
                    });

                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed1);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            for (int i = 0; i < values.size(); i++) {
                                sum += values.get(i);
                            }

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Cập nhật thời gian tổng
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;

                            // Tăng số vòng lặp
                            loopCount++;

                            // Hiển thị kết quả trung bình
                            final String averageText = "" + (double) loopCount / (double) totalTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo1 = (TextView) findViewById(R.id.txtAvgNo1);
                                    txtAvgNo1.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });

                        }
                    }
                }).start();

                /*new Thread(() -> {
                    //@Override
                    //public void run() {
                        while (isRunning) {
                            long startTime = System.currentTimeMillis();

                            // Thực hiện một số phép tính trong vòng lặp
                            for (int i = 0; i < values.size(); i++) {
                                sum1 += values.get(i);
                            }

                            long endTime = System.currentTimeMillis();
                            long elapsedTime = endTime - startTime;

                            // Tính tỷ lệ GC (giả sử không có yếu tố nào khác ảnh hưởng đến GC)
                            double gcRatio = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory();

                            // Cập nhật số vòng lặp và tổng tỷ lệ GC
                            synchronized (this) {
                                loopCount++;
                                totalGCRatio += gcRatio;
                            }

                            // Cập nhật giao diện người dùng với tỷ lệ GC trung bình hiện tại
                            //handler.post(new Runnable() {
                            //    @Override
                            //    public void run() {
                            runOnUiThread(() -> {
                                    double averageGCRatio = totalGCRatio / loopCount;
                                    TextView txtAvgNo1 = (TextView) findViewById(R.id.txtAvgNo1);
                                    txtAvgNo1.setText("Average GC Ratio: " + averageGCRatio);
                            });
                            //    }
                            //});
                        }
                    //}
                }).start();*/

            case R.id.radioButton12:
                loopCount = 0;
                totalGCRatio = 0;

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    Iterator<Integer> iterator = values.iterator();
                    while (isRunning){
                        while (iterator.hasNext()) {
                            sum2 += iterator.next();
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    long duration1 = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum2);
                        System.out.println("Thời gian chạy: " + duration1 + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration1 + " msec");
                        Toast.makeText(MainActivity.this, "Duration: " + duration1 + " msec", Toast.LENGTH_LONG).show();
                    });

                    TextView txt2 = (TextView) findViewById(R.id.txtElapsed12);
                    txt2.setText("Elapsed time (msec): " + duration1);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            Iterator<Integer> iterator = values.iterator();

                            while (iterator.hasNext()) {
                                sum += iterator.next();
                            }

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Cập nhật thời gian tổng
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;

                            // Tăng số vòng lặp
                            loopCount++;

                            // Hiển thị kết quả trung bình
                            final String averageText = "" + (double) loopCount / (double) totalTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo12);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });

                        }
                    }
                }).start();

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning) {
                            long startTime = System.currentTimeMillis();

                            // Thực hiện một số phép tính trong vòng lặp
                            Iterator<Integer> iterator = values.iterator();

                            while (iterator.hasNext()) {
                                sum2 += iterator.next();
                            }

                            long endTime = System.currentTimeMillis();
                            long elapsedTime = endTime - startTime;

                            // Tính tỷ lệ GC (giả sử không có yếu tố nào khác ảnh hưởng đến GC)
                            double gcRatio = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory();

                            // Cập nhật số vòng lặp và tổng tỷ lệ GC
                            synchronized (this) {
                                loopCount++;
                                totalGCRatio += gcRatio;
                            }

                            // Cập nhật giao diện người dùng với tỷ lệ GC trung bình hiện tại
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    double averageGCRatio = totalGCRatio / loopCount;
                                    TextView txtAvgNo2 = (TextView) findViewById(R.id.txtAvgNo12);
                                    txtAvgNo2.setText("Average GC Ratio: " + averageGCRatio);
                                }
                            });
                        }
                    }
                }).start();*/

        }
    }
}