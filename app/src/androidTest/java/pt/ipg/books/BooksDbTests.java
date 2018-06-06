package pt.ipg.books;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BooksDbTests {
    @Before
    public void setUp() {
        getContext().deleteDatabase(DbBooksOpenHelper.DATABASE_NAME);
    }

    @Test
    public void openBooksDbTest() {
        // Context of the app under test.
        Context appContext = getContext();

        DbBooksOpenHelper dbBooksOpenHelper = new DbBooksOpenHelper(appContext);

        SQLiteDatabase db = dbBooksOpenHelper.getReadableDatabase();
        assertTrue("Could not open/create books database", db.isOpen());
        db.close();
    }

    @Test
    public void categoriesCRUDtest() {
        DbBooksOpenHelper dbBooksOpenHelper = new DbBooksOpenHelper(getContext());

        SQLiteDatabase db = dbBooksOpenHelper.getWritableDatabase();

        DbTableCategories tableCategories = new DbTableCategories(db);

        Category category = new Category();
        category.setName("Scifi");

        // Insert/create (C)RUD
        long id = insertCategory(tableCategories, category);

        // query/read C(R)UD
        category = ReadFirstCategory(tableCategories, "Scifi", id);

        // update CR(U)D
        category.setName("Sci-fi");
        int rowsAffected = tableCategories.update(
                DbTableCategories.getContentValues(category),
                DbTableCategories._ID + "=?",
                new String[]{Long.toString(id)}
        );
        assertEquals("Failed to update category", 1, rowsAffected);

        // query/read C(R)UD
        category = ReadFirstCategory(tableCategories, "Sci-fi", id);

        // delete CRU(D)
        rowsAffected = tableCategories.delete(
                DbTableCategories._ID + "=?",
                new String[]{Long.toString(id)}
        );
        assertEquals("Failed to delete category", 1, rowsAffected);

        Cursor cursor = tableCategories.query(DbTableCategories.ALL_COLUMNS, null, null, null, null, null);
        assertEquals("Categories found after delete ???", 0, cursor.getCount());
    }

    @Test
    public void booksCRUDtest() {
        DbBooksOpenHelper dbBooksOpenHelper = new DbBooksOpenHelper(getContext());

        SQLiteDatabase db = dbBooksOpenHelper.getWritableDatabase();

        DbTableCategories tableCategories = new DbTableCategories(db);

        Category category = new Category();
        category.setName("Drama");

        long idCategory = insertCategory(tableCategories, category);

        DbTableBooks tableBooks = new DbTableBooks(db);

        // Insert/create (C)RUD
        Book book = new Book();

        book.setTitle("Sleeping volcan");
        book.setPrice(9.99);
        book.setIdCategory((int) idCategory);

        long id = tableBooks.insert(
                DbTableBooks.getContentValues(book)
        );
        assertNotEquals("Failed to insert book", -1, id);

        // query/read C(R)UD
        book = ReadFirstBook(tableBooks, "Sleeping volcan", 9.99, idCategory, id);

        // update CR(U)D
        book.setTitle("Sleeping volcano");
        book.setPrice(8.99);

        int rowsAffected = tableBooks.update(
                DbTableBooks.getContentValues(book),
                DbTableBooks._ID + "=?",
                new String[]{Long.toString(id)}
        );
        assertEquals("Failed to update book", 1, rowsAffected);

        // query/read C(R)UD
        book = ReadFirstBook(tableBooks, "Sleeping volcano", 8.99, idCategory, id);

        // delete CRU(D)
        rowsAffected = tableBooks.delete(
                DbTableBooks._ID + "=?",
                new String[]{Long.toString(id)}
        );
        assertEquals("Failed to delete book", 1, rowsAffected);

        Cursor cursor = tableBooks.query(DbTableBooks.ALL_COLUMNS, null, null, null, null, null);
        assertEquals("Books found after delete ???", 0, cursor.getCount());
    }

    private Book ReadFirstBook(DbTableBooks tableBooks, String expectedTitle, double expectedPrice, long expectedCategoryId, long expectedId) {
        Cursor cursor = tableBooks.query(DbTableBooks.ALL_COLUMNS, null, null, null, null, null);
        assertEquals("Failed to read books", 1, cursor.getCount());

        assertTrue("Failed to read the first book", cursor.moveToNext());

        Book book = DbTableBooks.getCurrentBookFromCursor(cursor);

        assertEquals("Incorrect book title", expectedTitle, book.getTitle());
        assertEquals("Incorrect book price", expectedPrice, book.getPrice(), 0.001);
        assertEquals("Incorrect book category", expectedCategoryId, book.getIdCategory());
        assertEquals("Incorrect book id", expectedId, book.getId());

        return book;
    }

    private long insertCategory(DbTableCategories tableCategories, Category category) {
        long id = tableCategories.insert(
                DbTableCategories.getContentValues(category)
        );

        assertNotEquals("Failed to insert a category", -1, id);

        return id;
    }

    @NonNull
    private Category ReadFirstCategory(DbTableCategories tableCategories, String expectedName, long expectedId) {
        Cursor cursor = tableCategories.query(DbTableCategories.ALL_COLUMNS, null, null, null, null, null);
        assertEquals("Failed to read categories", 1, cursor.getCount());

        assertTrue("Failed to read the first category", cursor.moveToNext());

        Category category = DbTableCategories.getCurrentCategoryFromCursor(cursor);
        assertEquals("Incorrect category name", expectedName, category.getName());
        assertEquals("Incorrect category id", expectedId, category.getId());

        return category;
    }

    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
