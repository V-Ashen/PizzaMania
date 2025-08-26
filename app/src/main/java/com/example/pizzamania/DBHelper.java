package com.example.pizzamania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context ctx) { super(ctx, "PizzaDB", null, 2); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS orders(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customerName TEXT," +
                "items TEXT," +
                "totalCost INTEGER," +
                "status TEXT," +               // Pending, Approved, OnTheWay, Delivered
                "address TEXT," +
                "custLat REAL," +
                "custLng REAL," +
                "driverName TEXT," +
                "driverLat REAL," +
                "driverLng REAL," +
                "createdAt INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    /* USERS */
    public boolean insertUser(String u, String p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", u);
        cv.put("password", p);
        return db.insert("users", null, cv) != -1;
    }

    public Cursor getUser(String u, String p) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{u,p});
    }

    /* ORDERS */
    public long insertOrder(String customerName, String items, int totalCost, String address, double lat, double lng) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("customerName", customerName);
        cv.put("items", items);
        cv.put("totalCost", totalCost);
        cv.put("status", "Pending");
        cv.put("address", address);
        cv.put("custLat", lat);
        cv.put("custLng", lng);
        cv.put("createdAt", System.currentTimeMillis());
        return db.insert("orders", null, cv);
    }

    // Check if a user exists with given username & password
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public Cursor getOrdersByStatus(String status) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE status=?", new String[]{status});
    }

    public Cursor getOrderById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE id=?", new String[]{String.valueOf(id)});
    }

    public void updateStatus(long id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("orders", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void assignDriver(long id, String driverName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("driverName", driverName);
        db.update("orders", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateDriverLocation(long id, double lat, double lng) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("driverLat", lat);
        cv.put("driverLng", lng);
        db.update("orders", cv, "id=?", new String[]{String.valueOf(id)});
    }

    // Get all orders for a specific user
    public Cursor getOrdersByUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customerName=? ORDER BY id DESC", new String[]{username});
    }

    // Get latest order for a specific user
    public Cursor getLatestOrder(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customerName=? ORDER BY id DESC LIMIT 1", new String[]{username});
    }

    // Delete user
    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "username=?", new String[]{username});
        return result > 0;
    }


}
