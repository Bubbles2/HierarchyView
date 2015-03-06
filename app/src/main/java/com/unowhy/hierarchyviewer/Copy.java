package com.unowhy.hierarchyviewer;


import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Copy extends ListFragment implements OnItemClickListener {
    // CP Field names
    public static final String KEY_ID = "_id";
    static final String KEY_SQOOLKEY = "sqoolkey";
    static final String KEY_RESOURCENAME = "resourcename";
    static final String KEY_OBJECTTYPE = "objecttype";
    static final String KEY_SCHOOL  = "school";
    static final String KEY_CLASS = "class";
    static final String KEY_STUDENT = "student";
    static final String KEY_RESOURCEPATH = "resecourcepath";
    static final String KEY_RESOURCEDATA = "resourcedata";
    static final String KEY_LOC_LONG  = "loclong";
    static final String KEY_LOC_LAT  = "loclat";

    static final String KEY_FLDRLEVEL  = "fldrlevel";
    static final String KEY_TEACHER  = "teacher";
    static final String KEY_SUBJECT  = "subject";

    //
    static final String PROVIDER_NAME = "com.unowhy.sqoolcp.SqoolCP";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME +"/chksqool");

    //
    ArrayList<HierarchyItem> hits;
    HierarchyListAdapter customGridAdapter;
    //

    public static Copy newInstance() {
        final Copy f = new Copy();
        return f;
    }
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        getHits();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
        // Test Data Base Read

    }
    //=====================================================================
    // Get Points of interest
    //=====================================================================
    public void getHits() {
        //
        // Get Points of interest
        new LoadObjectData().execute(new String[] { "None" });

    }

    /**
     * ArrayAdapter that displays Hierarchy objects.
     */
    private static class HierarchyListAdapter extends ArrayAdapter<HierarchyItem> {
        //


        LayoutInflater mInflater;
        //
        private Context context;
        private int layoutResourceId;
        private ArrayList data = new ArrayList();
        // =================================================
        // We need the POI list adapter because the
        // we want to use a custom view the list fragment
        //===================================================

        public HierarchyListAdapter(Context context, int layoutResourceId,
                                   ArrayList data) {
            super(context, layoutResourceId, data);
            mInflater = LayoutInflater.from(context);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
            final Resources res = context.getResources();
          }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //
            HierarchyItem hierarchy = (HierarchyItem) data.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.hierarchy_listitem, parent, false);
            }

            //=============================================================================
            // This will be made more efficient in a future chapter
            TextView tv = (TextView) convertView.findViewById(R.id.txtresource);
            tv.setText(hierarchy.hiername);
            ImageView iv = (ImageView) convertView.findViewById(R.id.mythumbnail);
            // May need some tests to select image for object type
            Resources resources = context.getResources();
            iv.setImageDrawable(resources.getDrawable(R.drawable.folder));
            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }
    //
    public void readCP() {
        // TODO Auto-generated method stub

    }

    //
    //===================================================================================
    // Create Background Process to Create the images adapter 
    //===================================================================================
    // the class that will create a background thread and get call log
    // Note the last parameter is the Images adapter which we will pass in the post execute
    private class LoadObjectData extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(String... params) {
            ContentResolver cr = getActivity().getContentResolver();
            //
            //String SORT_ORDER = KEY_FLDRLEVEL + " DESC" + ", " + KEY_RESOURCENAME + " ASC";
            //
            String SORT_ORDER = KEY_FLDRLEVEL + " ASC" + ", " + KEY_RESOURCENAME + " ASC";
            //String SORT_ORDER =  KEY_RESOURCENAME + " ASC" + ", " + KEY_FLDRLEVEL + " DESC";



            Cursor c = cr.query(CONTENT_URI, null, null, null, SORT_ORDER);
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            super.onPostExecute(c);
            // Create 
            hits = new ArrayList<HierarchyItem>();
                HierarchyItem h = new HierarchyItem();


            if (c.moveToFirst()) {
                do {
                     // Get Object Record and add to arraylist 7
                   h.hiername = c.getString(c.getColumnIndex(KEY_RESOURCENAME));
                   h.hierKey = c.getString(c.getColumnIndex(KEY_RESOURCEPATH));
                   h.objType = c.getString(c.getColumnIndex(KEY_OBJECTTYPE));
                   h.level =  c.getString(c.getColumnIndex(KEY_FLDRLEVEL));
                   //

                    Log.i("DF","DF RN "+h.hiername+" RP "+h.hierKey+" type "+h.objType+" level "+h.level+" Pos "+h.level);
                   // Set Top level
                    //if (h.level.equals("3")) {
                       // h.openThisLevel=true;
                    //}

                   hits.add(h);
                } while (c.moveToNext());
                //
                customGridAdapter = new HierarchyListAdapter(getActivity(), R.layout.hierarchy_listitem,hits);
                setListAdapter(customGridAdapter);
                ListView list = (ListView) getView().findViewById(android.R.id.list);
                list.setOnItemClickListener(new OnItemClickListener()
                { @Override
                  public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
                    { Toast.makeText(getActivity(),"Test in Fragment", Toast.LENGTH_SHORT).show();
                       // TextView tid =(TextView)  v.findViewById(R.id.hidden_value_id_list);
                        //Intent editPhoto = new Intent(getActivity(),PhotoDetailHolderA.class);
                        //editPhoto.putExtra("photo_id", tid.getText().toString());
                        //startActivity(editPhoto);
                    }
                });


            }



        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            // This method is called by publish progress
            super.onProgressUpdate(values);
        }



    }
}