package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SelectCameraOverlayActivity extends Activity {
	private final String tag = SelectCameraOverlayActivity.class
			.getSimpleName();
	private Camera mCamera;
	private CameraPreview mPreview;
	private FrameLayout mFrameLayoutPreview;
	ImageView mImageViewOverlay;
	private int mNumberOfImages;
	private int mSelectedImageNumber;
	public ImageLoader mImageLoader;
	private ArrayList<String> mListOverlayUrls = new ArrayList<String>();
	private GridView gridview;
	private ImageView			focusView       = null;
	


	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlay);
		
		focusView = (ImageView) findViewById(R.id.imageFocusDot);
		focusView.setVisibility(View.INVISIBLE);
		
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

		mImageViewOverlay = (ImageView) findViewById(R.id.imageViewOverlay);
		mImageViewOverlay.getLayoutParams().width = screenWidth;
		mImageViewOverlay.getLayoutParams().height = screenWidth;
		
		mImageLoader = new ImageLoader(this);
		EventModel em = EventModel.getSelectedEvent();
		mNumberOfImages = em.getnNumberOfOverlay();

		if (mNumberOfImages >= 1)
			mListOverlayUrls.add(em.getsImageOverlay1());
		if (mNumberOfImages >= 2)
			mListOverlayUrls.add(em.getsImageOverlay2());
		if (mNumberOfImages >= 3)
			mListOverlayUrls.add(em.getsImageOverlay3());
		if (mNumberOfImages >= 4)
			mListOverlayUrls.add(em.getsImageOverlay4());
		if (mNumberOfImages >= 5)
			mListOverlayUrls.add(em.getsImageOverlay5());
		// getWindow().setFormat(PixelFormat.UNKNOWN);

		// Create our Preview view and set it as the content of our activity.
		mFrameLayoutPreview = (FrameLayout) findViewById(R.id.camera_preview);

		mPreview = new CameraPreview(this);
		mCamera = mPreview.getCamera();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		
		Parameters parameters = mCamera.getParameters();
		Camera.Size minPreviewSize = parameters.getPictureSize();
		
		params.width = screenWidth;
		params.height = (int)((float)params.width * (float)minPreviewSize.width / (float)minPreviewSize.height);
		params.gravity = Gravity.TOP;
		
		mFrameLayoutPreview.addView(mPreview, params);
		
		//Set OverlayImage GridView Parameter
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mNormalSizePx = (int)Math.floor(100 *metrics.scaledDensity);
		int mSizePx = mNormalSizePx;
		int mSpacingPx = (int)Math.floor(3 *metrics.scaledDensity);
		int mPaddingPx = (int)Math.floor(10 *metrics.scaledDensity);
		
		gridview = (GridView) findViewById(R.id.gridViewOverlay);
		int columnCount = mNumberOfImages;
		if (columnCount > 0)
		{
			Rect p = new Rect();
			gridview.getSelector().getPadding(p);
			int selectorPadding = p.left + p.right;
			
			mSizePx = (metrics.widthPixels - (mPaddingPx * 2 + mSpacingPx * (columnCount - 1) + selectorPadding)) / columnCount;
			if (mSizePx > mNormalSizePx)
				mSizePx = mNormalSizePx;

			int contentWidth = columnCount * mSizePx;
			contentWidth += (columnCount - 1) * mSpacingPx;
			contentWidth += selectorPadding;
			
			int slack = metrics.widthPixels - contentWidth;
			gridview.setPadding(slack / 2, (int)Math.floor(5 *metrics.scaledDensity), slack / 2, (int)Math.floor(5 *metrics.scaledDensity) );
		}
		gridview.setHorizontalSpacing(mSpacingPx);
		gridview.setColumnWidth(mSizePx);
		
		gridview.setNumColumns(columnCount);
		gridview.setAdapter(new ImageAdapter(this, mListOverlayUrls, mSizePx));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// mImageViewOverlay.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));
				
				Log.e(tag, "gridView.getChildCount = " + gridview.getChildCount());
				
				mSelectedImageNumber = position;
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
				
				mImageLoader.displayImage(mListOverlayUrls.get(position),
						mImageViewOverlay);
			}
		});

		final Button buttonFlash = (Button) findViewById(R.id.buttonFlash);
		buttonFlash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int n = mPreview.toggleFlash();
				if (n == -1)
					return;
				buttonFlash
						.setBackgroundResource(n == 0 ? R.drawable.takephoto_flashon
								: R.drawable.takephoto_flashoff);
			}
		});

		final Button buttonToggleCamera = (Button) findViewById(R.id.buttonToggleCamera);
		buttonToggleCamera.setOnClickListener(new OnClickListener() {
			boolean bBackCamera = false;

			@Override
			public void onClick(View view) {
				final int n = bBackCamera ? 0 : 1;
				if (n == 0) {
					buttonToggleCamera
							.setBackgroundResource(R.drawable.takephoto_switchcam1);
				} else {
					buttonToggleCamera
							.setBackgroundResource(R.drawable.takephoto_switchcam);
				}
				mPreview.toggleCamera(n);
				SharedImageObjects.mSelectedCamera = n;
				bBackCamera = !bBackCamera;
				mCamera = mPreview.getCamera();
				buttonFlash
						.setBackgroundResource(R.drawable.takephoto_flashoff);
			}
		});

		final Button buttonCapture = (Button) findViewById(R.id.buttonCapture);
		buttonCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mCamera != null)
					mCamera.takePicture(cameraShutterCallback,
							cameraPictureCallbackRaw, cameraPictureCallbackJpeg);
			}
		});

		final Button buttonBack = (Button) findViewById(R.id.buttonSelectOverlayBack3);
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		mImageLoader.displayImage(em.getsImageOverlay1(), mImageViewOverlay);
		gridview.performItemClick(null, 0, 0);
		
		/*
		FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mPreview
				.getLayoutParams();
		params.height = getScreenWidth();
		params.width = getScreenWidth();
		mPreview.setLayoutParams(params);
		*/
	}

	private int getScreenWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	ShutterCallback cameraShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback cameraPictureCallbackRaw = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback cameraPictureCallbackJpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap capturedBitmap = Utils
					.rotateBitmap(
							BitmapFactory.decodeByteArray(data, 0, data.length),
							SharedImageObjects.mSelectedCamera == CameraInfo.CAMERA_FACING_BACK ? 90 : 270);

			SharedImageObjects.mSelectedImageUrl = mListOverlayUrls
					.get(mSelectedImageNumber);
			
			Context context = getBaseContext();// getApplicationContext();
			Utils.savePicture("capturedImage.png", capturedBitmap, context);
			
			Intent intent = new Intent(SelectCameraOverlayActivity.this,
					ImageEffectActivity.class);
			startActivity(intent);
			finish();
		}
	};
}