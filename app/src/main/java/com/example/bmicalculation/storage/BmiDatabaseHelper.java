package com.example.bmicalculation.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.bmicalculation.model.BmiRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BmiDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bmi_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_DATE = "record_date";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_BMI = "bmi";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    public BmiDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RECORDS + " ("
                + COLUMN_DATE + " TEXT PRIMARY KEY,"
                + COLUMN_HEIGHT + " REAL NOT NULL,"
                + COLUMN_WEIGHT + " REAL NOT NULL,"
                + COLUMN_AGE + " INTEGER NOT NULL,"
                + COLUMN_GENDER + " TEXT NOT NULL,"
                + COLUMN_BMI + " REAL NOT NULL,"
                + COLUMN_CATEGORY + " TEXT NOT NULL,"
                + COLUMN_UPDATED_AT + " INTEGER NOT NULL" + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public void saveRecord(BmiRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, record.getRecordDate().toString());
        values.put(COLUMN_HEIGHT, Double.parseDouble(record.getHeight()));
        values.put(COLUMN_WEIGHT, Double.parseDouble(record.getWeight()));
        values.put(COLUMN_AGE, Integer.parseInt(record.getAge()));
        values.put(COLUMN_GENDER, record.getGender());
        values.put(COLUMN_BMI, Double.parseDouble(record.getBmi()));
        values.put(COLUMN_CATEGORY, record.getCategory());
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        db.insertWithOnConflict(TABLE_RECORDS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<BmiRecord> loadRecords() {
        List<BmiRecord> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                TABLE_RECORDS,
                new String[]{COLUMN_DATE, COLUMN_HEIGHT, COLUMN_WEIGHT, COLUMN_AGE, COLUMN_GENDER, COLUMN_BMI, COLUMN_CATEGORY},
                null,
                null,
                null,
                null,
                COLUMN_DATE + " ASC")) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String height = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
                String weight = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                String age = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER));
                String bmi = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BMI)));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                records.add(new BmiRecord(height, weight, age, gender, bmi, category, LocalDate.parse(date)));
            }
        }
        return records;
    }
}
