package pt.ipg.books;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbBooksOpenHelper extends SQLiteOpenHelper {
    private static final boolean PRODUCTION = false;

    public static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public DbBooksOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        DbTableCategories dbTableCategories = new DbTableCategories(db);
        dbTableCategories.create();

        DbTableBooks dbTableBooks = new DbTableBooks(db);
        dbTableBooks.create();

        if (!PRODUCTION) {
            seed(db);
        }
    }

    private void seed(SQLiteDatabase db) {
        DbTableCategories dbTableCategories = new DbTableCategories(db);

        Category category = new Category();
        category.setName("Drama");
        int idCategoryDrama = (int) dbTableCategories.insert(DbTableCategories.getContentValues(category));

        category = new Category();
        category.setName("Romance");
        int idCategoryRomance = (int) dbTableCategories.insert(DbTableCategories.getContentValues(category));

        category = new Category();
        category.setName("Comedy");
        dbTableCategories.insert(DbTableCategories.getContentValues(category));

        DbTableBooks dbTableBooks = new DbTableBooks(db);

        Book book = new Book();
        book.setTitle("A midsummer night's dream");
        book.setIdCategory(idCategoryRomance);
        book.setPrice(9.99);
        dbTableBooks.insert(DbTableBooks.getContentValues(book));

        book = new Book();
        book.setTitle("Hamlet");
        book.setIdCategory(idCategoryDrama);
        book.setPrice(11.99);
        dbTableBooks.insert(DbTableBooks.getContentValues(book));

        book = new Book();
        book.setTitle("Macbeth");
        book.setIdCategory(idCategoryDrama);
        book.setPrice(5.99);
        dbTableBooks.insert(DbTableBooks.getContentValues(book));
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
