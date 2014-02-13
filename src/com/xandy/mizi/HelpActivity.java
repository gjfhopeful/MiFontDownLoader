package com.xandy.mizi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		mHelps = new ArrayList<ImageView>();
		for(int i = 0 ; i < helpSrcId.length ; i++){
			ImageView img = new ImageView(this);
			img.setImageResource(helpSrcId[i]);
			mHelps.add(img);
		}
		
		mViewPager = (ViewPager) findViewById(R.id.help);
		mViewPager.setAdapter(new MPageAdapter());
		mCheckBox = (CheckBox) findViewById(R.id.notshow);
		mButton = (Button) findViewById(R.id.next);
		mButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
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
	    	mCheckBox.setVisibility( position == mHelps.size() ? View.VISIBLE : View.GONE);
	    	return mHelps.get(position);  
	 	}  

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
