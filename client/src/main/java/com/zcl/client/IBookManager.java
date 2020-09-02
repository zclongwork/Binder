package com.zcl.client;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IBookManager extends IInterface {

    public List<Book> getBookList() throws RemoteException;

    public void addBookInOut(Book book) throws RemoteException;
}
