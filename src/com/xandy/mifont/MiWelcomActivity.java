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

public class MiWelcomActivity extends Activity implements OnClickListener,OnPageChangeListener{
	
	private ViewPager mHelpViewPager ;
	private CheckBox mNeverShow ;
	private Button mEnter ;
	private List<ImageView> mHelpImages;
	private int[] helpSrcId = {
		R.drawable.h1,	
		R.drawable.h2,
		R.drawable.h3,
		R.drawable.h4,
		R.drawable.h5,
		R.drawable.h6
	};
	
//	image points  
    private ImageView[] mHelpPoints;   
//  navi layout viewgroup  
    private LinearLayout mHelpPointLayout;  
    private String[] mHelpStrings;
    
    private static final String NEED_SHOW_HELP = "need_show_help";
    private boolean isNeedShowHelp = false;
    
    private static final String NEVER_SHOW_HELP = "neever_show_help";
    private boolean isNeverShowHelp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		Intent data = getIntent();
		isNeedShowHelp = data.getBooleanExtra(NEED_SHOW_HELP, false);
		
		isNeverShowHelp = isShouldShowHelp();		
		
		if( isNeedShowHelp || !isNeverShowHelp ){
			initView();
		}
		else {
			//go to main activity
			goToMainActivity();
		}
	}
	
	private void initView(){
		mHelpImages = new ArrayList<ImageView>();
		mHelpPoints = new ImageView[helpSrcId.length];	
		mHelpPointLayout = (LinearLayout) findViewById(R.id.help_point_tips);
		mHelpStrings = getResources().getStringArray(R.array.help_string);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(10, 10);
		mLayoutParams.setMargins(2, 0, 2, 0);
		for(int i = 0 ; i < helpSrcId.length ; i++){
			ImageView img = new ImageView(this);
			img.setImageResource(helpSrcId[i]);
			mHelpImages.add(img);
			ImageView point = new ImageView(this);  
			point.setLayoutParams(mLayoutParams);  
			point.setPadding(5, 0, 5, 0);
			point.setBackgroundResource((R.drawable.circle_ucimg));
			if( 0 == i) point.setBackgroundResource((R.drawable.circle_cimg));
            mHelpPoints[i] = point;
            mHelpPointLayout.addView(mHelpPoints[i]);
		}
		
		mHelpViewPager = (ViewPager) findViewById(R.id.help_image_tips);
		mHelpViewPager.setAdapter(new HelpImageAdapter());
		mHelpViewPager.setOnPageChangeListener(this);
		mNeverShow = (CheckBox) findViewById(R.id.help_nevershow);
		mNeverShow.setOnClickListener(this);
		mEnter = (Button) findViewById(R.id.help_enter);
		mEnter.setOnClickListener(this);
		int selectHelpImage = 0;
		mHelpViewPager.setCurrentItem(selectHelpImage);
		updateHelpPointUI(selectHelpImage);
	}
	
	class HelpImageAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return mHelpImages.size();
		}
		
		@Override  
        public void destroyItem(ViewGroup container, int position, Object object)   {     
            container.removeView(mHelpImages.get(position));//删除页卡  
        }  
		
		@Override  
		public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
	    	container.addView(mHelpImages.get(position), 0);//添加页卡  
	    	return mHelpImages.get(position);  
	 	}  

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}
	}
	
	private boolean isShouldShowHelp(){
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
		boolean shouldShowHelp = mSharedPreferences.getBoolean(NEVER_SHOW_HELP, false);
		return shouldShowHelp;
	}
	
	private void saveCheckState(){
		SharedPreferences mSharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(NEVER_SHOW_HELP, mNeverShow.isChecked());
		mEditor.commit();
	}
	
	private void goToMainActivity(){
		finish();
//		Intent mIntent = new Intent(getApplicationContext(), MiActivity.class);
		Intent mIntent = new Intent(getApplicationContext(), MiFontActivity.class);
		startActivity(mIntent);		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.help_enter:
			goToMainActivity();
			break;
		case R.id.help_nevershow:
			saveCheckState();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int page) {
		
	}

	@Override
	public void onPageScrolled(int oldPage, float arg1, int newPage) {
		
	}

	@Override
	public void onPageSelected(int position) {
		updateHelpPointUI(position);
	}
	
	public void updateHelpPointUI(int position){
		mNeverShow.setVisibility( position == mHelpImages.size()-1 ? View.VISIBLE : View.GONE);
		mEnter.setVisibility( position == mHelpImages.size()-1 ? View.VISIBLE : View.GONE);
		Toast toast = Toast.makeText(getApplicationContext(), mHelpStrings[position], Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP , 0, 250);
		toast.show();
		for (int i = 0; i < mHelpImages.size(); i++) {  
			ImageView img = mHelpPoints[i];
			img.setBackgroundResource((R.drawable.circle_ucimg));
			if( position == i) img.setBackgroundResource((R.drawable.circle_cimg));
		}
		mHelpPointLayout.setVisibility( position == mHelpImages.size()-1 ? View.INVISIBLE : View.VISIBLE);
		
	}

}
