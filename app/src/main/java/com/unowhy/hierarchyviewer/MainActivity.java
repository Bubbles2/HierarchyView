package com.unowhy.hierarchyviewer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    //
    static final String PROVIDER_NAME = "com.unowhy.sqoolcp.SqoolCP";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME +"/chksqool");
    //
    static final String KEY_SYNCHRONIZED  = "synchronized";// Integer 1 or 0
    public static final String KEY_ID = "_id";
    static final String KEY_SQOOLKEY = "sqoolkey";
    static final String KEY_RESOURCENAME = "resourcename";
    static final String KEY_OBJECTTYPE = "objecttype";
    static final String KEY_OBJ_TYPE  = "obj_type"; // image doc etc as integer
    static final String KEY_MOD_DATE  = "moddate";
    static final String KEY_FILE_DOC  = "file_or_doc";// File(1) or folder(0) (Integer 1 or 0)
    static final String KEY_RESOURCEPATH = "resecourcepath";
    static final String KEY_FLDRLEVEL  = "fldrlevel";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //createTestRecord();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //====================================================================
// Create Test Record
//====================================================================
    public void createTestRecord() {
         // Write New record
        ContentResolver cr = getContentResolver();
        ContentValues values  = new ContentValues();
        values.put(KEY_RESOURCENAME,"Math1.pdf");
        values.put(KEY_RESOURCEPATH,"df/Mathématiques/Math1.pdf");
        values.put(KEY_FILE_DOC,1);
        values.put(KEY_OBJECTTYPE,"pdf");
        values.put(KEY_SYNCHRONIZED,0);
        values.put(KEY_FLDRLEVEL,"3");


        cr.insert(CONTENT_URI, values);
        //
        values.put(KEY_RESOURCENAME,"Math2.pdf");
        values.put(KEY_RESOURCEPATH,"df/Mathématiques/Math2.pdf");
        values.put(KEY_FILE_DOC,1);
        values.put(KEY_OBJECTTYPE,"pdf");
        values.put(KEY_SYNCHRONIZED,0);
        values.put(KEY_FLDRLEVEL,"3");
        cr.insert(CONTENT_URI, values);
        //
        values.put(KEY_RESOURCENAME,"Science1.pdf");
        values.put(KEY_RESOURCEPATH,"df/Chimie/Science1.pdf");
        values.put(KEY_FILE_DOC,1);
        values.put(KEY_OBJECTTYPE,"pdf");
        values.put(KEY_SYNCHRONIZED,0);
        values.put(KEY_FLDRLEVEL,"3");
        cr.insert(CONTENT_URI, values);
     }
}
