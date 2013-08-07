package com.abir.photolotto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.abir.photolotto.database.DatabaseConstants;
import com.abir.photolotto.database.DatabaseHandler;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		this.registerReceiver(mConnectivityCheckReceiver, filter);
		// sendMails();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConnectivityCheckReceiver);
	}

	private final BroadcastReceiver mConnectivityCheckReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			boolean mStatus = noConnectivity;
			if (!mStatus) {
				sendMails();
			}

		}
	};

	private void sendMails() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				DatabaseHandler db = new DatabaseHandler(BaseActivity.this);
				ArrayList<HashMap<String, String>> allEmailsRecords = db
						.executeQuery("select * from "
								+ DatabaseConstants.TABLE_EMIAL);
				for (int i = 0; i < allEmailsRecords.size(); i++) {
					if (Utils.isOnline(BaseActivity.this)) {
						HashMap<String, String> singleRecord = allEmailsRecords
								.get(i);
						
						if (singleRecord.get(DatabaseConstants.ISSENT)
								.equalsIgnoreCase("false")) {
							File file = new File(singleRecord
									.get(DatabaseConstants.IMAGEURL));
							SharedImageObjects.mBitmap = BitmapFactory
									.decodeFile(singleRecord
											.get(DatabaseConstants.IMAGEURL));
							SharedImageObjects.mKey = file.getName();
							new EmailTask(BaseActivity.this).execute(
									singleRecord
											.get(DatabaseConstants.SURL),
									singleRecord
											.get(DatabaseConstants.EMAIL_ID));
						}
					}
				}

			}
		}).start();
	}
}
