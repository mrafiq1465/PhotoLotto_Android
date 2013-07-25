package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectEffectActivity extends Activity {
	
	private final static int 	FILTER_NO_ONE	= 0;
	private final static int 	FILTER_NO_TWO	= 1;
	private final static int 	FILTER_NO_THREE	= 2;
	private final static int 	FILTER_NO_FOUR	= 3;
	private final static int 	FILTER_NO_FIVE	= 4;
	private final static int 	FILTER_NO_SIX	= 5;
	private final static int 	FILTER_TOTAL	= 6;
	protected static final String	t	= null;
	
	private ImageView			focusView       = null;
	private GridView			gridview		= null;
	private final String		tag				= this.getClass().getSimpleName();
	private TextView			textViewEffectName;
	private ImageView			imageView;
	private ImageEffectAdapter	imageEffectAdapter;
	ImageLoader					mImageLoader;
	private int					nSelectedItem	= 0;
	
	private Bitmap				bitmapIn		= null;
	private Bitmap				bitmapOut		= null;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {

		for (int i = 0; i < FILTER_TOTAL; i++) {
			if (i == nSelectedItem)
				continue;
			Bitmap bmp = SharedImageListObjects.mEffectImages.get(i);
			bmp.recycle();
			bmp = null;
		}
		SharedImageListObjects.mEffectImages.clear();
		SharedImageListObjects.mTempImage.recycle();
		SharedImageListObjects.mTempImage = null;
		
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effects);
		
		focusView = (ImageView) findViewById(R.id.imageFocusDot);
		focusView.setVisibility(View.INVISIBLE);
		
		imageView = (ImageView) findViewById(R.id.full_image_view);
		textViewEffectName = (TextView) findViewById(R.id.textViewEffectName);
		textViewEffectName.setText("Normal");

		imageView.setImageBitmap(SharedImageListObjects.mTempImage);
		
		gridview = (GridView) findViewById(R.id.gridViewEffects);
		gridview.setGravity(Gravity.CENTER);
		gridview.setNumColumns(FILTER_TOTAL);
		imageEffectAdapter = new ImageEffectAdapter(this, SharedImageListObjects.mEffectImages);
		gridview.setAdapter(imageEffectAdapter);
//		imageEffectAdapter.notifyDataSetChanged();
		
		mImageLoader = new ImageLoader(this);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				bitmapOut = SharedImageListObjects.mEffectImages.get(position);
				int h = bitmapOut.getHeight();
				int w = bitmapOut.getWidth();
				Canvas canvas = new Canvas(bitmapOut);
				canvas.drawBitmap(bitmapOut, 0f, 0f, null);
				Drawable drawable = new BitmapDrawable(getResources(), mImageLoader.getBitmap(SharedImageObjects.mSelectedImageUrl));
				drawable.setBounds(0, 0, w, h);
				drawable.draw(canvas);
				imageView.setImageBitmap(bitmapOut);
				
				nSelectedItem = position;
				for (int i = 0; i < gridview.getChildCount(); i++) {
					LinearLayout selImageView = (LinearLayout) gridview.getChildAt(i);
					if (i == position) {
						selImageView.getChildAt(0).setBackgroundColor(0xff00cece);
						ImageView imageView = (ImageView)(selImageView.getChildAt(1));
						imageView.setImageResource(R.drawable.filter_dot_selected);
					} else {
						selImageView.getChildAt(0).setBackgroundColor(0x00000000);
						ImageView imageView = (ImageView)(selImageView.getChildAt(1));
						imageView.setImageResource(R.drawable.filter_dot_normal);
					}
				}
				
				switch (position) {
					case FILTER_NO_ONE :
						textViewEffectName.setText("Normal");
						break;
					case FILTER_NO_TWO :
						textViewEffectName.setText("Sydney");
						break;
					case FILTER_NO_THREE :
						textViewEffectName.setText("London");
						break;
					case FILTER_NO_FOUR :
						textViewEffectName.setText("New York");
						break;
					case FILTER_NO_FIVE :
						textViewEffectName.setText("Paris");
						break;
					case FILTER_NO_SIX :
						textViewEffectName.setText("Tokyo");
						break;
					default :
						break;
				}
			}
		});
		Button button = (Button) findViewById(R.id.buttonShareDone);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedImageObjects.mBitmapWithEffect = (bitmapOut == null) ? bitmapIn : bitmapOut;
				Intent intent = new Intent(SelectEffectActivity.this, ShareActivity.class);
				startActivity(intent);
//				SelectEffectActivity.this.finish();
			}
		});

		Button buttonBack = (Button) findViewById(R.id.buttonShareBack);
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectEffectActivity.this.onBackPressed();
			}
		});
		
		gridview.performItemClick(null, FILTER_NO_SIX, 0);
		gridview.performItemClick(null, FILTER_NO_FIVE, 0);
		gridview.performItemClick(null, FILTER_NO_FOUR, 0);
		gridview.performItemClick(null, FILTER_NO_THREE, 0);
		gridview.performItemClick(null, FILTER_NO_TWO, 0);
		gridview.performItemClick(null, FILTER_NO_ONE, 0);
	}	
}
