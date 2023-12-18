package com.example.impactjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class ForeachActivity extends AppCompatActivity {

    Button btnIterator, btnForeach, btnLambda, btnStream;
    // Biến đếm số lượng Runnable
    private int countRunnableNotOptimized = 0;
    private int countRunnableOptimized = 0;
    private volatile boolean isRunning = false;

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
        Button start = findViewById(R.id.btnStart2);
        Button stop = findViewById(R.id.btnStop2);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = true ;
//                listNotOptimized();
                runCodeForTenSeconds();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false ;
                runCodeForTenSecondsOp();
            }
        });


        Toast.makeText(ForeachActivity.this, "Tab Foreach", Toast.LENGTH_LONG).show();
    }
    private void runCodeForTenSeconds() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (isRunning && (System.currentTimeMillis() - startTime) < 10000) {
                listNotOptimized();
                try {
                    Thread.sleep(1000); // Đợi 1 giây trước mỗi lần chạy
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void runCodeForTenSecondsOp() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (!isRunning && (System.currentTimeMillis() - startTime) < 10000) {
                listOptimized();
                try {
                    Thread.sleep(1000); // Đợi 1 giây trước mỗi lần chạy
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // Code không tối ưu
    private void listNotOptimized() {
        List<String> list = Arrays.asList("a", "b", "c");
        list.forEach(s ->{
            System.out.println(s);
            countRunnableNotOptimized++ ;
        } );

    }

    // Code tối ưu
    private void listOptimized() {

        List<String> list = Arrays.asList("a", "b", "c");
        for (String s : list) {
            System.out.println(s);
            countRunnableOptimized++ ;
        }
    }
}
