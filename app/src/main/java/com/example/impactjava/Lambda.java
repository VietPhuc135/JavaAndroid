package com.example.impactjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;

public class Lambda extends AppCompatActivity {

    Button btnIterator, btnForeach, btnLambda, btnStream;
    volatile boolean isRunning = false;
    int gcCount;
    int sum, suml,sumla;

    void helper(Runnable action) {
        action.run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lambda);

        btnIterator = findViewById(R.id.btnIterator);
        btnForeach = findViewById(R.id.btnForeach);
        btnLambda = findViewById(R.id.btnLambda);
        btnStream = findViewById(R.id.btnStream);

        btnIterator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Lambda.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        btnForeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Lambda.this, ForeachActivity.class);
                startActivity(intent2);
            }
        });

        btnLambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Lambda.this, Stream.class);
                startActivity(intent3);
            }
        });
        Button startButton = findViewById(R.id.btnStart3);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExecution();
            }
        });

        Button stopButton = findViewById(R.id.btnStop3);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopExecution();
            }
        });


        Toast.makeText(Lambda.this, "Tab Lambda", Toast.LENGTH_LONG).show();

    }
    private void stopExecution() {
        isRunning = false;
    }
    @SuppressLint("SetTextI18n")
    private void startExecution() {
        isRunning = true;

        RadioGroup radioGroupLamba = (RadioGroup) findViewById(R.id.radioGroup3);
        int idCheckedLamba = radioGroupLamba.getCheckedRadioButtonId();
        switch (idCheckedLamba) {
            case R.id.radioButton3:
                new Thread(() -> {
                    // Bắt đầu tính thời gian
                    long startTime = System.currentTimeMillis();

                    while (isRunning) {
                        helper(() -> suml++);
                    }

                    // Kết thúc tính thời gian
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + suml);
                        System.out.println("Thời gian chạy: " + duration + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration + " msec");
                        Toast.makeText(Lambda.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();

                    });
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed3);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;

                        while (isRunning) {
                            while (isRunning) {
                                helper(() -> suml++);
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
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo3);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });

                        }
                    }
                }).start();

            case R.id.radioButton32:
                Runnable action = () -> sum++;

                new Thread(() -> {
                    // Bắt đầu tính thời gian
                    long startTime = System.currentTimeMillis();

                    while (isRunning) {
                        helper(action);
                    }

                    // Kết thúc tính thời gian
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    // Thời gian chạy câu lệnh trong mili giây


                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum);
                        System.out.println("Thời gian chạy: " + duration + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration + " msec");
                        Toast.makeText(Lambda.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();

                    });
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed32);
                    txt1.setText("Elapsed time (msec): " + duration);
                    TextView txtGC = (TextView) findViewById(R.id.txtNoGC3);
                    txtGC.setText("No. of GCs: " + gcCount);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;

                        while (isRunning) {
                            while (isRunning) {
                                helper(action);
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
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo32);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });

                        }
                    }
                }).start();
        }
    }
}
