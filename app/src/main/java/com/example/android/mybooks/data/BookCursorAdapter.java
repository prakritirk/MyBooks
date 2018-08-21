package com.example.android.mybooks.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybooks.BookContract.BookEntry;
import com.example.android.mybooks.R;

/**
 * Created by prajbhanda on 8/15/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super( context, c, 0 /* flags */ );
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from( context ).inflate( R.layout.list_item, parent, false );
    }

    /**
     * This method binds the Book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current Book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameTextView = (TextView) view.findViewById( R.id.name );
        TextView priceTextview = (TextView) view.findViewById( R.id.price );
        TextView quantityTextview = (TextView) view.findViewById( R.id.quantity );
        ImageButton sellButton = view.findViewById( R.id.sell_button );


        // Extract properties from cursor
        int idColumnIndex = cursor.getColumnIndex( BookEntry._ID );
        int nameColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_NAME );
        int priceColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_PRICE );
        int quantityColumnIndex = cursor.getColumnIndex( BookEntry.COLUMN_BOOK_QUANTITY );

        //Read the Book attributes from the cursor or the current Book
        final int bookId = cursor.getInt( idColumnIndex );
        String bookName = cursor.getString( nameColumnIndex );
        String bookPrice = cursor.getString( priceColumnIndex );
        String bookQuantity = cursor.getString( quantityColumnIndex );
        final int quantity = Integer.valueOf( bookQuantity );


        //Add attribute labels

        String priceLabel = "Price:" + bookPrice + " $";
        String quantityLabel = "Quantity:" + bookQuantity;


        // Populate fields with extracted properties
        nameTextView.setText( bookName );
        priceTextview.setText( priceLabel );
        quantityTextview.setText( quantityLabel );

//        //Setting Onclick listener on the sell button
        sellButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    int reducedQuantity = quantity - 1;
                    Uri currentUri = ContentUris.withAppendedId( BookEntry.CONTENT_URI, bookId );
                    ContentValues values = new ContentValues();
                    values.put( BookEntry.COLUMN_BOOK_QUANTITY, reducedQuantity );
                    context.getContentResolver().update( currentUri, values, null, null );
                } else
                    Toast.makeText( context, "This book is sold out!", Toast.LENGTH_SHORT ).show();
            }
        } );
    }


}