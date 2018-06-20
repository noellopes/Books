package pt.ipg.books;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.CursorLoader;

import org.w3c.dom.Text;

public class EditBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CATEGORIES_CURSOR_LOADER_ID = 0;
    ;
    private EditText editTextTitle;
    private EditText editTextPrice;
    private Spinner spinnerCategory;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        int bookId = intent.getIntExtra(MainActivity.BOOK_ID, -1);

        if (bookId == -1) {
            finish();
            return;
        }

        Cursor cursorBook = getContentResolver().query(
                Uri.withAppendedPath(BooksContentProvider.BOOKS_URI, Integer.toString(bookId)),
                DbTableBooks.ALL_COLUMNS,
                null,
                null,
                null
        );

        if (!cursorBook.moveToNext()) {
            Toast.makeText(this, "Book not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);

        book = DbTableBooks.getCurrentBookFromCursor(cursorBook);

        editTextTitle.setText(book.getTitle());
        editTextPrice.setText(String.format("%.2f",book.getPrice()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(CATEGORIES_CURSOR_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(CATEGORIES_CURSOR_LOADER_ID, null, this);
    }

    public void cancel(View view) {
        finish();
    }

    public void save(View view) {
        // todo: validations

        book.setTitle(editTextTitle.getText().toString());
        book.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
        book.setIdCategory((int) spinnerCategory.getSelectedItemId());

        int recordsAffected = getContentResolver().update(
                Uri.withAppendedPath(BooksContentProvider.BOOKS_URI, Integer.toString(book.getId())),
                DbTableBooks.getContentValues(book),
                null,
                null
        );

        if (recordsAffected > 0) {
            Toast.makeText(this, "Book saved successfully", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Toast.makeText(this, "Could not save book", Toast.LENGTH_LONG).show();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CATEGORIES_CURSOR_LOADER_ID) {
            return new CursorLoader(this, BooksContentProvider.CATEGORIES_URI, DbTableCategories.ALL_COLUMNS, null, null, null);
        }

        return null;
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        SimpleCursorAdapter cursorAdapterCategories = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                data,
                new String[]{DbTableCategories.FIELD_NAME},
                new int[]{android.R.id.text1}
        );

        spinnerCategory.setAdapter(cursorAdapterCategories);

        int idCategory = book.getIdCategory();

        for (int i = 0; i < spinnerCategory.getCount(); i++) {
            Cursor cursor = (Cursor) spinnerCategory.getItemAtPosition(i);

            final int posId = cursor.getColumnIndex(DbTableCategories._ID);

            if (idCategory == cursor.getInt(posId)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
