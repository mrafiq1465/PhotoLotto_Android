package com.abir.photolotto;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EmailActivity extends Activity implements OnClickListener {
	EditText mEditTextToEmail, mEditTextSubject, mEditTextMessage;
	private LinearLayout linearLayoutEmailPhoto, llMid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email);
		// mBitmap = Utils.rotateBitmap(SharedImageObjects.mBitmapWithEffect,
		// -5);
		ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
		imageViewPhoto.setImageBitmap(Utils.rotateBitmap(
				SharedImageObjects.mBitmapWithEffect, -5));
		mEditTextToEmail = (EditText) findViewById(R.id.editTextPassword);
		mEditTextSubject = (EditText) findViewById(R.id.editTextEmailSubject);
		mEditTextMessage = (EditText) findViewById(R.id.editTextEmailMessage);

		Button buttonSend = (Button) findViewById(R.id.buttonSendAuth);
		buttonSend.setOnClickListener(this);
		Button buttonCancel = (Button) findViewById(R.id.buttonCancelAuth);
		buttonCancel.setOnClickListener(this);

		linearLayoutEmailPhoto = (LinearLayout) findViewById(R.id.linearLayoutEmailPhoto);
		llMid = (LinearLayout) findViewById(R.id.llMid);
		linearLayoutEmailPhoto.setOnClickListener(this);
		llMid.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public final boolean isValidEmail(CharSequence target) {
		if (target == null || target.toString().isEmpty()) {
			return false;
		} else {

			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.buttonSendAuth:
			if (isValidEmail(mEditTextToEmail.getEditableText().toString())) {
				String sUrl;
				try {

					sUrl = "http://www.pixta.com.au/eventemail?event_id="
							+ EventModel.getSelectedEvent().getlId()
							+ "&phone_type=Android&action=email&phone_id=1&photo="
							+ /* Constants.getPictureBucket() */SharedImageObjects.mKey
							+ "&email_to="
							+ mEditTextToEmail.getEditableText().toString()
							+ "&subject="
							+ URLEncoder.encode(mEditTextSubject
									.getEditableText().toString(), "UTF-8")
							+ "&message="
							+ URLEncoder.encode(mEditTextMessage
									.getEditableText().toString(), "UTF-8");
					new EmailTask(EmailActivity.this).execute(sUrl, "-1");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(EmailActivity.this, "Invalid Email",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.buttonCancelAuth:
			finish();
			break;
		case R.id.linearLayoutEmailPhoto:
		case R.id.llMid:
			dismissKeyPad();
			break;
		default:
			break;
		}
	}

	private void dismissKeyPad() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
}
