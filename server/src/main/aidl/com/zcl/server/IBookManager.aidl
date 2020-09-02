// IBookManager.aidl
package com.zcl.server;

import com.zcl.server.Book;
import com.zcl.server.INewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();

    void addBookInOut(inout Book book);

    void registerListener(INewBookArrivedListener listener);

    void unregisterListener(INewBookArrivedListener listener);
}
