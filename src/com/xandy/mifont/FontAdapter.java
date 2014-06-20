package com.xandy.mifont;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FontAdapter extends BaseAdapter{
	
	private Context mContext;
	
	private List<Font> mFonts;
	
	private ImageLoader mImageLoader;
	
	private DisplayImageOptions mOptions;
	
//	public FontAdapter(Context context){
//		mContext = context;
//		updateFonts();
//	}
	
	public FontAdapter(Context context ,ImageLoader loader ,DisplayImageOptions options){
		mContext = context;
		updateFonts();
		setImageLoader(loader);
		setDisplayImageOptions(options);
	}
	
	public void updateFonts(){
		mFonts = Font.getFonts();
	}
	
	public void setImageLoader(ImageLoader loader){
		mImageLoader = loader;
	}
	
	public void setDisplayImageOptions(DisplayImageOptions options){
		mOptions = options;
	}
	
	public void updateFontDatas(){
		
		Font.refreshFontData(Font.getFontURL(Font.getFonts().size()));
		updateFonts();
//		notifyDataSetChanged();
	}
	
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
		mHandleView.fontName.setText(font.getFontDetailName());
		Log.d("wang"," image " + index + " == "+ font.getFontCover());
		if(null != mImageLoader && null != mOptions ){
			mImageLoader.displayImage(font.getFontCover(), mHandleView.fontImg, mOptions);
		}
		return view;
	}
	
	class HandleView {
		ImageView fontImg ;
		TextView fontName ;
		ProgressBar fontProgressBar;
	}
}
