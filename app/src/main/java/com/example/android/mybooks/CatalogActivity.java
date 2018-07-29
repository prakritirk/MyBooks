package com.example.android.mybooks;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.mybooks.BookContract.BookEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_catalog );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( CatalogActivity.this, EditorActivity.class );
                startActivity( intent );
            }
        } );

        mDbHelper = new BookDbHelper( this );
    }

    //to display after book is added on editor activity
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the book database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        BookDbHelper mDbHelper = new BookDbHelper( this );

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.

        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_PHONE};
        Cursor cursor = db.query( BookEntry.TABLE_NAME, projection, null, null, null, null, null );

        TextView displayView = (TextView) findViewById( R.id.text_view_book );
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).

            displayView.setText( "The books table contains: " + cursor.getCount() + " books.\n\n" );
            displayView.append( BookEntry._ID + "-" + BookEntry.COLUMN_BOOK_NAME + "-" + BookEntry.COLUMN_BOOK_PRICE + "-" + BookEntry.COLUMN_BOOK_QUANTITY + "-" + BookEntry.COLUMN_SUPPLIER_NAME + "-" + BookEntry.COLUMN_SUPPLIER_PHONE + "\n" );

            //find index of each column
            int idColumnIndex = cursor.getColumnIndex( BookEntry._ID );
            int bookNameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_NAME );
            int bookPriceColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_QUANTITY );
            int supplierNameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_SUPPLIER_NAME );
            int supplierNumColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_SUPPLIER_PHONE );

            //iterate through each row in the cursor

            while (cursor.moveToNext()) {

                int currentId = cursor.getInt( idColumnIndex );
                String currentName = cursor.getString( bookNameColumnIndex );
                int currentPrice = cursor.getInt( bookPriceColumnIndex );
                int currentQuantity = cursor.getInt( quantityColumnIndex );
                String currentSupplier = cursor.getString( supplierNameColumnIndex );
                String currentPhone = cursor.getString( supplierNumColumnIndex );


                displayView.append( ("\n" + currentId + "\t" + currentName + "\t" + currentPrice + "\t" + currentQuantity + "\t" + currentSupplier + "\t" + currentPhone) );


            }


        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_catalog, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    private void insertBook() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( BookEntry.COLUMN_BOOK_NAME, "Kite Runner" );
        values.put( BookEntry.COLUMN_BOOK_PRICE, 10 );
        values.put( BookEntry.COLUMN_BOOK_QUANTITY, 2 );
        values.put( BookEntry.COLUMN_SUPPLIER_NAME, "PKT Suppliers" );
        values.put( BookEntry.COLUMN_SUPPLIER_PHONE, "77115571" );

        long newRowId = db.insert( BookEntry.TABLE_NAME, null, values );
        Log.v( "CatalogActivity", "New row ID" + newRowId );

    }
}