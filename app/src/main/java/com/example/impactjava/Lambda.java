package com.example.impactjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Iterator;

public class Lambda extends AppCompatActivity {

    Button btnIterator, btnForeach, btnLambda, btnStream;
    volatile boolean isRunning = false;
    int sum, suml, sumla;
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

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    private void startExecution() {
        isRunning = true;

        RadioGroup radioGroupLamba = (RadioGroup) findViewById(R.id.radioGroup3);
        int idCheckedLamba = radioGroupLamba.getCheckedRadioButtonId();
        switch (idCheckedLamba) {
            case R.id.radioButton32:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng
                            while (isRunning) {
                                helper(() -> suml++);
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
                        final String gcCountText = "No. of GCs: " + gcCount++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //gcCountTextView.setText(totalTimeText);
                                TextView txtNo = (TextView) findViewById(R.id.txtNoGC32);
                                txtNo.setText(gcCountText);
                            }
                        });
                    }
                }).start();

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
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed32);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;
                        double averageGCTime = 0;

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
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            gcCount++;
                            // Cập nhật thời gian tổng
                            totalTime += (System.currentTimeMillis() - startTime) - gcTime;

                            // Tăng số vòng lặp
                            loopCount++;

                            // Hiển thị kết quả trung bình
                            final String averageText = "" + (double) loopCount / (double) totalTime;
                            final String gcCountText = "No. of GCs: " + gcCount1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo32);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                    //TextView txtNo = (TextView) findViewById(R.id.txtNoGC32);
                                    //txtNo.setText(gcCountText);
                                }
                            });

                        }
                        if (!isRunning) {
                            averageGCTime = (double) totalTime / (double) (gcCount1 -1);

                            double finalAverageGCTime = Double.parseDouble(new DecimalFormat("##.####").format(averageGCTime));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime32);
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
                            while (isRunning) {
                                helper(() -> suml++);
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
                                    TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap32);
                                    txtHeap.setText(heap);
                                }
                            });
                        }
//                        TextView txtHeap = (TextView) findViewById(R.id.txtAvgHeap1);
//                        txtHeap.setText(averageHeapSizeText);
                    }
                }).start();
                //break ;

            case R.id.radioButton3:
                Runnable action = () -> sum++;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int gcCount = 0;
                        long startTime = System.currentTimeMillis();

                        while (isRunning) {
                            // Thực hiện vòng lặp tính tổng

                            while (isRunning) {
                                helper(action);
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
                        final String gcCountText = "No. of GCs: " + gcCount1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //gcCountTextView.setText(totalTimeText);
                                TextView txtNo2 = (TextView) findViewById(R.id.txtNoGC3);
                                txtNo2.setText(gcCountText);
                            }
                        });
                    }
                }).start();

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
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed3);
                    txt1.setText("Elapsed time (msec): " + duration);
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long totalTime = 0;
                        int loopCount = 0;
                        double averageGCTime = 0;
                        int gcCount3 = 0;

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
                            loopCount++;gcCount3++;

                            // Hiển thị kết quả trung bình
                            final String averageText = "" + (double) gcCount3 / (double) totalTime;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgNo12 = (TextView) findViewById(R.id.txtAvgNo3);
                                    txtAvgNo12.setText("Avg. no of loops (per GC): " + averageText);
                                }
                            });

                        }
                        if (!isRunning) {
                            averageGCTime = (double) totalTime / (double) (gcCount3);

                            double finalAverageGCTime = Double.parseDouble(new DecimalFormat("##.####").format(averageGCTime));

                            int finalGcCount = gcCount3;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtAvgTime12 = (TextView) findViewById(R.id.txtAvgTime3);
                                    txtAvgTime12.setText("Avg. time between GCs (msec): " + finalAverageGCTime);
                                    TextView txtNo2 = (TextView) findViewById(R.id.txtNoGC3);
                                    txtNo2.setText("No. of GCs: " + finalGcCount);

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
                            while (isRunning) {
                                helper(action);
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
                                    TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap3);
                                    txtHeap1.setText(heap);
                                }
                            });
                        }
//                        TextView txtHeap1 = (TextView) findViewById(R.id.txtAvgHeap12);
//                        txtHeap1.setText(averageHeapSizeText);
                    }
                }).start();
                //break ;
        }
    }
}
