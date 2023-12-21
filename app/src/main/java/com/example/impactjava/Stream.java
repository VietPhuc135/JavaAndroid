package com.example.impactjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stream extends AppCompatActivity {

    int sum1, sum2 = 1000;
    private volatile boolean isRunning = false;

    private int loopCount;
    private double totalGCRatio;
    private Handler handler;

    int gcCount1 = 0 ;
    private WeakReference<GarbageCollectionWatcher> gcWatcher
            = new WeakReference<>(new GarbageCollectionWatcher());

    private class GarbageCollectionWatcher {
        protected void finalize() {
            gcWatcher = new WeakReference<>(new GarbageCollectionWatcher());
            gcCount1 ++ ;
            System.out.println("dem số " + gcCount1);
        }
    }

    Button btnIterator, btnForeach, btnLambda, btnStream;

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        btnIterator = findViewById(R.id.btnIterator);
        btnForeach = findViewById(R.id.btnForeach);
        btnLambda = findViewById(R.id.btnLambda);
        btnStream = findViewById(R.id.btnStream);

        btnIterator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Stream.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        btnForeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Stream.this, ForeachActivity.class);
                startActivity(intent2);
            }
        });

        btnLambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Stream.this, Lambda.class);
                startActivity(intent3);
            }
        });

        btnStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button startButton = findViewById(R.id.btnStart4);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExecution();
            }
        });

        Button stopButton = findViewById(R.id.btnStop4);
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

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    private void startExecution() {
        isRunning = true;

        handler = new Handler();

        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(i);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup4);
        int idChecked = radioGroup.getCheckedRadioButtonId();
        switch (idChecked) {
            case R.id.radioButton4:
                loopCount = 0;
                totalGCRatio = 0;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            for (int x: values) {
                                if (x > 0) {
                                    sum += (x + 1);
                                }
                            }

                            // Gọi GC và tăng số lần gọi GC
                            System.gc();
                            gcCount++;
                        }

                        // Tính thời gian thực hiện
                        long endTime = System.currentTimeMillis();
                        final String totalTimeText = "Total time: " + (endTime - startTime) + " ms";
                        final String gcCountText = "No. of GCs: " + gcCount1;
                        gcCount1 = 0;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //gcCountTextView.setText(totalTimeText);
                                TextView txtNo = (TextView) findViewById(R.id.txtNoGC4);
                                txtNo.setText(gcCountText);
                            }
                        });
                    }
                }).start();

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    long sum = 0;

                    while (isRunning) {
                        for (int x: values) {
                            if (x > 0) {
                                sum += (x + 1);
                            }
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum1);
                        System.out.println("Thời gian chạy: " + duration + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration + " msec");
                    });

                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed4);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long totalTime = 0;
                        double averageGCTime = 0;
                        long startTime = System.currentTimeMillis();
                        int totalLoopCount = 0;
                        int gcCount = 0;

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            for (int x: values) {
                                if (x > 0) {
                                    sum += (x + 1);
                                }
                            }

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Cập nhật thời gian tổng
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;
                            // Tăng tổng số vòng lặp và số lần GC
                            totalLoopCount++;
                            gcCount++;

                            // Hiển thị kết quả trung bình
                            String averageText = "" + (double) totalLoopCount / gcCount;

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    TextView txtAvgNo1 = (TextView) findViewById(R.id.txtAvgNo4);
//                                    txtAvgNo1.setText("Avg. no of loops (per GC): " + averageText);
//                                }
//                            });
                        }

                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double)gcCount;

                            double finalAverageGCTime = Double.parseDouble(new DecimalFormat("##.####").format(averageGCTime));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime4);
                                    txtAvgTime12.setText("Avg. time between GCs (msec): " + finalAverageGCTime);
                                }
                            });
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int gcCount = 0;
                        String averageHeapSizeText = "";

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            for (int x: values) {
                                if (x > 0) {
                                    sum += (x + 1);
                                }
                            }

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Lấy thông tin về kích thước heap
                            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
                            Debug.getMemoryInfo(memoryInfo);

                            // Cập nhật thời gian tổng và số lần GC
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;
                            gcCount++;

                            // Tính trung bình kích thước heap mỗi GC
                            averageHeapSizeText = "Avg. heap size (per GC, msec): " +
                                    (double) memoryInfo.getTotalPss() / gcCount;

                        }

                        if (!isRunning){
                            String heap = averageHeapSizeText;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap4);
                                    txtHeap.setText(heap);
                                }
                            });
                        }
                    }
                }).start();
                break;

            case R.id.radioButton42:
                loopCount = 0;
                totalGCRatio = 0;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            sum = values.stream()
                                    .filter(x -> x > 0)
                                    .mapToInt(x -> x + 1)
                                    .sum();

                            // Gọi GC và tăng số lần gọi GC
                            System.gc();
                            gcCount++;

                        }

                        // Tính thời gian thực hiện
                        long endTime = System.currentTimeMillis();
                        final String totalTimeText = "Total time: " + (endTime - startTime) + " ms";
                        final String gcCountText = "No. of GCs: " + gcCount1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtNo2 = (TextView) findViewById(R.id.txtNoGC42);
                                txtNo2.setText(gcCountText);
                            }
                        });
                    }
                }).start();

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    while (isRunning) {
                        long sum = 0;
                        sum = values.stream()
                                .filter(x -> x > 0)
                                .mapToInt(x -> x + 1)
                                .sum();
                    }

                    long endTime = System.currentTimeMillis();
                    long duration1 = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum2);
                        System.out.println("Thời gian chạy: " + duration1 + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration1 + " msec");
                    });

                    TextView txt2 = (TextView) findViewById(R.id.txtElapsed42);
                    txt2.setText("Elapsed time (msec): " + duration1);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;
                        int numOfGC = 0  ;
                        double averageGCTime =0 ;

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            sum = values.stream()
                                    .filter(x -> x > 0)
                                    .mapToInt(x -> x + 1)
                                    .sum();

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Cập nhật thời gian tổng
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;
                            numOfGC++ ;
                            // Tăng số vòng lặp
                            loopCount++;

                            // Hiển thị kết quả trung bình
                            averageGCTime = (double) totalTime /(double)  numOfGC;

                            String averageText = "" + (double) loopCount / (double) totalTime;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo42);
//                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
//                                }
//                            });
                        }

                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double) numOfGC;

                            double finalAverageGCTime = Double.parseDouble(new DecimalFormat("##.####").format(averageGCTime));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime42);
                                    txtAvgTime12.setText("Avg. time between GCs (msec): " + finalAverageGCTime);
                                }
                            });
                        }

                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int gcCount = 0;
                        String averageHeapSizeText = "";

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            sum = values.stream()
                                    .filter(x -> x > 0)
                                    .mapToInt(x -> x + 1)
                                    .sum();

                            // Gọi GC và đo thời gian
                            System.gc();
                            long gcStartTime = System.currentTimeMillis();
                            System.runFinalization();
                            long gcEndTime = System.currentTimeMillis();
                            long gcTime = gcEndTime - gcStartTime;

                            // Lấy thông tin về kích thước heap
                            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
                            Debug.getMemoryInfo(memoryInfo);

                            // Cập nhật thời gian tổng và số lần GC
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;
                            gcCount++;

                            // Tính trung bình kích thước heap mỗi GC
                            averageHeapSizeText = "Avg. heap size (per GC, msec): " +
                                    (double) memoryInfo.getTotalPss() / gcCount;
                        }
                        if (!isRunning){
                            String heap = averageHeapSizeText;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap42);
                                    txtHeap1.setText(heap);
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }
}
