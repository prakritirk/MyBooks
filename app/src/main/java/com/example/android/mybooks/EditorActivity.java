package com.example.android.mybooks;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.mybooks.BookContract.BookEntry;

/**
 * Created by prajbhanda on 7/28/2018.
 */

public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the book's supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the book's supplier number
     */
    private EditText mSupplierNumEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editor );

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById( R.id.edit_book_name );
        mPriceEditText = (EditText) findViewById( R.id.edit_book_price );
        mQuantityEditText = (EditText) findViewById( R.id.edit_book_quantity );
        mSupplierNameEditText = (EditText) findViewById( R.id.edit_supplier_name );
        mSupplierNumEditText = (EditText) findViewById( R.id.edit_supplier_number );

    }

    private void insertBook() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int price = Integer.parseInt( priceString );
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt( quantityString );
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierNum = mSupplierNumEditText.getText().toString().trim();

        BookDbHelper mDbHelper = new BookDbHelper( this );
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( BookEntry.COLUMN_BOOK_NAME, nameString );
        values.put( BookEntry.COLUMN_BOOK_PRICE, price );
        values.put( BookEntry.COLUMN_BOOK_QUANTITY, quantity );
        values.put( BookEntry.COLUMN_SUPPLIER_NAME, supplierName );
        values.put( BookEntry.COLUMN_SUPPLIER_PHONE, supplierNum );

        long newRowId = db.insert( BookEntry.TABLE_NAME, null, values );
        if (newRowId == -1) {
            Toast.makeText( this, "Error with saving book", Toast.LENGTH_SHORT ).show();

        } else {
            Toast.makeText( this, "Book saved with row id:" + newRowId, Toast.LENGTH_SHORT ).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_editor, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask( this );
                return true;
        }
        return super.onOptionsItemSelected( item );
    }
}