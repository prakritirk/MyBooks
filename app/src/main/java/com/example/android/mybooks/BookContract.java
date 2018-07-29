package com.example.android.mybooks;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by prajbhanda on 7/28/2018.
 */

public class BookContract {

    //The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = " com.example.android.mybooks";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY );
    public static final String PATH_BOOK = "mybooks";

    private BookContract() {
    }

    /* Inner class that defines the table contents of the pets table */
    public static final class BookEntry implements BaseColumns {


        // Table name
        public static final String TABLE_NAME = "books";

        //column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "product_name";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone_number";


    }

}
