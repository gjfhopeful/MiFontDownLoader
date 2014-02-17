package com.xandy.mizi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xandy.mizi.R.id;

public class MiActivity extends Activity implements OnItemClickListener,OnClickListener{
	
	private boolean DEBUG = true;
	private String TAG = "MiActivity";
	
	private Context mContext = null;
	private FontAdapter mFontAdapter = null;
	private GridView mFontGridView = null;
	private ImageLoader imageLoader = null;
	DisplayImageOptions options;
	
	private ProgressBar mProgressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        
        log("onCreate");
        if(null == imageLoader)imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MiActivity.this));
        log("init ImageLoader");
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
        
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        
        getFons();
        
        showConnectTip();
    }
    
    private int pageid = 1;
    private boolean isHot = true;
    public void getFons(){
    	getFons(false);
    }
    public void getFons(final boolean isRefsh){
    	Thread httpThread = new Thread(new Runnable() {
    		@Override
    		public void run() {
    			log("start get font grid data");
    			mHandler.sendEmptyMessage(GET_FONT_URL_START);
    			mFonts.clear();
    			mFonts = HttpTools.getFonts(pageid, isHot);
    			mHandler.sendEmptyMessage(isRefsh?GET_FONT_URL_START:GET_FONT_URL_END);
    		}
    	});
    	httpThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        
//        lastPage = menu.getItem(0);
//        lastPage.setEnabled(1 != pageid);
//        
//        nextPage = menu.getItem(1);
//        nextPage.setEnabled(13 != pageid);
//        
//        New = menu.getItem(2);
//        New.setVisible(isHot);
//        
//        Hot = menu.getItem(3);
//        Hot.setVisible(!isHot);
        
        return true;
    }
    
    private MenuItem lastPage;
    private MenuItem nextPage;
    private MenuItem Hot;
    private MenuItem New;
    
    public void updateMenuUi(){
    	lastPage.setEnabled(1 != pageid);
    	nextPage.setEnabled(13 != pageid);
    	Hot.setVisible(!isHot);
    	New.setVisible(isHot);
    }
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
    	menu.clear();
    	getMenuInflater().inflate(R.menu.main, menu);
//        
    	MenuItem lastPage = menu.getItem(0);
        lastPage.setEnabled(1 != pageid);
//        
        MenuItem nextPage = menu.getItem(1);
        nextPage.setEnabled(13 != pageid);
//        
        MenuItem Hot = menu.getItem(2);
        Hot.setVisible(!isHot);
//        
        MenuItem New = menu.getItem(3);
        New.setVisible(isHot);
//        
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//    	Toast.makeText(mContext, "getItemId:"+item.getItemId()+" getOrder:"+item.getOrder(), Toast.LENGTH_SHORT).show();
    	switch (item.getOrder()) {
		case 100:
			pageid--;
			getFons();
			break;
		case 101:
			pageid++;
			getFons();
			break;
		case 102:
			isHot = true;
			getFons();
			break;
		case 103:
			isHot = false;
			getFons();
			break;
		case 104:
			getFons(true);
			break;
		case 105:
			Intent mIntent = new Intent(getApplicationContext(), HelpActivity.class);
			mIntent.putExtra("needShowHelp", true);
			startActivity(mIntent);
			break;
		default:
			break;
		}
