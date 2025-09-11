package com.example.pizzar11;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CartDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cartDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_CART = "cart";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";
    private static final String COL_QUANTITY = "quantity";

    private static final String COL_IMAGE = "image";

    public CartDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CART + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_PRICE + " REAL," +
                COL_QUANTITY + " INTEGER," +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // Add or update item
    // Add or update item
    public void addToCart(String name, double price, String imageUrl, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if item already exists
        Cursor cursor = db.query(TABLE_CART, null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            int currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
            ContentValues cv = new ContentValues();
            cv.put(COL_QUANTITY, currentQty + quantity);
            db.update(TABLE_CART, cv, "name=?", new String[]{name});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, name);
            cv.put(COL_PRICE, price);
            cv.put(COL_QUANTITY, quantity);
            cv.put(COL_IMAGE, imageUrl); // now comes from parameter
            db.insert(TABLE_CART, null, cv);
        }
        cursor.close();
    }


    // Get all items
    public List<CartItem> getAllCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART, null);
        if (cursor.moveToFirst()) {
            do {
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
                list.add(new CartItem(name, price, quantity, image));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Clear cart
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }
}
