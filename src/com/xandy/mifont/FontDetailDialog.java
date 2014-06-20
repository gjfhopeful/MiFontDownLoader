package com.xandy.mifont;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FontDetailDialog extends Dialog{
	
	private Context mContext;
	
	private ImageView mDetailImage;
	private Button mDownLoad;
	
	private Font mFont;
	
	private ImageLoader mImageLoader;
	
	private DisplayImageOptions mOptions;
	
	
	private static final int UPDATE_DETAIL_IMAGE = 100;
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_DETAIL_IMAGE:
//				mImageLoader.displayImage(mFont.getFontDetailImgUrl(), mDetailImage, mOptions);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};

	public FontDetailDialog(Context context, int theme) {
		super(context, theme);
		initView(context);
	}

	protected FontDetailDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initView(context);
	}

	public FontDetailDialog(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context){
		
		
		setCanceledOnTouchOutside(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mContext = context;
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View layout = mInflater.inflate(R.layout.font_detail_view, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		setContentView(layout,params);
		mDetailImage = (ImageView)findViewById(R.id.font_detail_image);
		mDownLoad = (Button)findViewById(R.id.font_down);
		mDownLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != mFont){
					Uri uri = Uri.parse(mFont.getFontDownLoadUrl());  
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
					mContext.startActivity(intent);
					dismiss();
				}
			}
		});
		
	}

	public Font getFont() {
		return mFont;
	}

	public void setFont(Font font) {
		this.mFont = font;
		new Thread(new Runnable() {
			@Override
			public void run() {
				mFont.refreshDetailImgUrl();
				mHandler.sendEmptyMessage(UPDATE_DETAIL_IMAGE);
			}
		}).start();
	}

	public ImageView getDetailImage() {
		return mDetailImage;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.mImageLoader = imageLoader;
	}

	public DisplayImageOptions getOptions() {
		return mOptions;
	}

	public void setOptions(DisplayImageOptions options) {
		this.mOptions = options;
	}

}
