package pt.ipg.books;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DbTableCategories implements BaseColumns {
    public static final String TABLE_NAME = "categories";
    private static final String FIELD_NAME = "name";

    private SQLiteDatabase db;

    public DbTableCategories(SQLiteDatabase db) {
        this.db = db;
    }

    public void create() {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_NAME + " TEXT NOT NULL" +
                        ")"
        );
    }

    public static ContentValues getContentValues(Category category) {
        ContentValues values = new ContentValues();

        values.put(_ID, category.getId());
        values.put(FIELD_NAME, category.getName());

        return values;
    }

    public long insert(ContentValues values) {
        return db.insert(TABLE_NAME, null, values);
    }
}
