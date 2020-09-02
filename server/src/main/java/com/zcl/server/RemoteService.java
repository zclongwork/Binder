package com.zcl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteService extends Service {

    private final String TAG = "Server";

    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();

//    private CopyOnWriteArrayList<INewBookArrivedListener> listenersList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<INewBookArrivedListener> listenersList = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);


    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestoryed.set(true);
    }

    private void initData() {
        bookList.add(new Book("活着"));
        bookList.add(new Book("设计模式"));
    }

    private final IBookManager.Stub stub = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {
            if (book != null) {
                bookList.add(book);
                onNewBookArrived(book);
            } else {
                Log.e(TAG, "接收到了一个空对象 InOut");
            }
        }

        @Override
        public void registerListener(INewBookArrivedListener listener) throws RemoteException {
            listenersList.register(listener);

        }

        @Override
        public void unregisterListener(INewBookArrivedListener listener) throws RemoteException {
            boolean result = listenersList.unregister(listener);
            Log.d(TAG, "unregisterListener result: " + result);
        }
    };

    private void onNewBookArrived(Book book) throws RemoteException {
        //注意：RemoteCallbackList的beginBroadcast  finishBroadcast 必需要同时使用

        int n = listenersList.beginBroadcast();
        for (int i=0; i< n;i++) {
            INewBookArrivedListener listener = listenersList.getBroadcastItem(i);
            listener.onNewBookArrived(book);
        }
        listenersList.finishBroadcast();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            // do background processing here.....
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                int bookId = bookList.size() + 1;
                Book newBook = new Book("new book by thread");
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
