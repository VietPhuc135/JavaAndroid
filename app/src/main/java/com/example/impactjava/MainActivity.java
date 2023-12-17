package com.example.impactjava;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TabHost tabHost;
    int sum, suml , sum1, sum2 = 1000;
     volatile boolean isRunning = true;
    int gcCount ;

    private WeakReference<GarbageCollectionWatcher> gcWatcher
            = new WeakReference<>(new GarbageCollectionWatcher());

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Ghi lại thời gian trước khi chạy đoạn code câu lệnh for
//        long startTime = SystemClock.elapsedRealtime();
//
//        // Đoạn code câu lệnh for
//        /*for (int i = 0; i < 10; i++) {
//            // Các câu lệnh bạn muốn chạy trong vòng lặp for
//            Log.d("Duration1", "Duration1: " + i + " msec");
//        }*/

//        //List<Integer> values = createIntegerList(1000);
//        //List<Integer> values = Arrays.asList(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);
//
//        /*for (int i = 0; i < values.size(); i++) {
//            sum += values.get(i);
//        }*/
//        Iterator<Integer> iterator = values.iterator();
//        while (iterator.hasNext()) {
//            sum += iterator.next();
//        }
//
//        // Ghi lại thời gian sau khi chạy đoạn code câu lệnh for
//        long endTime = SystemClock.elapsedRealtime();
//
//        // Tính thời gian chạy của đoạn code câu lệnh for
//        long duration = endTime - startTime;
//
//        System.out.println("Tổng: " + sum);
//        System.out.println("Thời gian chạy: " + duration + " msec");
//
//        // Hiển thị thời gian chạy trên Log
//        Log.d("Duration", "Duration: " + duration + " msec");
//        Toast.makeText(MainActivity.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();

        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(i);
        }

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

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int idChecked = radioGroup.getCheckedRadioButtonId();
        switch (idChecked){
            case R.id.radioButton1:
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

            case R.id.radioButton12:
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

        }


        RadioGroup radioGroupLamba = (RadioGroup) findViewById(R.id.radioGroup3);
        int idCheckedLamba = radioGroupLamba.getCheckedRadioButtonId();
        switch (idCheckedLamba){
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
                        Toast.makeText(MainActivity.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();
                        if (!isRunning) {
                            gcWatcher.get().collectMemoryUsageInformation();
                        }
                    });
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed3);
                    txt1.setText("Elapsed time (msec): " + duration);
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
                    long duration = endTime - startTime; // Thời gian chạy câu lệnh trong mili giây

                    runOnUiThread(() -> {
                        System.out.println("Tổng: " + sum);
                        System.out.println("Thời gian chạy: " + duration + " msec");

                        // Hiển thị thời gian chạy trên Log
                        Log.d("Duration", "Duration: " + duration + " msec");
                        Toast.makeText(MainActivity.this, "Duration: " + duration + " msec", Toast.LENGTH_LONG).show();
                        if (!isRunning) {
                            gcWatcher.get().collectMemoryUsageInformation();
                        }
                    });
                    TextView txt1 = (TextView) findViewById(R.id.txtElapsed32);
                    txt1.setText("Elapsed time (msec): " + duration);
                    TextView txtGC = (TextView) findViewById(R.id.txtNoGC3);
                    txtGC.setText("No. of GCs:: " + gcCount);
                }).start();
        }
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1, spec2, spec3, spec4;
        // Tab 1
        spec1 = tabHost.newTabSpec("Tab One");
        spec1.setContent(R.id.Iterator);
        spec1.setIndicator("Iterator");
        tabHost.addTab(spec1);

        // Tab 2
        spec2 = tabHost.newTabSpec("Tab Two");
        spec2.setContent(R.id.Foreach);
        spec2.setIndicator("Foreach");
        tabHost.addTab(spec2);

        // Tab 3
        spec3 = tabHost.newTabSpec("Tab Three");
        spec3.setContent(R.id.Lambda);
        spec3.setIndicator("Lambda");
        tabHost.addTab(spec3);

        // Tab 4
        spec4 = tabHost.newTabSpec("Tab Three");
        spec4.setContent(R.id.Stream);
        spec4.setIndicator("Stream");
        tabHost.addTab(spec4);

        // Chuyển đổi giữa các tab
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // Lưu trữ tab hiện tại
                int selectedTab = tabHost.getCurrentTab();

                // Xử lý logic khi chuyển đổi giữa các tab
                switch (selectedTab) {
                    case 0:
                        // Chọn Tab 1
                        break;
                    case 1:
                        // Chọn Tab 2
                        break;
                    case 2:
                        // Chọn Tab 3
                        break;
                }
            }
        });

        // Mặc định hiển thị Tab 1
        tabHost.setCurrentTab(0);

    }
    void helper(Runnable action) {
        action.run();
    }
     void stopExecution() {
        isRunning = false;
    }

     void startExecution() {
        isRunning = true;
    }

    private class GarbageCollectionWatcher {
        protected void finalize() throws Throwable {
            runOnUiThread(() -> {
                if (gcWatcher != null) {
                    System.out.println("gcWatcher");

                    System.out.println(gcWatcher);
                    Log.d("gc", gcWatcher.toString());

                    gcCount ++ ;
                }
            });

            // Create a new instance for the next garbage collection
            gcWatcher = new WeakReference<>(new GarbageCollectionWatcher());
        }

        void collectMemoryUsageInformation() {
            // Thu thập thông tin về việc sử dụng bộ nhớ ở đây
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used Memory: " + usedMemory + " bytes");
        }
    }
}