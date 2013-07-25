package com.abir.photolotto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EventAuthActivity extends Activity {
	private TextView mRequestPasswdTextView;
	private TextView mDescirptionPasswdTextView;
	private EditText mPasswdEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_auth);

		mRequestPasswdTextView = (TextView) findViewById(R.id.textViewRequestPassword);
		mDescirptionPasswdTextView = (TextView) findViewById(R.id.textViewDescriptionPassword);
		mPasswdEditText = (EditText) findViewById(R.id.editTextPassword);
		
		Button sendAuthButton = (Button) findViewById(R.id.buttonSendAuth);
		sendAuthButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkPassword();
			}
		});
		
		Button cancelAuthButton = (Button) findViewById(R.id.buttonCancelAuth);
		cancelAuthButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
	
	private void checkPassword() {
		EventModel em = EventModel.getSelectedEvent();
		String sPassword = em.getsPassword();
		Log.d("PhotoLotto", "Password: " + sPassword);
		String sTypedPassword = mPasswdEditText.getEditableText().toString();
		if (sPassword.equals(sTypedPassword))
		{
			Intent intent = new Intent(EventAuthActivity.this, SelectCameraOverlayActivity.class);
			startActivity(intent);
			finish();
		} else {
			mRequestPasswdTextView.setText("Incorrect Password");
			mRequestPasswdTextView.setTextColor(0xffff0000);
			mDescirptionPasswdTextView.setText("That password is not recognised.\nPlease try again");
			mPasswdEditText.setSelection(0, mPasswdEditText.getEditableText().toString().length());
		}
	}
}
