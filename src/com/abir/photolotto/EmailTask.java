package com.abir.photolotto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.abir.photolotto.database.DatabaseConstants;
import com.abir.photolotto.database.DatabaseHandler;

public class EmailTask extends AsyncTask<String, Void, String> {

	private Activity activity;
	private long idOfEmail = -1, selctedOverlay;
	private String recipient, message, subject, mKey;
	String sUrl = "";

	public EmailTask() {
		// TODO Auto-generated constructor stub
	}

	public EmailTask(Activity activity) {
		this.activity = activity;
	}

	protected String doInBackground(String... urls) {

		try {

			idOfEmail = Long.parseLong(urls[1]);

			sUrl = urls[0];
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(sUrl);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			Log.e("EmailActivity", statusLine.getReasonPhrase().toString());
			return statusLine.getReasonPhrase().toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	protected void onPostExecute(String result) {

		DatabaseHandler db = new DatabaseHandler(activity);
		ContentValues values = new ContentValues();
		values.put(DatabaseConstants.SURL, sUrl);

		if (result.equalsIgnoreCase("OK")) {
			if (idOfEmail != -1) {
				values.put(DatabaseConstants.IMAGEURL, sUrl);
				values.put(DatabaseConstants.ISSENT, "true");
				db.updateRecord(DatabaseConstants.TABLE_EMIAL, values,
						DatabaseConstants.EMAIL_ID + " =" + idOfEmail);
			} else
				activity.finish();

		} else {
			if (idOfEmail == -1) {
				values.put(DatabaseConstants.IMAGEURL, getFileFromBitmap());
				values.put(DatabaseConstants.ISSENT, "false");
				db.insertRecord(DatabaseConstants.TABLE_EMIAL, values);
			}
		}

	}

	private String getFileFromBitmap() {
		File fileImage = null;
		try {
			File storagePath = new File(Constants.DIRECTORY_PATH + "/temp");
			storagePath.mkdirs();
			String sFileName = Utils.generateUniqueName() + ".jpg";
			fileImage = new File(storagePath, sFileName);
			FileOutputStream out = new FileOutputStream(fileImage);
			SharedImageObjects.mBitmapWithEffect.compress(
					Bitmap.CompressFormat.JPEG, 80, out);
			out.flush();
			out.close();
			// bitmap.recycle();
			// bitmap = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileImage.getAbsolutePath();
	}
}
