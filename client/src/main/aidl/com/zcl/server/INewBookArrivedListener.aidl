// INewBookArrivedListener.aidl
package com.zcl.server;
import com.zcl.server.Book;
// Declare any non-default types here with import statements

interface INewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in Book newBook);
}
