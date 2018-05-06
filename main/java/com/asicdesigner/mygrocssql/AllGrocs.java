
package com.asicdesigner.mygrocssql;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class AllGrocs extends ListActivity implements LoaderManager.LoaderCallbacks {
    private static final int ACTIVITY_CREATE = 0;
    private static final int DELETE_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.CATEGORY_SECONDARY;
    private static final String TAG = "Fixgrocs";
    private ImageButton mAddButton;
    private EditText mTitleText;
    private EditText mTypeText;
    private EditText mStatusText;
    private EditText mVenueText;
    private Cursor cursor;
    private static final String[] PROJECTION = new String[]{"_id", "title", "status"};
    private static final int LOADER_ID = 1;
    String URL = "content://com.asicdesigner.mygrocssql.provider.Groceries/grocsAdapter";
    Uri groceries_uri = Uri.parse(URL);

    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    android.support.v4.widget.SimpleCursorAdapter grocsAdapter;
    ListView listview;
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allgroceries);
        int[] viewIDs = {R.id.text1};
        String[] colname = {"title"};
        grocsAdapter = new android.support.v4.widget.SimpleCursorAdapter(this, R.layout.groceryrow, null, colname, viewIDs, 0);
        grocsAdapter.setViewBinder(new CustomViewBinder());
        setListAdapter(grocsAdapter);
        mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
        final Intent j = new Intent(this, GrocsToGet.class);
        //final Intent k = new Intent(this, GroceriesAdd.class);
        registerForContextMenu(getListView());
/********************************************************************/
        Button confirmButton = (Button) findViewById(R.id.confirm);
           confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTitleText = (EditText) findViewById(R.id.title);
                String mydata = mTitleText.getText().toString();
                Log.v(TAG, "pushed confirm button   " + mydata);
                ContentValues values = new ContentValues();
                values.put(GroceryProvider.KEY_TITLE, mydata);
                values.put(GroceryProvider.KEY_STATUS, "active");
                getContentResolver().insert(groceries_uri,values);


                     Toast.makeText(getApplicationContext(),mydata +" added",Toast.LENGTH_LONG);
                }
           });
/********************************************************************/
        ImageButton backButton = (ImageButton) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.v(TAG, "pushed the back button");
                startActivityForResult(j, ACTIVITY_CREATE);
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /********************************************************************/
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.v(TAG, "onmenuID is on" + item);
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                String mSelectionClause = "_id = " + info.id;
                getContentResolver().delete(groceries_uri,
                        mSelectionClause,
                        null
                );
                break;
            case EDIT_ID:
             //   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
              //  String mSelectionClause = "_id = " + info.id;
             //   getContentResolver().edit(groceries_uri,
            //            mSelectionClause,
              //          null
               // );
                break;
        }
        return true;
    }

    /********************************************************************/
      private void makeActive(long id) {
        ContentValues values = new ContentValues();
        Log.v(TAG, "WE are IN makeactive!!!!!!!!");
        values.put("status", "active");
        String mSelectionClause = "_id = " + id;
        getContentResolver().update(
                groceries_uri,
                values,
                mSelectionClause,
                null);
    }

    /********************************************************************/
    @Override
    public void onLoaderReset(Loader loader) {
    }
    /********************************************************************/
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(AllGrocs.this, groceries_uri, PROJECTION, null, null, null);
    }
    /********************************************************************/
    //@Override
    public void onLoadFinished(Loader loader, Object cursor) {
        grocsAdapter.swapCursor((Cursor) cursor);
    }
    /*******************************************************************************/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    }
    /*******************************************************************************/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
makeActive(id);
        //    v.setBackgroundColor(Color.YELLOW);
    }
    /*******************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
/*******************************************************************************/
    @Override
    public void onStart() {
        super.onStart();
       // client.connect();
       // Action viewAction = Action.newAction(
       //         Action.TYPE_VIEW,
       //         "AllGrocs Page",
       //       null, //  Uri.parse("http://host/path"),
       //         Uri.parse("android-app://com.asicdesigner.mygrocssql/http/host/path")
       // );
       // AppIndex.AppIndexApi.start(client, viewAction);
    }
/*******************************************************************************/
    @Override
    public void onStop() {
        super.onStop();
     //   Action viewAction = Action.newAction(
     //           Action.TYPE_VIEW,
     //           "AllGrocs Page",
     //         null, //  Uri.parse("http://host/path"),
     //           Uri.parse("android-app://com.asicdesigner.mygrocssql/http/host/path")
     //   );
     //   AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
/*******************************************************************************/
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
