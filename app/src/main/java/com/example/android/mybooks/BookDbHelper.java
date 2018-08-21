package com.example.android.mybooks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.mybooks.BookContract.BookEntry;

/**
 * Created by prajbhanda on 7/28/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    //datanase schema and version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bookstore.db";

    //string for queries
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" + BookEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," + BookEntry.COLUMN_BOOK_NAME + TEXT_TYPE + NOT_NULL + "," + BookEntry.COLUMN_BOOK_PRICE + INTEGER_TYPE + " NOT NULL DEFAULT 0," + BookEntry.COLUMN_BOOK_QUANTITY + INTEGER_TYPE + NOT_NULL + " DEFAULT 0," + BookEntry.COLUMN_SUPPLIER_NAME + TEXT_TYPE + NOT_NULL + "," + BookEntry.COLUMN_SUPPLIER_PHONE + INTEGER_TYPE + NOT_NULL + " DEFAULT 0" + ");";


    public BookDbHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( SQL_CREATE_ENTRIES );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
