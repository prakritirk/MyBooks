package com.example.android.mybooks;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.mybooks.BookContract.BookEntry;
import com.example.android.mybooks.data.BookCursorAdapter;


/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;


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


        //Find the ListView which will be populated with the book data
        ListView bookListView = (ListView) findViewById( R.id.list );

        //Find and set empty view on the ListView so that it only shows when the list has 0 item.
        View emptyView = findViewById( R.id.empty_view );
        bookListView.setEmptyView( emptyView );

        mCursorAdapter = new BookCursorAdapter( this, null );
        bookListView.setAdapter( mCursorAdapter );

        bookListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create new intent to go to {@Link EditorActivity}
                Intent intent = new Intent( CatalogActivity.this, EditorActivity.class );

                //Form the content URI that represents the specific book that was clicked on
                //by appending the id (passed as input to this method) onto the PetEntry#ContentURI
                //For example, the URI would be "content://com.example.android.books/books/2"
                //if the book with ID 2 was clicked on.

                Uri currentBookUri = ContentUris.withAppendedId( BookEntry.CONTENT_URI, id );
                //Set the URI on the data field of the intent
                intent.setData( currentBookUri );
                Log.d( "URICHECK", currentBookUri.toString() );
                //launch the EditorActivity to display the data for the current book.
                startActivity( intent );

            }
        } );

        // Kick off the loader
        getLoaderManager().initLoader( BOOK_LOADER, null, this );

    }

    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's book attributes are the values.
        ContentValues values = new ContentValues();

        values.put( BookEntry.COLUMN_BOOK_NAME, "Kite Runner" );
        values.put( BookEntry.COLUMN_BOOK_PRICE, 10 );
        values.put( BookEntry.COLUMN_BOOK_QUANTITY, 2 );
        values.put( BookEntry.COLUMN_SUPPLIER_NAME, "PKT Suppliers" );
        values.put( BookEntry.COLUMN_SUPPLIER_PHONE, 777568 );

        // Insert a new row for Kite Runner into the provider using the ContentResolver.
        // Use the {@link PetsEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert( BookEntry.CONTENT_URI, values );

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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete( BookEntry.CONTENT_URI, null, null );
        Log.v( "CatalogActivity", rowsDeleted + " rows deleted from books database" );
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY,};

        //THIS loader will execute the content provider's query method on a background thread
        return new CursorLoader( this,//parent activity context
                BookEntry.CONTENT_URI,//Provider conent URI to query
                projection,//Columns to include in the resulting cursor
                null,//no selection cause
                null,//no selection args
                null );//no sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update {@link BookCursorAdapter} with this new cursor containing updated data
        mCursorAdapter.swapCursor( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor( null );
    }
}