package com.xandy.mizi;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MiActivity extends Activity implements OnItemClickListener,OnClickListener{
	
	private Context mContext = null;
	private FontAdapter mFontAdapter = null;
	private GridView mFontGridView = null;
	private ImageLoader imageLoader = null;
	DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        if(null == imageLoader)imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MiActivity.this));
        options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_launcher)
        .showImageForEmptyUri(R.drawable.ic_launcher)
        .showImageOnFail(R.drawable.ic_launcher)
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
        
        mFontGridView = (GridView) findViewById(R.id.font_view);
        mFontAdapter = new FontAdapter();
        mFontGridView.setAdapter(mFontAdapter);
        mFontGridView.setOnItemClickListener(this);
        
        httpThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class FontAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mFonts.size();
		}

		@Override
		public Object getItem(int index) {
			return mFonts.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup viewGroup) {
			HandleView mHandleView ;
			if(null == view){
				view = LayoutInflater.from(mContext).inflate(R.layout.font_item_view, null);
				mHandleView = new HandleView();
				mHandleView.fontImg = (ImageView)view.findViewById(R.id.font_pre);
				mHandleView.fontName = (TextView)view.findViewById(R.id.font_name);
				view.setTag(mHandleView);
			} else {
				mHandleView = (HandleView) view.getTag();
			}
			mHandleView.fontName.setText(mFonts.get(index).getFontName());
			
			imageLoader.displayImage(mFonts.get(index).getFontImgURL(), mHandleView.fontImg, options);
			return view;
		}
    	
    }
    class HandleView {
    	ImageView fontImg ;
    	TextView fontName ;
    }
    
    Thread httpThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mFonts.clear();
			mFonts = HttpTools.getFonts(1, true);
			mHandler.sendEmptyMessage(GET_FONT_URL);
		}
	});
    
    private List<Font> mFonts = new ArrayList<Font>();
    private final int GET_FONT_URL = 100000;
    private final int DOWN_FONT_START = 100001;
    private final int DOWN_FONT_END = 100002;
    private final int DOWN_FONT_ERROR = 100003;
    private final int DOWN_FONT_OK = 100004;
    private final int SET_DETAIL_IMG = 100005;
    
    Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_FONT_URL:
				mFontAdapter.notifyDataSetChanged();
				break;
			case SET_DETAIL_IMG:
				imageLoader.displayImage(msg.obj.toString(),fontDetail, options);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
    };
    
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showAlertDialog(arg2,(Font)mFontAdapter.getItem(arg2));
	}
	
	private AlertDialog alert = null;
	private ImageView fontDetail = null;
	private Button mDown = null;
	private void showAlertDialog(int id,final Font font){
		if(null == alert){
			LayoutInflater flater = LayoutInflater.from(this);
			View view = flater.inflate(R.layout.font_detail_view, null);
			fontDetail = (ImageView)view.findViewById(R.id.font_detail_icon);
			mDown = (Button)view.findViewById(R.id.down);
			mDown.setOnClickListener(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext); 
			builder.setView(view);
			alert = builder.create(); 
		}
		Runnable m = new Runnable() {
			@Override
			public void run() {
				String imgUrl = HttpTools.getFontDetailImgUrl(font);
				Log.d("test", imgUrl);
				mHandler.sendMessage(Message.obtain(mHandler, SET_DETAIL_IMG, imgUrl));
			}
		};
		mThread = new Thread(m);
		mThread.start();
		alert.show();
	}
	
	private Thread mThread ;

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.down){
			mHandler.sendMessage(new Message().obtain(mHandler,DOWN_FONT_START , 0, 0));
		}
	}
    
}
