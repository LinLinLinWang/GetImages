package com.example.getimages;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.UtilsTool.Permissions;
import com.example.getimages.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Button startService ;
    private Intent intent;
    private String ip;
    private EditText editText;



    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            建立连接
//            获取服务操作对象
            MyService.MyBinder binder = (MyService.MyBinder) service;
            binder.getService();//获取到Service



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


            //绑定服务
        intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection,  Context.BIND_AUTO_CREATE);
        startService=  (Button)findViewById(R.id.button_first);
        editText= (EditText) findViewById(R.id.ip);
        editText.setEnabled(true);
        class MyButtonlistener implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                  Toast.makeText(MainActivity.this, "启动服务中。。", Toast.LENGTH_LONG).show();
               // TextInputEditText textview_first = (TextInputEditText)findViewById(R.id.)
                // 开启后台服务、避免查杀
                intent.putExtra("ip",editText.getText().toString());

                startService(intent);// 启动服务
                editText.setEnabled(false);
                Permissions  permissions = new Permissions();
                boolean Permissions = permissions.RequestPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(Permissions){
                    Toast.makeText(MainActivity.this,"有这个权限",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"无这个权限，发起弹窗请求",Toast.LENGTH_LONG).show();
                }

            }
        };
        MyButtonlistener listener = new MyButtonlistener();
        startService.setOnClickListener(listener);

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

//检查权限





}