package com.vitysoft.android.startadb;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.vitysoft.android.startadb.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAdb();
        finish();
    }

    private void startAdb() {
        if (isAdbStarted()) {
            Log.i("startAdb", "adb 已经启动");
            Toast.makeText(this, "adb 已经启动", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Process rootShell = Runtime.getRuntime().exec("su");
            DataOutputStream out = new DataOutputStream(rootShell.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(rootShell.getInputStream()));
            out.writeBytes("setprop service.adb.tcp.port 5555\n");
            out.writeBytes("stop adbd\n");
            out.writeBytes("start adbd\n");
            out.writeBytes("exit\n");
            out.flush();
            String result;
            do {
                result = input.readLine(); // 需要等待，直接finish()，没生效
                if (result != null) {
                    Log.i("startAdb", result);
                }
            } while (result != null);
            out.close();
            input.close();
            Log.i("startAdb", "adb 启动成功");
            Toast.makeText(this, "adb 启动成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("startAdb", "Failed to start adb.");
            e.printStackTrace();
        }
    }

    private boolean isAdbStarted() {
        boolean started = false;
        try {
            Process process = Runtime.getRuntime().exec("getprop service.adb.tcp.port");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = input.readLine();
            if (result != null && result.equals("5555")) {
                started = true;
            }
            input.close();
        } catch (Exception e) {
            Log.e("startAdb", "Failed to check isAdbStarted.");
            e.printStackTrace();
        }
        return started;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
