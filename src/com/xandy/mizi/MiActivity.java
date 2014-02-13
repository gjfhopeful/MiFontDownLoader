package com.xandy.mizi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xandy.mizi.network.DownloadProgressListener;
import com.xandy.mizi.network.FileDownloader;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MiActivity extends Activity implements OnItemClickListener,OnClickListener{
	
	private boolean DEBUG = true;
	private String TAG = "MiActivity";
	
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
        
        Thread httpThread = new Thread(new Runnable() {
        	@Override
        	public void run() {
        		log("start get font grid data");
        		mFonts.clear();
        		mFonts = HttpTools.getFonts(1, true);
        		mHandler.sendEmptyMessage(GET_FONT_URL);
        	}
        });
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
				download(mHandleView.fontProgressBar,
						HttpTools.getFontDownLoad(font),
						Environment.getExternalStorageDirectory()+HttpTools.DownLoadDir);
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
    private final int GET_FONT_URL = 100000;
    private final int DOWN_FONT_START = 100001;
    private final int DOWN_FONT_END = 100002;
    private final int DOWN_FONT_ERROR = 100003;
    private final int DOWN_FONT_OK = 100004;
    private final int SET_DETAIL_IMG = 100005;
    private final int UPDATE_PROGRESSBAR = 100006;
    
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
			case UPDATE_PROGRESSBAR:
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
//		mDown.setTag(id);
		mDown.setTag(HttpTools.getFontDownLoad(font));
//		mDown.setHint(HttpTools.getFontDownLoad(font));
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
//			int id = (Integer) ((Button)view).getTag();
//			Font font = mFonts.get(id);
//			font.setDownLoading(true);
//			mFonts.set(id, font);
//			mFontAdapter.notifyDataSetChanged();
		}
	}
    
	/**
  	 * 主线程(UI线程)
  	 * 对于显示控件的界面更新只是由UI线程负责，如果是在非UI线程更新控件的属性值，更新后的显示界面不会反映到屏幕上
  	 * 如果想让更新后的显示界面反映到屏幕上，需要用Handler设置。
  	 * @param path
  	 * @param savedir
  	 */
	private void download(final ProgressBar progressBar,final String path, final String savedir) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				log("Strat downLoad " + path);
				//开启3个线程进行下载
				File mFile = new File(savedir);
				if(!mFile.exists()) mFile.mkdirs();
				FileDownloader loader = new FileDownloader(mContext, path, new File(savedir), 3);
				progressBar.setMax(loader.getFileSize());//设置进度条的最大刻度为文件的长度
				final Message msg = new Message();
				msg.obj = progressBar;
				try {
					loader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
							msg.what = UPDATE_PROGRESSBAR;
							msg.getData().putInt("size", size);
							mHandler.sendMessage(msg);//发送消息
						}
					});
				} catch (Exception e) {
					msg.what = DOWN_FONT_ERROR;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private void log(String logs){
		Log.d(TAG,logs);
	}
	
}
