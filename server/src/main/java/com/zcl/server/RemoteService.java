package com.zcl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteService extends Service {


    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        bookList.add(new Book("活着"));
        bookList.add(new Book("设计模式"));
    }


    IBookManagerStub stub = new IBookManagerStub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {
            if (book != null) {
                bookList.add(book);
            } else {
                Log.e(TAG.N, "接收到了一个空book对象");
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG.N, "onBind stub: " + stub.toString());
        return stub;
    }
}
