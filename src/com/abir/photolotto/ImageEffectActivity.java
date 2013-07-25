package com.abir.photolotto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ImageEffectActivity extends Activity {
	Bitmap						bitmapIn		= null;
	ProgressDialog				mDialog;
	private class LoadImages extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SharedImageListObjects.mEffectImages.clear();
			SharedImageListObjects.mEffectImages.add(SharedImageListObjects.mTempImage);
			SharedImageListObjects.mEffectImages.add(Utils.hefeImage(bitmapIn));
			SharedImageListObjects.mEffectImages.add(Utils.earlybirdImage(ImageEffectActivity.this, bitmapIn));
			SharedImageListObjects.mEffectImages.add(Utils.xProImage(ImageEffectActivity.this, bitmapIn));
			SharedImageListObjects.mEffectImages.add(Utils.inkwellImage(ImageEffectActivity.this, bitmapIn));
			SharedImageListObjects.mEffectImages.add(Utils.nashvilleImage(ImageEffectActivity.this, bitmapIn));

			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			mDialog.dismiss();
			
			Intent intent = new Intent(ImageEffectActivity.this,
					SelectEffectActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(ImageEffectActivity.this, null, "Applying effects...", true);
			mDialog.setCancelable(false);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_effect);

		Context context = getBaseContext(); //getApplicationContext();
		
		//read captured image
		Bitmap capturedBitmap = null;
		capturedBitmap = Utils.readPicture("capturedImage.png", capturedBitmap, context);
		
		//make 640 * 640 image
		int width = Math.min(capturedBitmap.getWidth(), capturedBitmap.getHeight());
		int height = width;
		bitmapIn = Utils.cropAndScaleBitmap(capturedBitmap, 0, 0, width, height, 640, 640);
//		SharedImageObjects.mBitmap = bitmapIn;
		
		//save as file 
		Utils.savePicture("cropedImage.png", bitmapIn, context);
		
		//read again
		Bitmap bitmapIn3 = null;
		bitmapIn3 = Utils.readPicture("cropedImage.png", bitmapIn3, context);
		SharedImageListObjects.mTempImage = Utils.convertToMutable(bitmapIn3);
		
		new LoadImages().execute();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	@Override
	public void onBackPressed() {

		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
}