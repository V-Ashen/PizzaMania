package com.example.pizzamania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PizzaMania.db";
    private static final int DB_VERSION = 4;

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users
        db.execSQL("CREATE TABLE IF NOT EXISTS users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        // branches
        db.execSQL("CREATE TABLE IF NOT EXISTS branches(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "lat REAL," +
                "lng REAL)");

        // menu items (each branch can have its own entry)
        db.execSQL("CREATE TABLE IF NOT EXISTS menu_items(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "branchId INTEGER," +
                "name TEXT," +
                "price INTEGER," +
                "imageResId INTEGER," +
                "available INTEGER)");

        // orders
        db.execSQL("CREATE TABLE IF NOT EXISTS orders(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customerName TEXT," +
                "items TEXT," +
                "totalCost INTEGER," +
                "status TEXT," +    // Pending, Approved, OnTheWay, Delivered
                "address TEXT," +
                "custLat REAL," +
                "custLng REAL," +
                "driverName TEXT," +
                "driverLat REAL," +
                "driverLng REAL," +
                "branchId INTEGER," +
                "createdAt INTEGER)");

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // safe for coursework: drop & recreate
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS branches");
        db.execSQL("DROP TABLE IF EXISTS menu_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    // ----------------- seeding -----------------
    private void seedInitialData(SQLiteDatabase db) {
        // insert branches if none exist
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM branches", null);
        boolean empty = true;
        if (c.moveToFirst()) empty = c.getInt(0) == 0;
        c.close();

        if (empty) {
            insertBranch(db, "Colombo", 6.9271, 79.8612); // id = 1
            insertBranch(db, "Galle", 6.0535, 80.2210);   // id = 2
        }

        // insert menu items for each branch if none exist
        Cursor cm = db.rawQuery("SELECT COUNT(*) FROM menu_items", null);
        boolean menuEmpty = true;
        if (cm.moveToFirst()) menuEmpty = cm.getInt(0) == 0;
        cm.close();

        if (menuEmpty) {
            // branchId 1: Colombo
            insertMenuItem(db, 1, "Cheese Pizza", 1300, R.drawable.cheese, 1);
            insertMenuItem(db, 1, "Chicken Pizza", 1500, R.drawable.chicken, 1);
            insertMenuItem(db, 1, "Veg Pizza", 1000, R.drawable.veg, 1);
            insertMenuItem(db, 1, "Sausage Pizza", 1200, R.drawable.sausage, 1);
            insertMenuItem(db, 1, "Pepsi", 500, R.drawable.pepsi, 1);

            // branchId 2: Galle
            insertMenuItem(db, 2, "Cheese Pizza", 1300, R.drawable.cheese, 1);
            insertMenuItem(db, 2, "Chicken Pizza", 1500, R.drawable.chicken, 1);
            insertMenuItem(db, 2, "Veg Pizza", 1000, R.drawable.veg, 1);
            insertMenuItem(db, 2, "Sausage Pizza", 1200, R.drawable.sausage, 1);
            insertMenuItem(db, 2, "Pepsi", 500, R.drawable.pepsi, 1);
        }
    }

    // helpers for seed (using direct db insert)
    private void insertBranch(SQLiteDatabase db, String name, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("lat", lat);
        cv.put("lng", lng);
        db.insert("branches", null, cv);
    }

    private void insertMenuItem(SQLiteDatabase db, int branchId, String name, int price, int imageResId, int available) {
        ContentValues cv = new ContentValues();
        cv.put("branchId", branchId);
        cv.put("name", name);
        cv.put("price", price);
        cv.put("imageResId", imageResId);
        cv.put("available", available);
        db.insert("menu_items", null, cv);
    }

    // ----------------- USERS -----------------
    public boolean insertUser(String u, String p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", u);
        cv.put("password", p);
        long res = db.insert("users", null, cv);
        return res != -1;
    }

    public Cursor getUser(String u, String p) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{u, p});
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete("users", "username=?", new String[]{username});
        return r > 0;
    }

    // ----------------- BRANCHES -----------------
    public Cursor getAllBranchesCursor() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM branches", null);
    }

    // compute nearest branch id using haversine
    public int getNearestBranchId(double custLat, double custLng) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, lat, lng FROM branches", null);
        double best = Double.MAX_VALUE;
        int bestId = -1;
        while (c.moveToNext()) {
            int id = c.getInt(0);
            double lat = c.getDouble(1);
            double lng = c.getDouble(2);
            double d = haversine(custLat, custLng, lat, lng);
            if (d < best) {
                best = d;
                bestId = id;
            }
        }
        c.close();
        if (bestId == -1) return 1;
        return bestId;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    // ----------------- MENU -----------------
    // returns Pizza list (uses your Pizza class constructor)
    public ArrayList<Pizza> getAvailableMenuForBranch(int branchId) {
        ArrayList<Pizza> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, name, price, imageResId FROM menu_items WHERE branchId=? AND available=1",
                new String[]{String.valueOf(branchId)});
        while (c.moveToNext()) {
            String name = c.getString(1);
            int price = c.getInt(2);
            int img = c.getInt(3);
            list.add(new Pizza(name, price + " LKR", price, img));
        }
        c.close();
        return list;
    }

    // cursor for staff UI (includes availability)
    public Cursor getMenuCursorForBranch(int branchId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id, name, price, imageResId, available FROM menu_items WHERE branchId=?",
                new String[]{String.valueOf(branchId)});
    }

    public void setMenuAvailability(int itemId, boolean available) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("available", available ? 1 : 0);
        db.update("menu_items", cv, "id=?", new String[]{String.valueOf(itemId)});
    }

    // ----------------- ORDERS -----------------
    public long insertOrder(String customerName, String items, int totalCost,
                            String address, double lat, double lng, int branchId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("customerName", customerName);
        cv.put("items", items);
        cv.put("totalCost", totalCost);
        cv.put("status", "Pending");
        cv.put("address", address);
        cv.put("custLat", lat);
        cv.put("custLng", lng);
        cv.put("branchId", branchId);
        cv.put("createdAt", System.currentTimeMillis());
        return db.insert("orders", null, cv);
    }

    public Cursor getOrdersByStatusAndBranch(String status, int branchId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE status=? AND branchId=? ORDER BY id DESC",
                new String[]{status, String.valueOf(branchId)});
    }

    public Cursor getOrdersByStatus(String status, int branchId) {
        return getOrdersByStatusAndBranch(status, branchId);
    }

    public Cursor getOrdersByUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customerName=? ORDER BY id DESC", new String[]{username});
    }

    public Cursor getLatestOrder(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customerName=? ORDER BY id DESC LIMIT 1", new String[]{username});
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

    public double getCustomerLat(long orderId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT custLat FROM orders WHERE id=?", new String[]{String.valueOf(orderId)});
        if (c.moveToFirst()) {
            double lat = c.getDouble(0);
            c.close();
            return lat;
        }
        c.close();
        return 0;
    }

    public double getCustomerLng(long orderId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT custLng FROM orders WHERE id=?", new String[]{String.valueOf(orderId)});
        if (c.moveToFirst()) {
            double lng = c.getDouble(0);
            c.close();
            return lng;
        }
        c.close();
        return 0;
    }
}