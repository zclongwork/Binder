package com.zcl.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 继承自binder实现了IBookManager的方法，说明它可以跨进程传输，并可进行服务端相关的数据操作
 */
public abstract class IBookManagerStub extends Binder implements IBookManager {

    public static final String DESCRIPTOR = "com.zcl.server.IBookManager";

    public IBookManagerStub() {
        this.attachInterface(this, DESCRIPTOR);
    }

    public static IBookManager asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }
        // 问题1: binder.queryLocalInterface的原理是什么 如何实现查询的？

        IInterface iin = binder.queryLocalInterface(DESCRIPTOR);//通过DESCRIPTOR查询本地binder，如果存在则说明调用方和service在同一进程间，直接本地调用
        if ((iin != null) && (iin instanceof IBookManager)) {
            Log.d(TAG.N, "本地调用 直接返回 RemoteService onBind 的binder : " + iin.toString());
            return (IBookManager) iin;
        }
        Log.d(TAG.N, "IPC 调用 IBookManagerProxy");
        return new IBookManagerProxy(binder);//本地没有，返回一个远程代理对象
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_getBookList: {
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBookList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            }
            case TRANSACTION_addBookInOut: {
                data.enforceInterface(DESCRIPTOR);
                Book book;
                if ((0 != data.readInt())) {
                    book = Book.CREATOR.createFromParcel(data);
                } else {
                    book = null;
                }
                this.addBookInOut(book);
                reply.writeNoException();
                if ((book != null)) {
                    reply.writeInt(1);
                    book.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                } else {
                    reply.writeInt(0);
                }
                return true;
            }

            default: {
                return super.onTransact(code, data, reply, flags);
            }
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static final int TRANSACTION_getBookList = android.os.IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSACTION_addBookInOut = android.os.IBinder.FIRST_CALL_TRANSACTION + 1;
}