//    	updateMenuUi();
		return super.onOptionsItemSelected(item);
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
				view = LayoutInflater.from(mContext).inflate(R.layout.font_gridview_item, null);
				mHandleView = new HandleView();
				mHandleView.fontImg = (ImageView)view.findViewById(R.id.font_pre);
				mHandleView.fontName = (TextView)view.findViewById(R.id.font_name);
				mHandleView.fontProgressBar = (ProgressBar) view.findViewById(R.id.font_progress);
				view.setTag(mHandleView);
			} else {
				mHandleView = (HandleView) view.getTag();
			}
			Font font = mFonts.get(index);
			log("show font : " + font.getFontDetailString());
			if(font.isDownLoading()){
				//update progressBar
			}
			mHandleView.fontName.setText(font.getFontName());
			
			imageLoader.displayImage(font.getFontImgURL(), mHandleView.fontImg, options);
			return view;
		}
    	
		class HandleView {
			ImageView fontImg ;
			TextView fontName ;
			ProgressBar fontProgressBar;
		}
    }
    
    
    private List<Font> mFonts = new ArrayList<Font>();
    private final int GET_FONT_URL_START = 100000;
    private final int GET_FONT_URL_END = 100001;
    private final int GET_FONT_URL_REFRSH = 100002;
    private final int DOWN_FONT_START = 100011;
    private final int DOWN_FONT_END = 100012;
    private final int DOWN_FONT_ERROR = 100013;
    private final int DOWN_FONT_OK = 100014;
    private final int UPDATE_DOWN_FONT_PROGRESSBAR = 100021;
    private final int SET_DETAIL_IMG = 100031;
    
    Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_FONT_URL_START:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case GET_FONT_URL_END:
				mProgressBar.setVisibility(View.GONE);
				mFontAdapter.notifyDataSetInvalidated();
				break;
			case GET_FONT_URL_REFRSH:
				mProgressBar.setVisibility(View.GONE);
				mFontAdapter.notifyDataSetChanged();
				break;
			case SET_DETAIL_IMG:
				imageLoader.displayImage(msg.obj.toString(),fontDetail, options);
				break;
			case UPDATE_DOWN_FONT_PROGRESSBAR:
				ProgressBar mBar = (ProgressBar) msg.obj;
				mBar.setProgress(msg.getData().getInt("size"));
				break;
			case DOWN_FONT_ERROR:
				Toast.makeText(mContext, getString(R.string.font_download_error), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
    };
    
	public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
		showAlertDialog(index,(Font)mFontAdapter.getItem(index));
	}
	
	private AlertDialog alert = null;
	private ImageView fontDetail = null;
	private Button mDown = null;
	private void showAlertDialog(int id,final Font font){
		if(null == alert){
			LayoutInflater flater = LayoutInflater.from(this);
			View view = flater.inflate(R.layout.font_detail_view, null);
			fontDetail = (ImageView) view.findViewById(R.id.font_detail_icon);
			mDown = (Button) view.findViewById(R.id.down);
			mDown.setOnClickListener(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext); 
			builder.setView(view);
			alert = builder.create(); 
		}
		mDown.setText(font.isDownLoading()?R.string.button_pres_download:R.string.button_start_download);
		mDown.setTag(HttpTools.getFontDownLoad(font));
		Runnable m = new Runnable() {
			@Override
			public void run() {
				String imgUrl = HttpTools.getFontDetailImgUrl(font);
				log( imgUrl);
				mHandler.sendMessage(Message.obtain(mHandler, SET_DETAIL_IMG, imgUrl));
			}
		};
		Thread mThread = new Thread(m);
		mThread.start();
		alert.show();
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.down){
			mHandler.sendMessage(new Message().obtain(mHandler,DOWN_FONT_START , 0, 0));
			if(alert.isShowing())alert.dismiss();
			Uri uri = Uri.parse(((Button)view).getTag().toString());  
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
            startActivity(intent); 
		}
	}
	
	private void log(String logs){
		Log.d(TAG,logs);
	}
	
	public boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	
	public boolean isWifiConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mWiFiNetworkInfo = mConnectivityManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        if (mWiFiNetworkInfo != null) {  
	            return mWiFiNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	
	public boolean isMobileConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mMobileNetworkInfo = mConnectivityManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        if (mMobileNetworkInfo != null) {  
	            return mMobileNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	private boolean isConnect = false;
	private void showConnectTip(){
		int tipId = R.string.connect_noconnect;
		switch (getConnectState()) {
		case 1:
			tipId = R.string.connect_wifi;
			isConnect = true;
			break;
		case 2:
			tipId = R.string.connect_2g_3g;
			isConnect = true;
			break;
		default:
			tipId = R.string.connect_noconnect;
			isConnect = false;
			break;
		}
		Toast.makeText(mContext, getResources().getString(tipId), Toast.LENGTH_SHORT).show();
		
	}
	
	private int getConnectState(){
		int state = 0;
		if (mContext != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        if (mNetworkInfo != null) {  
	            if(mNetworkInfo.isAvailable()) {
	            	state = 1;  
	            }
	            else {
	            	mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	            	if (mNetworkInfo != null) {  
	            		if(mNetworkInfo.isAvailable()) state = 2;;  
	            	}	            	
	            }
	        }  
		}
		return state;
	}
	
}
