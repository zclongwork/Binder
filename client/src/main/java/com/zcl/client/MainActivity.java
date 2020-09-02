package com.zcl.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "Client";
    IBookManager iBookManager;
    private boolean connected;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_list).setOnClickListener(this);
        findViewById(R.id.bt_add).setOnClickListener(this);
        findViewById(R.id.bt_bind).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            unbindService(serviceConnection);
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.zcl.server");
        intent.setAction("com.zcl.server.aidl");
        boolean result = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = new IBookManagerProxy(service);

            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };



    int i;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_bind:
                bindService();
                break;
            case R.id.bt_list:
                if (connected) {
                    try {
                        bookList = iBookManager.getBookList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    log();
                } else {
                    Log.d(TAG, "not connected");
                }
                break;
            case R.id.bt_add:
                if (connected) {
                    Book book = new Book("这是client新书 " + i);
                    i++;
                    try {
                        iBookManager.addBookInOut(book);
                        Log.d(TAG, "client向服务器添加了一本新书: " + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void log() {
        for (Book book : bookList) {
            Log.d(TAG, book.toString());
        }
    }
}
