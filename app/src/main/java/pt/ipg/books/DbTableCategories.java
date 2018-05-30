package pt.ipg.books;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DbTableCategories implements BaseColumns {
    private static final String TABLE_NAME = "categories";
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
}
