package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ImageAdapter extends BaseAdapter {
	private final int			ITEM_WIDTH = 100;
	
	private Activity			mActivity;
	private ImageLoader			mImageLoader;
	private ArrayList<String>	mListOverlayUrls	= new ArrayList<String>();
	private int					mItemWidth = ITEM_WIDTH;
	private boolean bfisrt = true;
	
	public ImageAdapter(Activity a, ArrayList<String> lstOverlayImages, int itemWidth) {
		mActivity = a;
		this.mListOverlayUrls = lstOverlayImages;
		mImageLoader = new ImageLoader(mActivity.getApplicationContext());
		mItemWidth = itemWidth;
		bfisrt = true;
	}

	public int getCount() {
		return mListOverlayUrls.size();
	}

	public Object getItem(int position) {
		if (position>=mListOverlayUrls.size())
			position = 0;
		return mListOverlayUrls.get(position);
	}

	public long getItemId(int position) {
		if (position>=mListOverlayUrls.size())
			position = 0;
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position>=mListOverlayUrls.size())
			position = 0;
		
		LinearLayout linearlayout;
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			FrameLayout layout = new FrameLayout(mActivity);
			layout.setLayoutParams(new GridView.LayoutParams(mItemWidth, mItemWidth));
			int padding1 = mItemWidth * 5 / ITEM_WIDTH; 
			layout.setPadding(padding1, padding1, padding1, padding1);
			
			ImageView blackView = new ImageView(mActivity);
			blackView.setBackgroundResource(R.drawable.black_thumb_image);
			
			imageView = new ImageView(mActivity);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setTag(position + 1);
			
			layout.addView(blackView);
			layout.addView(imageView);
			
			ImageView indicatorView = new ImageView(mActivity);
			indicatorView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			indicatorView.setPadding(padding1, padding1, padding1, padding1);
			indicatorView.setImageResource(R.drawable.filter_dot_normal);
			indicatorView.setScaleType(ScaleType.CENTER_INSIDE);
			
			linearlayout = new LinearLayout(mActivity);
			int padding3 =  mItemWidth * 130 / ITEM_WIDTH; 
			linearlayout.setLayoutParams(new GridView.LayoutParams(mItemWidth, padding3));
			linearlayout.setOrientation(LinearLayout.VERTICAL);
			
			linearlayout.addView(layout);
			linearlayout.addView(indicatorView);
		} else {
			linearlayout = (LinearLayout) convertView;
			imageView = (ImageView) linearlayout.findViewWithTag(position + 1);
		}
		//imageView.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));
		mImageLoader.displayImage(mListOverlayUrls.get(position), imageView);
		
		if (bfisrt && position == 0)
		{
			linearlayout.getChildAt(0).setBackgroundColor(0xff00cece);
			ImageView imageView1 = (ImageView)(linearlayout.getChildAt(1));
			imageView1.setImageResource(R.drawable.filter_dot_selected);
			bfisrt = false;
		}
		
		return linearlayout;
	}
}
