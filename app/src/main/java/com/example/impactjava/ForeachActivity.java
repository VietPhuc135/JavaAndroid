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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ForeachActivity extends AppCompatActivity {

    Button btnIterator, btnForeach, btnLambda, btnStream;
    // Biến đếm số lượng Runnable
    private int countRunnableNotOptimized = 0;
    private int countRunnableOptimized = 0;
    private volatile boolean isRunning = false;

    int sum1, sum2 = 1000;
    private int loopCount;
    private double totalGCRatio;
    private Handler handler;

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreach);

        btnIterator = findViewById(R.id.btnIterator);
        btnForeach = findViewById(R.id.btnForeach);
        btnLambda = findViewById(R.id.btnLambda);
        btnStream = findViewById(R.id.btnStream);

        btnIterator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ForeachActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        btnForeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnLambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(ForeachActivity.this, Lambda.class);
                startActivity(intent2);
            }
        });

        btnStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(ForeachActivity.this, Stream.class);
                startActivity(intent3);
            }
        });

        Button startButton = findViewById(R.id.btnStart2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExecution();
            }
        });

        Button stopButton = findViewById(R.id.btnStop2);
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

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        int idChecked = radioGroup.getCheckedRadioButtonId();
        switch (idChecked) {
            case R.id.radioButton2:
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
                            for (int i = 0; i < values.size(); i++) {
                                sum += values.get(i);
                            }

                            // Gọi GC và tăng số lần gọi GC
                            System.gc();
                            gcCount++;

                            // Hiển thị số lần gọi GC
                            /*final String gcCountText = "GC count: " + gcCount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gcCountTextView.setText(gcCountText);
                                }
                            });*/
                        }

                        // Tính thời gian thực hiện
                        long endTime = System.currentTimeMillis();
                        final String totalTimeText = "Total time: " + (endTime - startTime) + " ms";
                        final String gcCountText = "No. of GCs: " + gcCount;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //gcCountTextView.setText(totalTimeText);
                                TextView txtNo = (TextView) findViewById(R.id.txtNoGC2);
                                txtNo.setText(gcCountText);
                            }
                        });
                    }
                }).start();

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    while (isRunning) {
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
                    });

                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed2);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
//                        int loopCount = 0;
//                        int gcCount = 0 ;
                        double averageGCTime = 0;
                        long startTime = System.currentTimeMillis();
                        int totalLoopCount = 0;
                        int gcCount = 0;

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
//                            // Tăng số vòng lặp
//                            loopCount++;
//                            gcCount ++ ;
                            // Tăng tổng số vòng lặp và số lần GC
                            totalLoopCount++;
                            gcCount++;

                            // Hiển thị kết quả trung bình
                            String averageText = "" + (double) totalLoopCount / gcCount;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo1 = (TextView) findViewById(R.id.txtAvgNo2);
                                    txtAvgNo1.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });
                        }
//                        TextView txtAvgNo1 = (TextView) findViewById(R.id.txtAvgNo1);
//                        txtAvgNo1.setText("Avg. no of loops (per GC): " + averageText);

                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double)gcCount;

                            double finalAverageGCTime = averageGCTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime2);
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
                            for (int i = 0; i < 1000; i++) {
                                sum += i;
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
                                    TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap2);
                                    txtHeap.setText(heap);
                                }
                            });
                        }
//                        TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap1);
//                        txtHeap.setText(averageHeapSizeText);
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

            case R.id.radioButton22:
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
                                sum += sum;
                            }

                            // Gọi GC và tăng số lần gọi GC
                            System.gc();
                            gcCount++;

                            // Hiển thị số lần gọi GC
                            /*final String gcCountText = "GC count: " + gcCount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gcCountTextView.setText(gcCountText);
                                }
                            });*/
                        }

                        // Tính thời gian thực hiện
                        long endTime = System.currentTimeMillis();
                        final String totalTimeText = "Total time: " + (endTime - startTime) + " ms";
                        final String gcCountText = "No. of GCs: " + gcCount;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //gcCountTextView.setText(totalTimeText);
                                TextView txtNo2 = (TextView) findViewById(R.id.txtNoGC22);
                                txtNo2.setText(gcCountText);
                            }
                        });
                    }
                }).start();

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    while (isRunning) {
                        long sum = 0;
                        for (int x: values) {
                            sum += sum;
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    long duration1 = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum2);
                        System.out.println("Thời gian chạy: " + duration1 + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration1 + " msec");
                    });

                    TextView txt2 = (TextView) findViewById(R.id.txtElapsed22);
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
//                        String averageText = "";

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            for (int x: values) {
                                sum += sum;
                            }

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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo22);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });
                        }
//                        TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo12);
//                        txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);

                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double) numOfGC;

                            double finalAverageGCTime = averageGCTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo12);
//                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);

                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime22);
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
                                sum += sum;
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
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap12);
//                                    txtHeap1.setText(averageHeapSizeText);
//                                }
//                            });
                        }
                        if (!isRunning){
                            String heap = averageHeapSizeText;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap22);
                                    txtHeap1.setText(heap);
                                }
                            });
                        }
//                        TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap12);
//                        txtHeap1.setText(averageHeapSizeText);
                    }
                }).start();
        }
    }
}
