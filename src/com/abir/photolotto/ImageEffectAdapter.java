package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ImageEffectAdapter extends BaseAdapter {

	private Activity activity;
	public ImageLoader imageLoader;
	private ArrayList<Bitmap> lstImageEffect = new ArrayList<Bitmap>();
	private boolean bfisrt = true;
	
	public ImageEffectAdapter(Activity a, ArrayList<Bitmap> lstImages) {
		activity = a;
		this.lstImageEffect = lstImages;
		bfisrt = true;
	}

	@Override
	public int getCount() {
		return lstImageEffect.size();
	}

	@Override
	public Object getItem(int position) {
		return lstImageEffect.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		ImageView imageView;
//		if (convertView == null) { // if it's not recycled, initialize some
//									// attributes
//			imageView = new ImageView(activity);
//			imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
//			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//			imageView.setPadding(5, 5, 5, 5);
//		} else {
//			imageView = (ImageView) convertView;
//		}
//		imageView.setTag(position);
//		imageView.setImageBitmap(lstImageEffect.get(position));
//		Log.d("pointer null", ":" + position+ (int)imageView.getId());
//		return imageView;
//	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout linearlayout;
		FrameLayout frameLayout;
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			frameLayout = new FrameLayout(activity);
			frameLayout.setLayoutParams(new GridView.LayoutParams(100, 100));
			frameLayout.setPadding(5, 5, 5, 5);
			
			ImageView blackView = new ImageView(activity);
			blackView.setBackgroundResource(R.drawable.black_thumb_image);
			
			imageView = new ImageView(activity);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setTag(position + 1);
			
			frameLayout.addView(blackView);
			frameLayout.addView(imageView);
			
			ImageView indicatorView = new ImageView(activity);
			indicatorView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			indicatorView.setPadding(5, 15, 5, 5);
			indicatorView.setImageResource(R.drawable.filter_dot_normal);
			indicatorView.setScaleType(ScaleType.CENTER_INSIDE);
			
			linearlayout = new LinearLayout(activity);
			linearlayout.setLayoutParams(new GridView.LayoutParams(100, 140));
			linearlayout.setOrientation(LinearLayout.VERTICAL);
			
			linearlayout.addView(frameLayout);
			linearlayout.addView(indicatorView);
		} else {
			linearlayout = (LinearLayout) convertView;
			imageView = (ImageView)(linearlayout.findViewWithTag(position + 1));
		}		
		//imageView.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));
		
		imageView.setImageBitmap(lstImageEffect.get(position));
		
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
