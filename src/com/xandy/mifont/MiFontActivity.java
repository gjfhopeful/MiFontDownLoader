package com.xandy.mifont;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MiFontActivity extends Activity{
	
	
	private Context mContext = null;
	
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private FontAdapter mAdapter;
	
	private ImageLoader mImageLoader = null;
	private DisplayImageOptions mOptions;
	
	private FontDetailDialog mDetailDialog;
	private Font mSelectFont;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		mContext = this;
		if(null == mImageLoader) mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

        mOptions = new DisplayImageOptions.Builder()
        	.showStubImage(R.drawable.ic_launcher)
        	.showImageForEmptyUri(R.drawable.ic_launcher)
        	.showImageOnFail(R.drawable.ic_launcher)
        	.cacheInMemory(true)
        	.cacheOnDisc(true)
        	.bitmapConfig(Bitmap.Config.RGB_565)
        	.build();
		
		
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		
		mPullRefreshGridView.setMode(Mode.PULL_FROM_END);
		

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						mAdapter.updateFontDatas();
						mHandler.sendEmptyMessage(UPDATE_FONT_DATA);
					}
				}).start();

			}

		});
		
		mGridView = mPullRefreshGridView.getRefreshableView();
		mAdapter = new FontAdapter(mContext, mImageLoader, mOptions);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,long id) {
				mSelectFont = (Font)mAdapter.getItem(index);
				new Thread(new Runnable() {
					@Override
					public void run() {
						mSelectFont.refreshDetailImgUrl();
						mHandler.sendEmptyMessage(UPDATE_FONT_DETAIL_IMAGE);
					}
				}).start();
				showFontDetail();
			}
		});
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAdapter.updateFontDatas();
				mHandler.sendEmptyMessage(UPDATE_FONT_DATA);
			}
		}).start();
	}
	
	
	private void showFontDetail(){
		if( null == mDetailDialog){
			mDetailDialog = new FontDetailDialog(mContext);
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mDetailDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			mDetailDialog.getWindow().setAttributes(lp); 
		}
		mDetailDialog.setFont(mSelectFont);
		mDetailDialog.show();
	}
	
	private static final int UPDATE_ERRPR = 100; 
	private static final int UPDATE_FONT_DATA = 101;
	private static final int UPDATE_FONT_DETAIL_IMAGE = 102;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UPDATE_ERRPR:
				break;
			case UPDATE_FONT_DATA:
				mAdapter.notifyDataSetChanged();
				mPullRefreshGridView.onRefreshComplete();
				if(Font.isEnd()){
					Toast.makeText(mContext, "is end", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "has update", Toast.LENGTH_SHORT).show();
				}
				break;
			case UPDATE_FONT_DETAIL_IMAGE:
				if(null != mDetailDialog){
					mImageLoader.displayImage(mSelectFont.getFontDetailImgUrl(),mDetailDialog.getDetailImage(), mOptions);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
}
