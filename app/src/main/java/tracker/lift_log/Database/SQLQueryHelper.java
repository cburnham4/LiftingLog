package tracker.lift_log.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by cvburnha on 3/6/2015.
 */
public class SQLQueryHelper {
    LiftDatabaseHelper dbHelper;
    SQLiteDatabase dbRead;

    public SQLQueryHelper(Context context){
        dbHelper = new LiftDatabaseHelper(context);
        dbRead = dbHelper.getReadableDatabase();
    }

    public int getLastDid(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Max(did) FROM Days";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        return c.getInt(0);
    }
    public int getLastLid(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Max(lid) FROM Lifts";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }
    public int getLastSid(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Max(sid) FROM Sets";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }
}
