package com.asicdesigner.mygrocssql;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteOutOfMemoryException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
//import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by greg on 5/21/2016.
 */
public class GroceryProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.asicdesigner.mygrocssql.provider.Groceries";
    static final String URL = "content://" + PROVIDER_NAME + "/grocsAdapter";
    static final String URL_OTHER = "content://" + PROVIDER_NAME + "/grocsAdapter_other";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final Uri CONTENT_URI_OTHER = Uri.parse(URL);

    static final String KEY_TITLE = "title";
    static final String KEY_CATEGORY = "category";
    static final String KEY_TYPE = "type";
    static final String KEY_STATUS = "status";
    static final String KEY_VENUE = "venue";
    static final String KEY_ROWID = "_id";
    static final int GROCS = 1;
    static final int MYVENUE = 4;
    static final int GROCS_ITEM = 2;
    static final int GROCS_SEARCH_SUGGEST = 2;
    static final int VENUE_ITEM = 3;
    static final int VENUE_SEARCH_SUGGEST = 3;

    static final int CATEGORY = 2;
    static final int TYPE = 3;
    static final int STATUS = 4;
    static final int VENUE = 5;
    static final int ID = 6;
    private static SQLiteDatabase mDb;

    private static HashMap<String, String> GroceriesMap;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter", GROCS);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter/#", GROCS_ITEM);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter/" + SearchManager.SUGGEST_URI_PATH_QUERY, GROCS_SEARCH_SUGGEST);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter_other", MYVENUE);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter_other/#", VENUE_ITEM);
        uriMatcher.addURI(PROVIDER_NAME, "grocsAdapter_other/" + SearchManager.SUGGEST_URI_PATH_QUERY, VENUE_SEARCH_SUGGEST);
    }

    private static final String DATABASE_NAME = "data1";
    private static final String DATABASE_TABLE = "grocs";
    private static final String DATABASE_TABLE_OTHER = "venue";
    private static final int DATABASE_VERSION =   20;
    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE   +
            "(" +
            "_id integer primary key autoincrement, " +
            "title text not null, " +
            "type text, " +
            "category text, " +
            "venue numeric, " +
            "status text not null);";

    public static class DBHelper extends SQLiteOpenHelper {
        private Context fContext;
        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fContext = context;}
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
            addDefaultValues(db);
             }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
           //  onCreate(db)
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
            db.execSQL(DATABASE_CREATE);
            addDefaultValues(db);
        }

        public void addDefaultValues(SQLiteDatabase db){
          //  db=this.getWritableDatabase();
            Toast t =Toast.makeText(fContext, "got to balues" ,Toast.LENGTH_LONG);
            //t.show();
            ContentValues _Values = new ContentValues();
            String[] myarray = {
                    "ahi",
                    "ajax",
                    "almond milk",
                    "almonds",
                    "anchovies",
                    "avocados",
                    "black sesame seeds",
                    "cashews",
                    "chard",
                    "chicken",
                    "condiments",
                    "conditioner",
                    "cream",
                    "dental floss",
                    "deodorant",
                    "blueberries",
                    "egg whites",
                    "feta cheese",
                    "goat cheese",
                    "goat milk",
                    "grape seed oil",
                    "lettuce",
                    "liquid hand soap",
                    "melatonin",
                    "mouth wash",
                    "olive oil",
                    "onions",
                    "peaches",
                    "pears",
                    "pecans",
                    "pine nuts",
                    "pita",
                    "polenta",
                    "prawns",
                    "raisins",
                    "rice milk",
                    "salmon",
                    "scallops",
                    "shampoo",
                    "sheep cheese",
                    "shrimp",
                    "smoked salmon",
                    "toilet paper",
                    "tooth paste",
                    "tuna",
                    "walnuts",
                    "zucchini",
                    "frozen blueberries",
                    "flax seeds"
            };
        for (String x:myarray) {
            _Values.put("title", x);
            _Values.put("status", "notactive");
            db.insert(DATABASE_TABLE, "", _Values);
            }
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context);
        mDb = dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DATABASE_TABLE);
        switch (uriMatcher.match(uri)){
            case GROCS:
                queryBuilder.setProjectionMap(GroceriesMap);
                break;
            case GROCS_ITEM:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            sortOrder= "title  COLLATE NOCASE";
        }

        Cursor cursor = queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = mDb.insert(DATABASE_TABLE, "", values);
        if(row > 0){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            Log.i("TEST","Not working - Got to the insert part ");
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);
    }

   // public String getStatus(long id){
  //      Cursor c = query(Uri uri, null, null, null, null);
  //      return "active";
 //   }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case GROCS:
                count = mDb.delete(DATABASE_TABLE, selection, selectionArgs);
                break;
            case GROCS_ITEM:
                String id = uri.getLastPathSegment();
                count = mDb.delete(DATABASE_TABLE, ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case GROCS:
                count = mDb.update(DATABASE_TABLE, values, selection, selectionArgs);
                break;
            case GROCS_ITEM:
                String id = uri.getLastPathSegment();
                mDb.update(DATABASE_TABLE, values, ID + "=" + id +(!TextUtils.isEmpty(selection) ? " " +
                        "AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
    }
}
