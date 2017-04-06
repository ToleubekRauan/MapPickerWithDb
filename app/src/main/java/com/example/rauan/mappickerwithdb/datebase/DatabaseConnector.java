package com.example.rauan.mappickerwithdb.datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rauan.mappickerwithdb.model.PlacePickModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rauan on 031 31.03.2017.
 */

public class DatabaseConnector {
    //Название базы данных
    public static final String DATABASE_NAME = "PLACE_DB";

    //Название таблицы
    public static final String TABLE_NAME = "PICK_MARKER";

    public static final int DATABASE_VERSION = 1;

    public static final String ID = "ID";

    public static final String NAME = "NAME";

    public static final String LAT = "LAT";

    public static final String LNG = "LNG";


    public static final String CREATE_TABLE =
            "CREATE TABLE if not exists " + TABLE_NAME +
                    " (" + ID + " integer primary key autoincrement, " +
                    NAME + " TEXT, " +
                    LAT + " TEXT, " +
                    LNG + " TEXT" + ");";


    //Класс для работы с базой данных
    private SQLiteDatabase database;

    //Класс для создания базы данных
    private DatabaseHelper helper;


    public DatabaseConnector(Context context) {
        helper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Открываем базу данных
    public void open() {
        database = helper.getWritableDatabase();
    }



    //Закрываем базу данных
    public void close() {
        if (database != null) {
            database.close();
        }
    }
    public void removeMapPick(PlacePickModel placePickModel) {
        open();
        database.delete(TABLE_NAME,
                NAME + "=?",
                new String[]{placePickModel.getName() + ""});
        close();
    }
    public void insertPlaceInfo(PlacePickModel placePickModel){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,placePickModel.getName());
        contentValues.put(LAT,placePickModel.getLatitude());
        contentValues.put(LNG,placePickModel.getLongitude());
        open();
        database.insert(TABLE_NAME, null, contentValues);
        close();
    }

    public List<PlacePickModel> getPlacesInfo(){
        List<PlacePickModel> pickModelList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        open();
        Cursor cursor = database.rawQuery(query, null);

        while (cursor.moveToNext()) {
            PlacePickModel placePickModel = new PlacePickModel();
            placePickModel.setName(cursor.getString(1));
            placePickModel.setLatitude(cursor.getString(2));
            placePickModel.setLongitude(cursor.getString(3));

            pickModelList.add(placePickModel);
        }
        close();
        return pickModelList;
    }


    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory,
                              int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        //Upgrade базы данных
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
