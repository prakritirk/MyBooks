package com.example.android.mybooks;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.mybooks.BookContract.BookEntry;
import com.example.android.mybooks.data.BookCursorAdapter;

/**
 * Created by prajbhanda on 7/28/2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;
    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;
    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;
    /**
     * EditText field to enter the book's quantitys
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
    private int currentQuantity;
    private boolean mBookHasChanged = false;
    private Uri mCurrentBookUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editor );

        //Use getIntent() and getData() to get the associated URI
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //if the itent does not contain a book content URI then we know that we are creating a new book
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a book"
            setTitle( R.string.editor_activity_title_add_book );
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            //Otherwise this is an existing book, so change app bar to say "Edit Pet"
            setTitle( R.string.editor_activity_title_edit_book );
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader( EXISTING_BOOK_LOADER, null, this );
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById( R.id.edit_book_name );
        mPriceEditText = (EditText) findViewById( R.id.edit_book_price );
        mQuantityEditText = (EditText) findViewById( R.id.edit_book_quantity );
        mSupplierNameEditText = (EditText) findViewById( R.id.edit_supplier_name );
        mSupplierNumEditText = (EditText) findViewById( R.id.edit_supplier_number );

        //Increase Quantity Button
        ImageButton mUpQuantity = findViewById( R.id.add_button );
        ImageButton mDownQuantity = findViewById( R.id.minus_button );
        ImageButton mCallSupplier = findViewById( R.id.call_button );


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener( mTouchListener );
        mPriceEditText.setOnTouchListener( mTouchListener );
        mQuantityEditText.setOnTouchListener( mTouchListener );
        mSupplierNameEditText.setOnTouchListener( mTouchListener );
        mSupplierNumEditText.setOnTouchListener( mTouchListener );
        mUpQuantity.setOnTouchListener( mTouchListener );
        mDownQuantity.setOnTouchListener( mTouchListener );

        //set on click listener on quantity up button
        mUpQuantity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = mQuantityEditText.getText().toString();
                if (!TextUtils.isEmpty( quantity )) {
                    currentQuantity = Integer.parseInt( quantity );
                    mQuantityEditText.setText( String.valueOf( currentQuantity + 1 ) );
                } else {
                    Toast.makeText( EditorActivity.this, getString( R.string.enter_quantity ), Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        //set on click listener on quantity down button
        mDownQuantity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = mQuantityEditText.getText().toString();
                if (!TextUtils.isEmpty( quantity )) {
                    currentQuantity = Integer.parseInt( quantity );
                    if (currentQuantity > 0) {
                        mQuantityEditText.setText( String.valueOf( currentQuantity - 1 ) );
                    } else {
                        Toast.makeText( EditorActivity.this, getString( R.string.quantity_not_negative ), Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( EditorActivity.this, getString( R.string.enter_quantity ), Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        //set on click listener on call button send intent to native call app
        mCallSupplier.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierNum = mSupplierNumEditText.getText().toString().trim();
                if (!TextUtils.isEmpty( supplierNum )) {
                    Intent callIntent = new Intent( Intent.ACTION_DIAL );
                    callIntent.setData( Uri.parse( "tel:" + supplierNum ) );
                    if (callIntent.resolveActivity( getPackageManager() ) != null) {
                        startActivity( callIntent );
                    }
                } else {
                    Toast.makeText( EditorActivity.this, R.string.enter_phone_num, Toast.LENGTH_LONG ).show();
                }


            }
        } );

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu( menu );
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem( R.id.action_delete );
            menuItem.setVisible( false );
        }
        return true;
    }

    //set onclick listener on quantity up button

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.unsaved_changes_dialog_msg );
        builder.setPositiveButton( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton( R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        } );

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.delete_dialog_msg );
        builder.setPositiveButton( R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        } );
        builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        } );

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete( mCurrentBookUri, null, null );
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText( this, getString( R.string.editor_delete_book_failed ), Toast.LENGTH_SHORT ).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText( this, getString( R.string.editor_delete_book_successful ), Toast.LENGTH_SHORT ).show();
            }
            finish();


        }

    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog( discardButtonClickListener );
    }

    private void saveBook() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierNum = mSupplierNumEditText.getText().toString().trim();

        if (mCurrentBookUri == null && TextUtils.isEmpty( nameString ) && TextUtils.isEmpty( priceString ) && TextUtils.isEmpty( quantityString ) && TextUtils.isEmpty( supplierName ) && TextUtils.isEmpty( supplierNum )) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }


        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty( nameString )) {
            mNameEditText.setError( getString( R.string.edit_book_name_empty ) );
            Toast.makeText( EditorActivity.this, R.string.edit_book_name_empty, Toast.LENGTH_LONG ).show();
            return;
        }
        if (TextUtils.isEmpty( supplierName )) {
            mSupplierNameEditText.setError( getString( R.string.edit_supplier_name_empty ) );
            Toast.makeText( EditorActivity.this, R.string.edit_supplier_name_empty, Toast.LENGTH_LONG ).show();
            return;
        }

        int quantity = 0;
        if (!TextUtils.isEmpty( quantityString )) {
            quantity = Integer.parseInt( quantityString );
        }
        values.put( BookEntry.COLUMN_BOOK_QUANTITY, quantity );

        int price = 0;
        if (!TextUtils.isEmpty( priceString )) {
            price = Integer.parseInt( priceString );
        }
        values.put( BookEntry.COLUMN_BOOK_PRICE, price );

        int phone = 0;
        if (!TextUtils.isEmpty( supplierNum )) {
            phone = Integer.parseInt( supplierNum );
        }
        values.put( BookEntry.COLUMN_SUPPLIER_PHONE, phone );
        values.put( BookEntry.COLUMN_SUPPLIER_NAME, supplierName );
        values.put( BookEntry.COLUMN_BOOK_NAME, nameString );


        // Determine if this is a new or existing book by checking if mCurrentPetUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert( BookEntry.CONTENT_URI, values );

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText( this, getString( R.string.editor_insert_book_failed ), Toast.LENGTH_SHORT ).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText( this, getString( R.string.editor_insert_book_successful ), Toast.LENGTH_SHORT ).show();
                finish();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update( mCurrentBookUri, values, null, null );

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText( this, getString( R.string.editor_update_book_failed ), Toast.LENGTH_SHORT ).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText( this, getString( R.string.editor_update_book_successful ), Toast.LENGTH_SHORT ).show();
            }
            finish();
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
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask( EditorActivity.this );
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask( EditorActivity.this );
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog( discardButtonClickListener );
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_PHONE,};

        //THIS loader will execute the conten provider's query method on a background thread
        return new CursorLoader( this,//parent activity context
                mCurrentBookUri,//Provider conent URI to query
                projection,//Columns to include in the resulting cursor
                null,//no selection cause
                null,//no selection args
                null );//no sort return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)

        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_NAME );
            int priceColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_QUANTITY );
            int supplierNameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_SUPPLIER_NAME );
            int supplierNumColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_SUPPLIER_PHONE );
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString( nameColumnIndex );
            int price = cursor.getInt( priceColumnIndex );
            int quantity = cursor.getInt( quantityColumnIndex );
            String supplierName = cursor.getString( supplierNameColumnIndex );
            int supplierNum = cursor.getInt( supplierNumColumnIndex );

            // Update the views on the screen with the values from the database
            mNameEditText.setText( name );
            mPriceEditText.setText( Integer.toString( price ) );
            mQuantityEditText.setText( Integer.toString( quantity ) );
            mSupplierNameEditText.setText( supplierName );
            mSupplierNumEditText.setText( Integer.toString( supplierNum ) );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText( "" );
        mPriceEditText.setText( "" );
        mQuantityEditText.setText( "" );
        mSupplierNameEditText.setText( "" );
        mSupplierNumEditText.setText( "" );
    }
}