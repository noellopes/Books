package pt.ipg.books;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
