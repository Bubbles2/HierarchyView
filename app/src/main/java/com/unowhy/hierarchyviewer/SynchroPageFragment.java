package com.unowhy.hierarchyviewer;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SynchroPageFragment extends ListFragment  implements
        LoaderManager.LoaderCallbacks<Cursor>  {
    //
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

    //
    private MyCursorAdapter adapter;
    // Drag parameters
    private final int INVALID_ID = -1;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;
    private int mDownY = -1;
    private int mDownX = -1;
    private int mTotalOffset = 0;
    private boolean mCellIsMobile = false;
    private BitmapDrawable mHoverCell;
    private final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private long mMobileItemId = INVALID_ID;
    private final int LINE_THICKNESS = 15;





    public SynchroPageFragment() {
        // Required empty public constructor
    }
    //


//    @Override
//    public void onListItemClick(ListView l, View v, int pos, long id) {
//        super.onListItemClick(l, v, pos, id);
//
//        mDownX = (int) v.getX();
//        mDownY = (int) v.getY();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    //
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        ListView lv = getListView();
        //lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        // Create a new adapter and bind it to the List View
        adapter = new MyCursorAdapter(getActivity(),null,
                new String[] { KEY_RESOURCENAME,  KEY_MOD_DATE},
                new int[] { R.id.txtresource,R.id.txtModDate }, 0);
        //

        setListAdapter(adapter);
        // Initiate the Cursor Loader
        getLoaderManager().initLoader(0, null, this);
    }


    //====================================================================
    // Load from content provider in background
    //====================================================================
    public CursorLoader onCreateLoader(int id, Bundle args) {
        //
        String qryString =  KEY_SYNCHRONIZED + " =  \'0\' AND "+KEY_FILE_DOC + " =  \'1\'";
        //String qryString =  KEY_SYNCHRONIZED + " =  0 AND "+KEY_FILE_DOC + " =  1";


        // Construct the new query in the form of a Cursor Loader.
        String[] projection = { KEY_ID, KEY_RESOURCENAME,KEY_RESOURCEPATH, KEY_OBJ_TYPE, KEY_MOD_DATE,KEY_FILE_DOC};

        // Create the new Cursor loader.
        return new CursorLoader(getActivity(), CONTENT_URI,projection, qryString, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    //
    /**
     * Listens for long clicks on any items in the listview. When a cell has
     * been selected, the hover cell is created and set up.
     */
//    public  AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
//        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
//                                long id){
//
//            try {
//                mTotalOffset = 0;
//                //Toast.makeText(getContext(), "OnItemClickListener", 100).show();
//
//
//                int position =  getListView().pointToPosition(mDownX, mDownY);
//                int itemNum = position -  getListView().getFirstVisiblePosition();
//
//                View selectedView = getListView().getChildAt(itemNum);
//
//                mMobileItemId = getListView().getAdapter().getItemId(position);
//                mHoverCell = getAndAddHoverView(selectedView);
//                selectedView.setVisibility(View.INVISIBLE);
//
//                mCellIsMobile = true;
//
//                //updateNeighborViewsForID(mMobileItemId);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                //e.printStackTrace();
//
//
//
//            }
//
//        }
//    };
//========================================================
    /**
     * Creates the hover cell with the appropriate bitmap and of appropriate
     * size. The hover cell's BitmapDrawable is drawn on top of the bitmap every
     * single time an invalidate call is made.
     */
    private BitmapDrawable getAndAddHoverView(View v) {

        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        Bitmap b = getBitmapWithBorder(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }
    /** Draws a black border over the screenshot of the view passed in. */
    private Bitmap getBitmapWithBorder(View v) {
        Bitmap bitmap = getBitmapFromView(v);
        Canvas can = new Canvas(bitmap);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_THICKNESS);
        paint.setColor(Color.BLACK);

        can.drawBitmap(bitmap, 0, 0, null);
        can.drawRect(rect, paint);

        return bitmap;
    }
    /** Returns a bitmap showing a screenshot of the view passed in. */
    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    //====================================================================
// Modify data displayed in list
//====================================================================
    class MyCursorAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;
        public MyCursorAdapter(Context context, Cursor c, String[] from,int[] to,int flags) {
            super(context,c,flags);
            // TODO Auto-generated constructor stub
            cursorInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public void bindView(View row, Context context, Cursor cursor) {
            // TODO Auto-generated method stub
            TextView text1 = (TextView) row.findViewById(R.id.txtresource);
            TextView text2 = (TextView) row.findViewById(R.id.txtModDate);
            TextView text3 = (TextView) row.findViewById(R.id.hidden_value_path);
            text1.setText(cursor.getString(1));
            text2.setText(cursor.getString(3));
            text3.setText(cursor.getString(2));

            //Simple Drag
            row.setOnTouchListener(new MyTouchListener());



        }
        @Override
        public View newView(Context arg0, Cursor cursor, ViewGroup parent) {
            // TODO Auto-generated method stub
            return cursorInflater.inflate(R.layout.synchro_page_listitem, parent, false);
        }

        //====================================================================
        //
        //====================================================================
        private class MyTouchListener implements View.OnTouchListener {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // This will create a bitmap of the view selected Why ??
                    mHoverCell = getAndAddHoverView(view);
                    //view.setVisibility(View.INVISIBLE);

                    // Get current data and pass it in clip data
                    TextView tv = (TextView) getActivity().findViewById(R.id.hidden_value_path);
                    String mpath = tv.getText().toString();
                    ClipData data = ClipData.newPlainText("filepath", mpath);
                    //
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    //view.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

}




