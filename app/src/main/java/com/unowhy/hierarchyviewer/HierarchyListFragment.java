package com.unowhy.hierarchyviewer;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.net.Uri;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HierarchyListFragment extends ListFragment {
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
    int myposa=-1;
    int parentPos=-1;



    //
    ArrayList<HierarchyItem> hits = new ArrayList<HierarchyItem>();
    ArrayList<HierarchyItem> displayHits;
    HierarchyListAdapter customGridAdapter;
    String objsList="";
    //

    public static HierarchyListFragment newInstance() {
        final HierarchyListFragment f = new HierarchyListFragment();
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


    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        // Get selected reference
        HierarchyItem h =displayHits.get(pos);
        // Is the level already open
        if (h.showChildren) {
            CloseDisplayListItems(h.currentpos);
            h.showChildren=false;
        }else
        {
            setNewDisplayListItems(h.currentpos);
            h.showChildren=true;
        }
        //
           createDisplayList();
            customGridAdapter = new HierarchyListAdapter(getActivity(), R.layout.hierarchy_listitem,displayHits);
            setListAdapter(customGridAdapter);

        Toast.makeText(getActivity(), "Item " + pos + " was clicked", Toast.LENGTH_SHORT).show();
    }

    private void CloseDisplayListItems(int parent) {
        //
        for (HierarchyItem h : hits) {
            if (h.parentPos==parent) {
                //
                h.openThisLevel=false;
                //
                if (h.showChildren) {
                    CloseDisplayListItems(h.currentpos);
                }
            }
        }
    }

    private void setNewDisplayListItems(int parent) {
        //
        for (HierarchyItem h : hits) {
            if (h.openThisLevel || h.parentPos==parent) {
                //
                     h.openThisLevel=true;
                }
                //
            }
        }
    // Move items to display
    protected void createDisplayList() {
        // Dislay only open items
        displayHits = new ArrayList<HierarchyItem>();
        for (HierarchyItem h : hits) {
            if (h.openThisLevel) {
                displayHits.add(h);
            }
        }
    }

    //
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
    private class HierarchyListAdapter extends ArrayAdapter<HierarchyItem> {
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
                    MyDragListener mydraglistener = new MyDragListener(context);
                    convertView.setOnDragListener(mydraglistener);
                //

                }
                // get indentation
                String indentation="";
                int indents =Integer.parseInt(hierarchy.level);
                    for (int i = 0; i <= indents; i++) {
                    indentation=indentation+"--";
                    }

                //=============================================================================
                // This will be made more efficient in future
                TextView tv = (TextView) convertView.findViewById(R.id.txtresource);
                String obj = indentation+" "+hierarchy.objectname;
                tv.setText(obj);
                ImageView iv = (ImageView) convertView.findViewById(R.id.mythumbnail);
                // May need some tests to select image for object type
                Resources resources = context.getResources();
                iv.setImageDrawable(resources.getDrawable(R.drawable.folder));

            return convertView;
        }
    }



    public void readCP(String qryString,String lvl,int parentPos) {
        // TODO Auto-generated method stub
        //
        ContentResolver cr = getActivity().getContentResolver();
        qryString = qryString+" AND "+KEY_FLDRLEVEL+ " = \'"+lvl+"\'";
        Cursor c = cr.query(CONTENT_URI, null, qryString, null, null);
        if (c.moveToFirst()) {
            do {
                myposa++;
                // Get Object Record and add to arraylist 7
                 objsList= objsList+c.getString(c.getColumnIndex(KEY_RESOURCENAME))+ "%" + myposa+ "%"
                        +parentPos+"%" + c.getString(c.getColumnIndex(KEY_OBJECTTYPE))+"%" + c.getString(c.getColumnIndex(KEY_FLDRLEVEL))+"%£";
                qryString =  KEY_RESOURCENAME  + " LIKE \'"+ c.getString(c.getColumnIndex(KEY_RESOURCENAME))+"%\'";
                //
                int i = Integer.parseInt(lvl);
                i++;
                readCP(qryString,""+i,myposa);
                //}


            } while (c.moveToNext());

        }

     }

    //
    //===================================================================================
    // Create Background Process to Create the images adapter 
    //===================================================================================
    // the class that will create a background thread and get call log
    // Note the last parameter is the Images adapter which we will pass in the post execute
    private class LoadObjectData extends AsyncTask<String, Void, ArrayList<HierarchyItem>> {
        //
        @Override
        protected ArrayList<HierarchyItem> doInBackground(String... params) {
        //
            String qry = KEY_RESOURCENAME  + " LIKE \'%\'";
            readCP(qry,"2",parentPos);
            //
            // Create List of subjects
            createList();
            return  hits;
        }

        @Override
        protected void onPostExecute(ArrayList<HierarchyItem> hits) {
            super.onPostExecute(hits);
            //
            createDisplayList();
            //
           // HierarchyListAdapter hla = new HierarchyListAdapter(getActivity(), R.layout.hierarchy_listitem,displayHits);
            customGridAdapter = new HierarchyListAdapter(getActivity(), R.layout.hierarchy_listitem,displayHits);
            getListView().setDivider(null);
            getListView().setDividerHeight(0);
            setListAdapter(customGridAdapter);



        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            // This method is called by publish progress
            super.onProgressUpdate(values);
        }




            protected void createList() {
             //
             // Create a list of menu items from string
             // There were problems with the array list in the recursive method
            String[] options = objsList.split("£");
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                String[] detail = option.split("%");
                HierarchyItem h = new HierarchyItem();
                //
                h.hiername = detail[0];
                h.hierKey = "";
                h.objType = detail[3];
                h.level = detail[4];
                h.currentpos=Integer.parseInt(detail[1]);
                h.parentPos=Integer.parseInt(detail[2]);
                h.showChildren=false;
                if (h.level.equals("2")) {
                    h.openThisLevel=true;
                } else
                {h.openThisLevel=false;}
                // get object name
                String[] hierparts = h.hiername.split("/");
                h.objectname = hierparts[hierparts.length-1];
                hits.add( h.currentpos,h);
            }
        }
    }
//===========================================================================================
//
//===========================================================================================
class MyDragListener implements View.OnDragListener {

    private Context context;
    Drawable enterShape;
    Drawable normalShape;

    public MyDragListener(Context context) {
        this.context = context;
        final Resources res = context.getResources();
        enterShape = res.getDrawable(R.drawable.shape_droptarget);
        normalShape= res.getDrawable(R.drawable.shape);

    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundDrawable(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);
                //
                // Gets the text data from the item.
                String dragData = item.getText().toString();
                // Get From Details from
                String[] dditems =dragData.split("/");
                String fromObj = dditems[dditems.length-1];
                String fromPath = dragData.replace(fromObj,"");
                //
                // Get To details
                String to = ((TextView) v.findViewById(R.id.txtresource)).getText().toString();
                v.setBackground(null);
                //
                //Display Message;
                Toast.makeText(getActivity(),"Copy  "+fromObj+" from "+fromPath+" to "+to, Toast.LENGTH_LONG).show();



                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
               // owner.removeView(view);
               // LinearLayout container = (LinearLayout) v;
               // container.addView(view);
               // view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
             //   v.setBackgroundDrawable(normalShape);
                v.setBackground(null);
            default:
                break;
        }
        return true;
    }
}

}