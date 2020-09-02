package com.zcl.server;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

public class IBookManagerProxy implements IBookManager {

    IBinder remote;
    private static final String DESCRIPTOR = "com.zcl.server.IBookManager";

    public IBookManagerProxy(IBinder remote) {
        this.remote = remote;
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        List<Book> result;
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            remote.transact(IBookManagerStub.TRANSACTION_getBookList, data, replay, 0);
            replay.readException();
            result = replay.createTypedArrayList(Book.CREATOR);
        } finally {
            replay.recycle();
            data.recycle();
        }

        return result;
    }

    @Override
    public void addBookInOut(Book book) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (book != null) {
                data.writeInt(1);
                book.writeToParcel(data, 0);
            } else {
                data.writeInt(0);
            }
            remote.transact(IBookManagerStub.TRANSACTION_addBookInOut, data, replay, 0);
            replay.readException();
        } finally {
            replay.recycle();
            data.recycle();
        }

    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
