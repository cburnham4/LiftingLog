package tracker.lift_log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LiftDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private  static final String DATABASE_NAME ="LiftDatabase.db";
    //private static final String DICTIONARY_TABLE_NAME = "dictionary";
    private static final String DAY_TABLE_CREATE =
            "CREATE TABLE Days("
                    + "did integer primary key AUTOINCREMENT,"
                    + "day TEXT"
                    + ")";

    private static final String LIFT_TABLE_CREATE =
            "Create table Lifts("
                    + "lid integer primary key AUTOINCREMENT,"
                    + "did integer,"
                    + "liftname TEXT,"
                    + "FOREIGN KEY(did) REFERENCES Days(did)"
                    + ")";

    private static final String SETS_TABLE_CREATE =
            "Create table Sets("
                    + "sid integer primary key AUTOINCREMENT,"
                    + "lid integer"
                    + "sets integer,"
                    + "reps integer,"
                    + "weight integer,"
                    + "date_Created text,"
                    + "FOREIGN KEY(lid) REFERENCES Lifts(lid)"
                    + ")";

    private static final String MAX_TABLE_CREATE=
            "CREATE TABLE Max("
                    +"mid integer primary key AUTOINCREMENT, "
                    + "lid integer, "
                    + "maxWeight REAL," +
                    "sid integer, "

                    + "date_Lifted text)";

    private final Context myContext;
    public SQLiteDatabase dbSqlite;

    public LiftDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        //if(checkDataBase()){
        db.execSQL(DAY_TABLE_CREATE);
        db.execSQL(LIFT_TABLE_CREATE);
        db.execSQL(SETS_TABLE_CREATE);
        db.execSQL(MAX_TABLE_CREATE);
        //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If you need to add a new column
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE Max ADD COLUMN sid integer");
        }
    }
}
