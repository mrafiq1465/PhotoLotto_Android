package com.abir.photolotto;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = CameraPreview.class.getSimpleName();
	private SurfaceHolder mHolder;
	private static Camera mCamera;
	private static boolean mHasFlashSupport = true;
	private Context mContext;
	private int mCameraId = CameraInfo.CAMERA_FACING_BACK;
//	private AutoFocusManager autoFocusManager;

	public Camera getCamera() {
		return mCamera;
	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			if (android.os.Build.VERSION.SDK_INT > 8
					&& Camera.getNumberOfCameras() == 2)
				c = Camera.open(mCameraId); // attempt to get a Camera instance
			else
				c = Camera.open();

		} catch (Exception e) {
			;
		}

		return c; // returns null if camera is unavailable
	}

	public void initializeCamera() {
		mHasFlashSupport = mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);

		mCamera = getCameraInstance();
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public CameraPreview(Context context) {
		super(context);
		mContext = context;
		initializeCamera();
	}

	public int toggleFlash() {
		if (mCamera == null || mCameraId == CameraInfo.CAMERA_FACING_FRONT)
			return -1;
		int nRet = 0;
		if (mHasFlashSupport) {
			mCamera.stopPreview();
			Parameters parameters = mCamera.getParameters();
			String sFlashMode = parameters.getFlashMode();
			nRet = sFlashMode.equals(Camera.Parameters.FLASH_MODE_ON) ? 1 : 0;
			parameters
					.setFlashMode(nRet == 1 ? Camera.Parameters.FLASH_MODE_OFF
							: Camera.Parameters.FLASH_MODE_ON);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
//			autoFocusManager = new AutoFocusManager(mContext, mCamera);

		}
		return nRet;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		stopCamera();
	}

	public void stopCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			getHolder().removeCallback(this);
			mCamera.release();
			mCamera = null;
		}
	}

	public void startCamera() {

		surfaceCreated(mHolder);
		// stop preview before making changes
		mCamera.stopPreview();
		try {
			mCamera.setPreviewDisplay(mHolder);
			Parameters parameters = mCamera.getParameters();
			mCamera.setDisplayOrientation(90);
			// parameters.setRotation(mCameraId == CameraInfo.CAMERA_FACING_BACK
			// ? 90 : 270);

			if (parameters.getFocusMode().equals(
					Camera.Parameters.FOCUS_MODE_AUTO)) {
				mCamera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {

						;
					}
				});
			}

			boolean bLargeThan640 = true;
			Camera.Size minPictureSize = parameters.getPictureSize();
			List<Size> sizes = parameters.getSupportedPictureSizes();
			for (Camera.Size size : sizes) {
				Log.d("PhotoLotto", "Supported size: " + size.width + " x "
						+ size.height);

				if (size.height < 640)
					continue;
				if (bLargeThan640) {
					minPictureSize = size;
					bLargeThan640 = false;
					continue;
				}

				if (size.width < minPictureSize.width) {
					minPictureSize = size;
				} else if (size.width == minPictureSize.width
						&& size.height < minPictureSize.height) {
					minPictureSize = size;
				}
			}
			Log.d("PhotoLotto", "Set size: " + minPictureSize.width + " x "
					+ minPictureSize.height);
			parameters.setPictureSize(minPictureSize.width,
					minPictureSize.height);

			// float ratio = (float)minPictureSize.width /
			// (float)minPictureSize.height;
			// Camera.Size minPreviewSize = parameters.getPreviewSize();
			// List<Size> sizes2 = parameters.getSupportedPreviewSizes();
			// for(Camera.Size size : sizes2) {
			// if (ratio - (float)size.width / (float)size.height <= 0.01)
			// {
			// minPreviewSize = size;
			// break;
			// }
			// }
			// parameters.setPreviewSize(minPreviewSize.width,
			// minPreviewSize.height);

			mCamera.setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (holder.getSurface() == null) {
			return;
		}
		if (mCamera == null) {
			initializeCamera();
		}

		requestLayout();
		startCamera();
	}

	public void toggleCamera(int cameraId) {
		mCameraId = cameraId;
		stopCamera();
		initializeCamera();
		startCamera();
	}
}
