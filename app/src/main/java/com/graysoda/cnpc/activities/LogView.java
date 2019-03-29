package com.graysoda.cnpc.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.graysoda.cnpc.R;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogView extends AppCompatActivity {
    private static final Logger logger = Logger.getLogger(LogView.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);

        File log = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "log/cnpc.log");

        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(log));
            String line;

            while ((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.logView_textView)).setText(sb.toString());
    }
}
