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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int sum1, sum2 = 1000;
    private volatile boolean isRunning = false;

    private boolean calculating;
    private int count;

    private long totalTime;
    private long totalGcTime;

    private Handler handler1;
    private Runnable runnable1;

    private int loopCount;
    private double totalGCRatio;
    private Handler handler;

    Button btnIterator, btnForeach, btnLambda, btnStream;

//    private WeakReference<GarbageCollectionWatcher> gcWatcher
//            = new WeakReference<>(new GarbageCollectionWatcher());

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
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

        handler = new Handler();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                calculateAverage();
                //handler.postDelayed(this, 1000); // Chạy lại sau mỗi giây
            }
        };
    }

    private void calculateAverage() {
        long startTime = System.currentTimeMillis();

        int sum = 0;
        for (int i = 0; i < 1000; i++) { // Điều chỉnh loop theo nhu cầu
            sum += i;
        }

        long gcTime = Debug.getGlobalGcInvocationCount() - count;

        totalTime += System.currentTimeMillis() - startTime;
        totalGcTime += gcTime;

        count += gcTime;

        double averageGcTime = (double) totalGcTime / (double) count;

        if (count > 0) {
            double averageHeapSize = ((double) totalGcTime * 1024) / (double) count;
            TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap1);
            txtHeap.setText(String.format("Avg. heap size (per GC, msec): %.2f", averageHeapSize));
            //resultTextView.setText(String.format("Trung bình kích thước heap (mỗi GC, msec): %.2f", averageHeapSize));
        }
    }

    private void stopExecution() {
        isRunning = false;

        calculating = false;
        handler.removeCallbacks(runnable1);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    private void startExecution() {
        isRunning = true;

        calculating = true;
        count = 0;
        totalTime = 0;
        totalGcTime = 0;

        handler.post(runnable1);

        handler = new Handler();

        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(i);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int idChecked = radioGroup.getCheckedRadioButtonId();
        switch (idChecked) {
            case R.id.radioButton1:
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
                                TextView txtNo = (TextView) findViewById(R.id.txtNoGC1);
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
                        int gcCount = 0 ;
                        double averageGCTime = 0;

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
                            gcCount ++ ;

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
                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double)gcCount;

                            double finalAverageGCTime = averageGCTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime1);
                                    txtAvgTime12.setText("Avg. time between GCs (msec): " + finalAverageGCTime);
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            long sum = 0;
                            Iterator<Integer> iterator = values.iterator();

                            while (iterator.hasNext()) {
                                sum += iterator.next();
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
                                TextView txtNo2 = (TextView) findViewById(R.id.txtNoGC12);
                                txtNo2.setText(gcCountText);
                            }
                        });
                    }
                }).start();

                new Thread(() -> {
                    long startTime = System.currentTimeMillis();

                    Iterator<Integer> iterator = values.iterator();
                    while (isRunning) {
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
                        int numOfGC = 0  ;
                        double averageGCTime =0 ;

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
                            numOfGC++ ;
                            // Tăng số vòng lặp
                            loopCount++;

                            // Hiển thị kết quả trung bình
                             averageGCTime = (double) totalTime /(double)  numOfGC;

                            final String averageText = "" + (double) loopCount / (double) totalTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo12);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);


                                }
                            });

                        }
                        if (!isRunning){
                            averageGCTime = (double) totalTime / (double) numOfGC;

                            double finalAverageGCTime = averageGCTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo12);
//                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);

                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime12);
                                    txtAvgTime12.setText("Avg. time between GCs (msec): " + finalAverageGCTime);
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

//     void stopExecution() {
//        isRunning = false;
//    }
//
//     void startExecution() {
//        isRunning = true;
//    }
//
//    private class GarbageCollectionWatcher {
//        protected void finalize() throws Throwable {
//            runOnUiThread(() -> {
//                if (gcWatcher != null) {
//                    System.out.println("gcWatcher");
//
//                    System.out.println(gcWatcher);
//                    Log.d("gc", gcWatcher.toString());
//
//                    gcCount ++ ;
//                }
//            });
//
//            // Create a new instance for the next garbage collection
//            gcWatcher = new WeakReference<>(new GarbageCollectionWatcher());
//        }
//
//        void collectMemoryUsageInformation() {
//            // Thu thập thông tin về việc sử dụng bộ nhớ ở đây
//            Runtime runtime = Runtime.getRuntime();
//            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
//            System.out.println("Used Memory: " + usedMemory + " bytes");
//        }


}