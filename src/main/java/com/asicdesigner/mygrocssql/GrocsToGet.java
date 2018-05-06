package com.asicdesigner.mygrocssql;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;
import static android.text.format.Time.getJulianDay;


public class GrocsToGet extends ListActivity implements LoaderManager.LoaderCallbacks {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    GroceryProvider groceryProvider;
    public static Cursor myCursor;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final String TAG = "GrocsToGet";
    private static final String[] PROJECTION = new String[]{"_id", "title"};
    private static final int LOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    String myAction;// = "allgrocs";
    String tm;

    ListView listview;
    String URL = "content://com.asicdesigner.mygrocssql.provider.Groceries/grocsAdapter";
    Uri groceries_uri = Uri.parse(URL);
    android.support.v4.widget.SimpleCursorAdapter grocsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("message");

      //  JSONObject jsonObject = new JSONObject();
        setContentView(R.layout.groceriestoget_list);
        //ImageButton writebutton =  (ImageButton) findViewById(R.id.writebutton);
        //ImageButton readbutton =  (ImageButton) findViewById(R.id.readbutton);
        final Intent jactivity = new Intent(this, AllGrocs.class);
        int[] viewIDs = {R.id.text1};
        String[] colname = {"title"};
        grocsAdapter = new SimpleCursorAdapter(this, R.layout.groceryrow, myCursor, colname, viewIDs, 0);
        //grocsAdapter.setViewBinder(new CustomViewBinder());
        //   grocsAdapter = new CustomViewBinder( this, R.layout.groceryrow,  null, colname, viewIDs,  0);
        setListAdapter(grocsAdapter);
        mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
        ImageButton gobutton = (ImageButton) findViewById(R.id.jumpbutton);
        gobutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myAction = "allgrocs";
                startActivityForResult(jactivity, ACTIVITY_CREATE);
                Log.v(TAG, "pushed the button");
            }
        });
             myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
              //  String myvalue = "";
              //  for(String mystring) {
              //      myvalue = myvalue + dataSnapshot.getValue(String[].class);
               //     Toast toast = Toast.makeText(getApplicationContext(), myvalue + " is my value", Toast.LENGTH_LONG);
              //      toast.show();
              //      Log.d(TAG, "Value is: " + myvalue);
            //    }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
     }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        makeInactive(id);
    }

    private void makeInactive(long id) {
        ContentValues values = new ContentValues();
        values.put(GroceryProvider.KEY_STATUS, "inactive");
        String mSelectionClause = "_id = " + id;
        int oount = getContentResolver().update(
                groceries_uri,
                values,
                mSelectionClause,
                null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String SELECTION = GroceryProvider.KEY_STATUS + "= 'active'";
        return new CursorLoader(this, groceries_uri, PROJECTION, SELECTION, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                grocsAdapter.swapCursor((Cursor) cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    private class CustomViewBinder implements android.support.v4.widget.SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int statusIndex = cursor.getColumnIndex("status");
            int titleIndex = cursor.getColumnIndex("title");
            int idIndex = cursor.getColumnIndex("_id");
            String grocStatus = "";
            String grocTitle = cursor.getString(titleIndex);
            String grocId = cursor.getString(idIndex);
            grocStatus = cursor.getString(statusIndex);

            TextView tv = (TextView) view;
            tv.setText(grocTitle);
            Log.v(TAG, "Current grocery = " + grocTitle + " with an id of " + grocId + " and status = " + grocStatus);
            view.setBackgroundColor(0xddffffee);
            tv.setTextColor(Color.BLACK);

            if (statusIndex == -1) {
                Log.v(TAG, "WELL We have a -1 meaning col don't exist!");
                return false;
            } else {
                if (grocStatus.equals("active")) {
                    Log.v(TAG, "--->>>>>>>We have " + grocTitle + " with an id of " + grocId);
                    view.setBackgroundColor(0x116666ee);
                    //   view.setVisibility(View.VISIBLE);
                    //      view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    //    view.setAlpha(0.4f);

                    tv.setText("   " + grocTitle);
                    tv.setTextColor(Color.WHITE);
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                    return true;
                } else
                    //  view.setBackgroundColor(Color.WHITE);
                    return false;
            }

        }
    }
}

