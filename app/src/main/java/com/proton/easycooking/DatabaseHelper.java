package com.proton.easycooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.proton.easycooking.models.AppConfig;
import com.proton.easycooking.models.Category;
import com.proton.easycooking.models.Recipes;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "EasyCooking";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_APPCONFIG = "AppConfig";
    private static final String TABLE_CATEGORY = "Category";
    private static final String TABLE_RECIPE = "Recipes";

    // AppInfo Table Columns
    private static final String KEY_APPINFO_ID = "Id";
    private static final String KEY_APPINFO_NAME = "Name";
    private static final String KEY_APPINFO_VALUE = "Value";

    // Category Table Columns
    private static final String KEY_CATEGORY_ID = "categoryId";
    private static final String KEY_CATEGORY_NAME = "categoryName";
    private static final String KEY_CATEGORY_IMAGE_URL = "categoryImageURL";

    // Recipe Table Columns
    private static final String KEY_RECIPE_ID = "recipeId";
    private static final String KEY_RECIPE_CATEGORY_ID = "categoryId";
    private static final String KEY_RECIPE_NAME = "recipeName";
    private static final String KEY_RECIPE_DESCRIPTION = "recipeDescription";
    private static final String KEY_RECIPE_IMAGE = "recipeImage";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Constructor should be private to prevent direct instantiation.
    // Make a call to the static method "getInstance()" instead.
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPCONFIG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
            onCreate(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_APPCONFIG_TABLE = "CREATE TABLE " + TABLE_APPCONFIG +
                "(" +
                KEY_APPINFO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_APPINFO_NAME + " TEXT," +
                KEY_APPINFO_VALUE + " TEXT" +
                ")";

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY +
                "(" +
                KEY_CATEGORY_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                //KEY_CATEGORY_NAME + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_CATEGORY_NAME + " TEXT," +
                KEY_CATEGORY_IMAGE_URL + " TEXT" +
                ")";

        String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPE +
                "(" +
                KEY_RECIPE_ID + " INTEGER PRIMARY KEY," +
                KEY_RECIPE_CATEGORY_ID + " INTEGER," +
                KEY_RECIPE_NAME + " TEXT," +
                KEY_RECIPE_DESCRIPTION + " TEXT," +
                KEY_RECIPE_IMAGE + " TEXT" +
                ")";

        db.execSQL(CREATE_APPCONFIG_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_RECIPE_TABLE);

    }

    public void addAppConfig(AppConfig data) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_APPINFO_ID, data.getId());
            values.put(KEY_APPINFO_NAME, data.getName());
            values.put(KEY_APPINFO_VALUE, data.getValue());

            db.insertOrThrow(TABLE_APPCONFIG, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add data to database");
        } finally {
            db.endTransaction();
        }

    }

    public String getAppConfig(String name) {
        String value = null;

        String selectQuery = "SELECT Value FROM " + TABLE_APPCONFIG + " WHERE Name ='" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);

            } while (cursor.moveToNext());
        }

        return value;
    }

    public String getNewRecipesId(String id) {
        String value = null;

        String selectQuery = "SELECT Name FROM " + TABLE_APPCONFIG + " WHERE Id ='" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(0);

            } while (cursor.moveToNext());
        }

        return value;
    }

    public void updateNewRecipesId(String date, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_APPINFO_NAME, date);
        cv.put(KEY_APPINFO_VALUE, value);
        db.update(TABLE_APPCONFIG, cv, "Id = ?", new String[]{"100"});

    }

    public void deleteAppConfig() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPCONFIG, "Id <> ?", new String[]{"100"});
        db.close();
    }

    // Insert a post into the database
    public void addCategory(Category category) {

        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addOrUpdateUser(post.user);
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_ID, category.getCategoryId());
            values.put(KEY_CATEGORY_NAME, category.getCategoryName());
            values.put(KEY_CATEGORY_NAME, category.getCategoryName());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_CATEGORY, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add data to database");
        } finally {
            db.endTransaction();
        }
    }

    public void addRecipe(Recipes recipe) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_RECIPE_ID, recipe.getRecipeId());
            values.put(KEY_RECIPE_CATEGORY_ID, recipe.getCategoryId());
            values.put(KEY_RECIPE_NAME, recipe.getRecipeName());
            values.put(KEY_RECIPE_DESCRIPTION, "recipe.getRecipeDescription()");
            values.put(KEY_RECIPE_IMAGE, recipe.getRecipeImage());

            db.insertOrThrow(TABLE_RECIPE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add data to database");
        } finally {
            db.endTransaction();
        }
    }

    // Get all recipes in the database
    public List<Recipes> getAllRecipes() {
        List<Recipes> dataList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " ORDER BY recipeId";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Recipes contact = new Recipes();
                    contact.setRecipeId(cursor.getString(0));
                    contact.setCategoryId(cursor.getString(1));
                    contact.setRecipeName(cursor.getString(2));
                    contact.setRecipeDescription(cursor.getString(3));
                    contact.setRecipeImage(cursor.getString(4));

                    dataList.add(contact);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get data from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dataList;
    }

    // Get Recipe by Id
    public List<Recipes> getRecipe(String id) {
        List<Recipes> dataList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " WHERE recipeId =" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Recipes contact = new Recipes();
                contact.setRecipeId(cursor.getString(0));
                contact.setCategoryId(cursor.getString(1));
                contact.setRecipeName(cursor.getString(2));
                contact.setRecipeDescription(cursor.getString(3));
                contact.setRecipeImage(cursor.getString(4));

                dataList.add(contact);
            } while (cursor.moveToNext());
        }

        return dataList;
    }

    // Delete Recipe
    public void deleteRecipe(Recipes recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPE, KEY_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipe.getRecipeId())});
        db.close();
    }


}
