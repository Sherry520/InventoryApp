package com.example.gyh.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gyh.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by gyh on 2016/11/1.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_PICTURE + " TEXT NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_TEL + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_TIMES + " INTEGER DEFAULT 0 );";
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
