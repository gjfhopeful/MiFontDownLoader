package com.xandy.mifont;

import java.util.ArrayList;
import java.util.List;

import com.xandy.mifont.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HelpActivity extends Activity implements OnClickListener,OnPageChangeListener{
	
	private ViewPager mViewPager ;
	private CheckBox mCheckBox ;
	private Button mButton ;
	private List<ImageView> mHelps;
	private int[] helpSrcId = {
		R.drawable.h1,	
		R.drawable.h2,
		R.drawable.h3,
		R.drawable.h4,
		R.drawable.h5,
		R.drawable.h6
	};
	
//	image points  
    private ImageView[] imageViews;   
//  navi layout viewgroup  
    private LinearLayout viewPoints;  
    private String[] mHelpTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		Intent data = getIntent();
		boolean needShowHelp = data.getBooleanExtra("needShowHelp", false);
		if(!needShowHelp) isShowHelp();
		
		
		mHelps = new ArrayList<ImageView>();
		imageViews = new ImageView[helpSrcId.length];	
		viewPoints = (LinearLayout) findViewById(R.id.viewPoints);
		mHelpTips = getResources().getStringArray(R.array.help_string);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(10, 10);
		mLayoutParams.setMargins(2, 0, 2, 0);
		for(int i = 0 ; i < helpSrcId.length ; i++){
			ImageView img = new ImageView(this);
			img.setImageResource(helpSrcId[i]);
			mHelps.add(img);
			
			ImageView point = new ImageView(this);  
			point.setLayoutParams(mLayoutParams);  
			point.setPadding(5, 0, 5, 0);
			point.setBackgroundResource((R.drawable.circle_ucimg));
			if( 0 == i) point.setBackgroundResource((R.drawable.circle_cimg));
            imageViews[i] = point;
            viewPoints.addView(imageViews[i]);
		}
		
		mViewPager = (ViewPager) findViewById(R.id.help);
		mViewPager.setAdapter(new MPageAdapter());
		mViewPager.setOnPageChangeListener(this);
		mCheckBox = (CheckBox) findViewById(R.id.nervershow);
		mCheckBox.setOnClickListener(this);
		mButton = (Button) findViewById(R.id.next);
		mButton.setOnClickListener(this);
		
		mViewPager.setCurrentItem(0);
		updateUI(0);
	}
	
	class MPageAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return mHelps.size();
		}
		
		@Override  
        public void destroyItem(ViewGroup container, int position, Object object)   {     
            container.removeView(mHelps.get(position));//删除页卡  
        }  
		
		@Override  
		public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
	    	container.addView(mHelps.get(position), 0);//添加页卡  
	    	return mHelps.get(position);  
	 	}  

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}
	
	private void isShowHelp(){
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
		boolean neverShowHelp = mSharedPreferences.getBoolean("nerverShowHelp", false);
		if(neverShowHelp){
			Intent mIntent = new Intent(getApplicationContext(), MiActivity.class);
			startActivity(mIntent);
			finish();
		}
	}
	
	private void saveCheckState(){
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("nerverShowHelp", mCheckBox.isChecked());
//		Toast toast = Toast.makeText(getApplicationContext(), mCheckBox.isChecked()+"", Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.TOP , 0, 250);
//		toast.show();
		mEditor.commit();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.next:
			finish();
			Intent mIntent = new Intent(getApplicationContext(), MiActivity.class);
			startActivity(mIntent);
			break;
		case R.id.nervershow:
			saveCheckState();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		updateUI(position);
	}
	
	public void updateUI(int position){
		mCheckBox.setVisibility( position == mHelps.size()-1 ? View.VISIBLE : View.GONE);
		mButton.setVisibility( position == mHelps.size()-1 ? View.VISIBLE : View.GONE);
		Toast toast = Toast.makeText(getApplicationContext(), mHelpTips[position], Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP , 0, 250);
		toast.show();
		for (int i = 0; i < mHelps.size(); i++) {  
			ImageView img = imageViews[i];
			img.setBackgroundResource((R.drawable.circle_ucimg));
			if( position == i) img.setBackgroundResource((R.drawable.circle_cimg));
		}
		viewPoints.setVisibility( position == mHelps.size()-1 ? View.INVISIBLE : View.VISIBLE);
		
	}

}
