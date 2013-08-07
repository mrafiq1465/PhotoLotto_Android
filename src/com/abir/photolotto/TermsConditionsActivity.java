package com.abir.photolotto;

import android.os.Bundle;
import android.widget.TextView;

public class TermsConditionsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_terms_conditions);
		TextView textViewTermsConditions = (TextView) findViewById(R.id.textViewTermsConditions);
		textViewTermsConditions.setText(EventModel.getSelectedEvent()
				.getsTerms());
	}
}