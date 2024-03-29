package com.abir.photolotto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
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

public class SelectCameraOverlayActivity extends BaseActivity {
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
	private ImageView focusView = null;
	private Context context;
	private ProgressDialog pd;

//	static {
//		System.loadLibrary("JniTest");
//	}

//	public native Bitmap rotateBitmapCcw90(Bitmap bitmap);
//
//	public native Bitmap rotateBitmapcw90(Bitmap bitmap);

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// startActivity(new Intent(SelectCameraOverlayActivity.this,
		// SelectEventActivity.class));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlay);

		context = SelectCameraOverlayActivity.this;

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
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		Parameters parameters = mCamera.getParameters();
		Camera.Size minPreviewSize = parameters.getPictureSize();
		PackageManager pm = context.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
			List<String> supportedFocusModes = parameters
					.getSupportedFocusModes();
			if (supportedFocusModes
					.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				parameters
						.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			} else if (supportedFocusModes
					.contains(Camera.Parameters.FOCUS_MODE_MACRO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
			} else if (supportedFocusModes
					.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			mCamera.setParameters(parameters);
		} else {
			Log.i("TAG", "Focus Mode :: Does not have autofocus.......");
		}
		// camera.startPreview();

		if (mCamera.getParameters().getFocusMode()
				.equals(Camera.Parameters.FOCUS_MODE_MACRO)
				|| mCamera.getParameters().getFocusMode()
						.equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					Log.i("TAG", "Auto Focus Completed :: " + success);
				}
			});
		}

		params.width = screenWidth;
		params.height = (int) ((float) params.width
				* (float) minPreviewSize.width / (float) minPreviewSize.height);
		params.gravity = Gravity.TOP;

		mFrameLayoutPreview.addView(mPreview, params);

		// Set OverlayImage GridView Parameter
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mNormalSizePx = (int) Math.floor(100 * metrics.scaledDensity);
		int mSizePx = mNormalSizePx;
		int mSpacingPx = (int) Math.floor(3 * metrics.scaledDensity);
		int mPaddingPx = (int) Math.floor(10 * metrics.scaledDensity);

		gridview = (GridView) findViewById(R.id.gridViewOverlay);
		int columnCount = mNumberOfImages;
		if (columnCount > 0) {
			Rect p = new Rect();
			gridview.getSelector().getPadding(p);
			int selectorPadding = p.left + p.right;

			mSizePx = (metrics.widthPixels - (mPaddingPx * 2 + mSpacingPx
					* (columnCount - 1) + selectorPadding))
					/ columnCount;
			if (mSizePx > mNormalSizePx)
				mSizePx = mNormalSizePx;

			int contentWidth = columnCount * mSizePx;
			contentWidth += (columnCount - 1) * mSpacingPx;
			contentWidth += selectorPadding;

			int slack = metrics.widthPixels - contentWidth;
			gridview.setPadding(slack / 2,
					(int) Math.floor(5 * metrics.scaledDensity), slack / 2,
					(int) Math.floor(5 * metrics.scaledDensity));
		}
		gridview.setHorizontalSpacing(mSpacingPx);
		gridview.setColumnWidth(mSizePx);
		gridview.setNumColumns(columnCount);
		// if ((!Utils.isOnline(SelectCameraOverlayActivity.this) &&
		// PreferenceManager
		// .getDefaultSharedPreferences(SelectCameraOverlayActivity.this)
		// .getBoolean(Constants.ISDOWNLOADEDALLIMAGES, false))
		// || Utils.isOnline(SelectCameraOverlayActivity.this)) {

		gridview.setAdapter(new ImageAdapter(this, mListOverlayUrls, mSizePx));
		// } else {
		// Utils.showNetworkAlert(SelectCameraOverlayActivity.this);
		// }
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// mImageViewOverlay.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));

				Log.e(tag,
						"gridView.getChildCount = " + gridview.getChildCount());

				mSelectedImageNumber = position;
				for (int i = 0; i < gridview.getChildCount(); i++) {
					LinearLayout selImageView = (LinearLayout) gridview
							.getChildAt(i);
					if (i == position) {
						selImageView.getChildAt(0).setBackgroundColor(
								0xff00cece);
						ImageView imageView = (ImageView) (selImageView
								.getChildAt(1));
						imageView
								.setImageResource(R.drawable.filter_dot_selected);
					} else {
						selImageView.getChildAt(0).setBackgroundColor(
								0x00000000);
						ImageView imageView = (ImageView) (selImageView
								.getChildAt(1));
						imageView
								.setImageResource(R.drawable.filter_dot_normal);
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

		mFrameLayoutPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isAutoFocusSupport = getPackageManager()
						.hasSystemFeature("android.hardware.camera.autofocus");
				if (isAutoFocusSupport) {

					mCamera.getParameters().setFocusMode("macro");
					mCamera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {

						}
					});
				}
			}
		});
		final Button buttonCapture = (Button) findViewById(R.id.buttonCapture);
		buttonCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mCamera != null) {
					// AutoFocusManager afm = new AutoFocusManager(context,
					// mCamera);
					// afm.onAutoFocus(success, theCamera)

					boolean isAutoFocusSupport = getPackageManager()
							.hasSystemFeature(
									"android.hardware.camera.autofocus");
					if (isAutoFocusSupport) {
						// Camera.AutoFocusMoveCallback
						// mCamera.setParameters("macro");
						mCamera.getParameters().setFocusMode("macro");
						mCamera.autoFocus(new AutoFocusCallback() {
							@Override
							public void onAutoFocus(boolean success,
									Camera camera) {
								if (success) {
									camera.takePicture(cameraShutterCallback,
											cameraPictureCallbackRaw,
											cameraPictureCallbackJpeg);
								} else {
									mCamera.takePicture(cameraShutterCallback,
											cameraPictureCallbackRaw,
											cameraPictureCallbackJpeg);
								}
							}
						});
					} else {
						mCamera.takePicture(cameraShutterCallback,
								cameraPictureCallbackRaw,
								cameraPictureCallbackJpeg);
					}
					// mCamera.setAutoFocusMoveCallback(new
					// AutoFocusMoveCallback() {
					//
					// @Override
					// public void onAutoFocusMoving(boolean start,
					// Camera camera) {
					// camera.takePicture(cameraShutterCallback,
					// cameraPictureCallbackRaw,
					// cameraPictureCallbackJpeg);
					// }
					// });

				}
			}
		});

		final Button buttonBack = (Button) findViewById(R.id.buttonSelectOverlayBack3);
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		Button buttonAddFromGalary = (Button) findViewById(R.id.buttonAddFromGalary);
		buttonAddFromGalary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent pickMedia = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				pickMedia.setType("image/*");
				startActivityForResult(pickMedia, 1);
			}
		});
		mImageLoader.displayImage(em.getsImageOverlay1(), mImageViewOverlay);
		gridview.performItemClick(null, 0, 0);

		/*
		 * FrameLayout.LayoutParams params =
		 * (android.widget.FrameLayout.LayoutParams) mPreview
		 * .getLayoutParams(); params.height = getScreenWidth(); params.width =
		 * getScreenWidth(); mPreview.setLayoutParams(params);
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri selectedImageUri = data.getData();
				String tempPath = getPath(selectedImageUri);
				if (tempPath != null) {
					Bitmap capturedBitmap = BitmapFactory.decodeFile(tempPath);

					SharedImageObjects.mSelectedImageUrl = mListOverlayUrls
							.get(mSelectedImageNumber);

					Context context = getBaseContext();// getApplicationContext();
					Utils.savePicture("capturedImage.png", capturedBitmap,
							context);

					Intent intent = new Intent(
							SelectCameraOverlayActivity.this,
							ImageEffectActivity.class);
					startActivity(intent);
					finish();
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	private String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// private int getScreenWidth() {
	// DisplayMetrics displaymetrics = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	// return displaymetrics.widthPixels;
	// }

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

	protected void onResume() {
		super.onResume();
		// if (SharedImageListObjects.mTempImage != null) {
		// SharedImageListObjects.mTempImage.recycle();
		// SharedImageListObjects.mEffectImages.clear();
		// }

	};

	PictureCallback cameraPictureCallbackJpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap original = BitmapFactory.decodeByteArray(data, 0,
					data.length);
			// Bitmap resized = Bitmap.createScaledBitmap(original, PHOTO_WIDTH,
			// PHOTO_HEIGHT, true);

			// ByteArrayOutputStream blob = new ByteArrayOutputStream();
			// resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);
			data = null;
			System.gc();
			saveImage(original);

		}

	};

	protected void onDestroy() {
		super.onDestroy();
		mCamera.release();
	};

	private void saveImage(final Bitmap bitmap) {
		pd = ProgressDialog.show(SelectCameraOverlayActivity.this, "",
				"Applying effects...");
		pd.setCancelable(false);
		new Thread(new Runnable() {

			@Override
			public void run() {

				Bitmap capturedBitmap = Utils
						.rotateBitmap(
								bitmap,
								SharedImageObjects.mSelectedCamera == CameraInfo.CAMERA_FACING_BACK ? 90
										: 270);
				bitmap.recycle();
				// Bitmap capturedBitmap = null;
				// if (SharedImageObjects.mSelectedCamera ==
				// CameraInfo.CAMERA_FACING_BACK) {
				// capturedBitmap = rotateBitmapcw90(bitmap);
				// } else {
				// capturedBitmap = rotateBitmapCcw90(bitmap);
				// }

				// ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/,
				// bos);
				// byte[] bitmapdata = bos.toByteArray();
				// ByteArrayInputStream bs = new
				// ByteArrayInputStream(bitmapdata);
				SharedImageObjects.mSelectedImageUrl = mListOverlayUrls
						.get(mSelectedImageNumber);

				Utils.savePicture("capturedImage.png", capturedBitmap,
						getBaseContext());

				int width = capturedBitmap.getWidth();
				int height = 0;
				if (capturedBitmap.getWidth() > 500) {
					width = 500;
					height = (capturedBitmap.getHeight() * 500)
							/ capturedBitmap.getWidth();

					capturedBitmap = Bitmap.createScaledBitmap(capturedBitmap,
							width, height, true);

				}
				Bitmap bitmapIn = Utils.cropAndScaleBitmap(capturedBitmap, 0,
						0, capturedBitmap.getWidth(),
						capturedBitmap.getWidth(), 500, 500);
				capturedBitmap.recycle();
				System.gc();
				Utils.savePicture("cropedImage.png", bitmapIn, context);

				Bitmap bitmapIn3 = null;
				bitmapIn3 = Utils.readPicture("cropedImage.png", bitmapIn3,
						context);
				SharedImageListObjects.mTempImage = Utils
						.convertToMutable(bitmapIn3);

				SharedImageListObjects.mEffectImages.clear();
				SharedImageListObjects.mEffectImages
						.add(SharedImageListObjects.mTempImage);
				SharedImageListObjects.mEffectImages.add(Utils
						.hefeImage(bitmapIn));
				// SharedImageListObjects.mEffectImages.add(Utils.earlybirdImage(
				// SelectCameraOverlayActivity.this, bitmapIn));
				// SharedImageListObjects.mEffectImages.add(Utils.xProImage(
				// SelectCameraOverlayActivity.this, bitmapIn));
				// SharedImageListObjects.mEffectImages.add(Utils.inkwellImage(
				// SelectCameraOverlayActivity.this, bitmapIn));
				// SharedImageListObjects.mEffectImages.add(Utils.nashvilleImage(
				// SelectCameraOverlayActivity.this, bitmapIn));
				pd.dismiss();
				Intent intent = new Intent(context, SelectEffectActivity.class);
				startActivity(intent);
				finish();
				// addInfo(capturedBitmap);
				System.gc();
			}
		}).start();

	}

	private Bitmap decodeFile(byte[] data) {
		ByteArrayInputStream bs = new ByteArrayInputStream(data);
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(bs, null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 500;

		// Find the correct scale value. It should be the power of 2.
		int scale = 1;
		while (o.outWidth / scale / 2 >= REQUIRED_SIZE
				&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(bs, null, o2);
	}
}