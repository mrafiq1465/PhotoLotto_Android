package com.abir.photolotto;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class SelectEventActivity extends Activity 
									implements OnClickListener, LocationListener, ReadFeedEventHandler {

	private final int					EVENT_PIXTA = 0;
	private final int					EVENT_LOCATION = 1;
	private final int					EVENT_NATIONAL = 2;
	private final int					EVENT_SEARCH = 3;
	private final int					EVENT_TOTAL	= 4;
	private LocationManager				mLocationManager;
	private Location					mLocation;
	private ArrayAdapter<EventModel>	mArrayAdapterPixta		= null;
	private ArrayAdapter<EventModel>	mArrayAdapterNear		= null;
	private ArrayAdapter<EventModel>	mArrayAdapterNational	= null;
	private ReadFeed					mReadFeedEvent = new ReadFeed(-1);
	private PullToRefreshListView		mListViewNear, mListViewPixta, mListViewNational;
	private TextView					mTextViewHeaderPixta;
	private Button						mButtonPlay, mButtonNear, mButtonNational, mButtonSearch;
	private EditText					mEditTextSearch;
	private boolean[]					mArrayButtonStates		= {false, false, false, false};
	private boolean[]					mArrayTapStates		= {false, false, false, true};
	private LinearLayout				mSearchTypebar 			= null;
	private Button						mTabPlay, mTabBrands, mTabPersonal, mTapAll;
	
	private void startReadFeed(int nEvent) {
		mReadFeedEvent = new ReadFeed(nEvent);
		mReadFeedEvent.setEventHandler(SelectEventActivity.this);
		mReadFeedEvent.setLocation(mLocation);
		mReadFeedEvent.execute("");
	}

	@Override
	protected void onDestroy() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromInputMethod(mEditTextSearch.getWindowToken(), 0);
	}

	private void setOnClickAndOnRefreshListener(PullToRefreshListView listView, final List<EventModel> list, final int nEvent) {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PullToRefreshListView aPullToRefreshListView = (PullToRefreshListView)parent;
				if (!aPullToRefreshListView.isRefreshing())
				{
					EventModel em = list.get(position);
					setEventAndStartActivity(em);
				} else {
					Log.d("---", "refresh...");
				}
			}
		});

		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				startReadFeed(nEvent);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (mLocation != null) {
			// Do something with the recent location fix otherwise wait for the update below
		} else {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
		setContentView(R.layout.activity_events);

		mSearchTypebar = (LinearLayout) findViewById(R.id.searchTypebar);
		
		mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);
		mTextViewHeaderPixta = (TextView) findViewById(R.id.textViewPixtaPlayHeader);

		mListViewPixta = (PullToRefreshListView) findViewById(R.id.listViewPixtaEvents);
		mListViewNear = (PullToRefreshListView) findViewById(R.id.listViewEvents);
		mListViewNational = (PullToRefreshListView) findViewById(R.id.listViewEventsNational);

		setOnClickAndOnRefreshListener(mListViewPixta, EventModel.lstPixtaEvents, EVENT_PIXTA);
		setOnClickAndOnRefreshListener(mListViewNear, EventModel.lstNearEvents, EVENT_LOCATION);
		setOnClickAndOnRefreshListener(mListViewNational, EventModel.lstNationalEvents, EVENT_NATIONAL);

		mArrayAdapterPixta = new EventAdapter(SelectEventActivity.this, EventModel.lstPixtaEvents);
		mListViewPixta.setAdapter(mArrayAdapterPixta);
		mArrayAdapterPixta.notifyDataSetChanged();

		mArrayAdapterNear = new EventAdapter(SelectEventActivity.this, EventModel.lstNearEvents);
		mListViewNear.setAdapter(mArrayAdapterNear);
		mArrayAdapterNear.notifyDataSetChanged();

		mArrayAdapterNational = new EventAdapter(SelectEventActivity.this, EventModel.lstNationalEvents);
		mListViewNational.setAdapter(mArrayAdapterNational);
		mArrayAdapterNational.notifyDataSetChanged();

		mButtonPlay = (Button) findViewById(R.id.buttonPlay);
		mButtonPlay.setOnClickListener(this);
		mButtonNear = (Button) findViewById(R.id.buttonNearMe);
		mButtonNear.setOnClickListener(this);
		mButtonNational = (Button) findViewById(R.id.buttonNational);
		mButtonNational.setOnClickListener(this);
		mButtonSearch = (Button) findViewById(R.id.buttonSearch);
		mButtonSearch.setOnClickListener(this);

		mTabPlay = (Button) findViewById(R.id.searchTabPlay);
		mTabPlay.setOnClickListener(this);
		mTabBrands = (Button) findViewById(R.id.searchTabBrands);
		mTabBrands.setOnClickListener(this);
		mTabPersonal = (Button) findViewById(R.id.searchTabPersonal);
		mTabPersonal.setOnClickListener(this);
		mTapAll = (Button) findViewById(R.id.searchTabAll);
		mTapAll.setOnClickListener(this);
		
		mEditTextSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int start, int before, int count) {
				// When user changed the Text
				if ((mArrayTapStates[EVENT_PIXTA] || mArrayTapStates[EVENT_SEARCH]) && (mArrayAdapterPixta != null))
					mArrayAdapterPixta.getFilter().filter(cs);
				if ((mArrayTapStates[EVENT_LOCATION] || mArrayTapStates[EVENT_SEARCH]) && (mArrayAdapterNear != null))
					mArrayAdapterNear.getFilter().filter(cs);
				if ((mArrayTapStates[EVENT_NATIONAL] || mArrayTapStates[EVENT_SEARCH]) && (mArrayAdapterNational != null))
					mArrayAdapterNational.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public void onClick(View v) {
		boolean bClickBottombar;
		switch (v.getId()) {
			case R.id.buttonPlay :
				bClickBottombar = true;
				break;
			case R.id.buttonNearMe :
				bClickBottombar = true;
				break;
			case R.id.buttonNational :
				bClickBottombar = true;
				break;
			case R.id.buttonSearch :
				bClickBottombar = true;
				break;
				
			default :
				bClickBottombar = false;
				break;
		}
		
		if (bClickBottombar)
		{
			for (int i = 0; i < EVENT_TOTAL; i++) {
				mArrayButtonStates[i] = false;
			}
		} else {
			for (int i = 0; i < EVENT_TOTAL; i++) {
				mArrayTapStates[i] = false;
			}
		}
		switch (v.getId()) {
			case R.id.buttonPlay :
				mArrayButtonStates[EVENT_PIXTA] = true;
				mTextViewHeaderPixta.setText("Pixta Play");
				break;
			case R.id.buttonNearMe :
				mArrayButtonStates[EVENT_LOCATION] = true;
				mTextViewHeaderPixta.setText("Brands");
				break;
			case R.id.buttonNational :
				mArrayButtonStates[EVENT_NATIONAL] = true;
				mTextViewHeaderPixta.setText("Personal");
				break;
			case R.id.buttonSearch :
				mArrayButtonStates[EVENT_SEARCH] = true;
				mTextViewHeaderPixta.setText("Search Events");
				break;
				
			case R.id.searchTabPlay :
				mArrayTapStates[EVENT_PIXTA] = true;
				break;
			case R.id.searchTabBrands :
				mArrayTapStates[EVENT_LOCATION] = true;
				break;
			case R.id.searchTabPersonal :
				mArrayTapStates[EVENT_NATIONAL] = true;
				break;
			case R.id.searchTabAll :
				mArrayTapStates[EVENT_SEARCH] = true;
				break;
			default :
				return;
		}

		if (bClickBottombar)
		{
			mButtonPlay.setBackgroundResource(mArrayButtonStates[EVENT_PIXTA] ? R.drawable.tab_play_on : R.drawable.tab_play_off);
			mButtonNear.setBackgroundResource(mArrayButtonStates[EVENT_LOCATION] ? R.drawable.tab_nearme_on : R.drawable.tab_nearme_off);
			mButtonNational.setBackgroundResource(mArrayButtonStates[EVENT_NATIONAL] ? R.drawable.tab_national_on : R.drawable.tab_national_off);
			mButtonSearch.setBackgroundResource(mArrayButtonStates[EVENT_SEARCH] ? R.drawable.tab_search_on : R.drawable.tab_search_off);
			
			mListViewPixta.setVisibility((mArrayButtonStates[0] | mArrayButtonStates[3]) ? View.VISIBLE : View.GONE);
			mListViewNear.setVisibility((mArrayButtonStates[1] | mArrayButtonStates[3]) ? View.VISIBLE : View.GONE);
			mListViewNational.setVisibility(mArrayButtonStates[2] | mArrayButtonStates[3] ? View.VISIBLE : View.GONE);
			mEditTextSearch.setVisibility(mArrayButtonStates[3] ? View.VISIBLE : View.GONE);
			
			mSearchTypebar.setVisibility(mArrayButtonStates[3] ? View.VISIBLE : View.GONE);
			mTextViewHeaderPixta.setVisibility(mArrayButtonStates[3] ? View.GONE : View.VISIBLE);
			
			if (mArrayButtonStates[EVENT_SEARCH] == false) {
				mEditTextSearch.setText("");
			} else {
				mTextViewHeaderPixta.setText("");
				mEditTextSearch.requestFocus();
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInputFromWindow(mEditTextSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
			}
			
			mArrayAdapterPixta.notifyDataSetChanged();
			mArrayAdapterNear.notifyDataSetChanged();
			mArrayAdapterNational.notifyDataSetChanged();
		} else {
			mTabPlay.setBackgroundResource(mArrayTapStates[EVENT_PIXTA] ? R.drawable.searchbar_left_tap_on : R.drawable.searchbar_left_tap_off);
			mTabBrands.setBackgroundResource(mArrayTapStates[EVENT_LOCATION] ? R.drawable.searchbar_mid_tap_on : R.drawable.searchbar_mid_tap_off);
			mTabPersonal.setBackgroundResource(mArrayTapStates[EVENT_NATIONAL] ? R.drawable.searchbar_mid_tap_on : R.drawable.searchbar_mid_tap_off);
			mTapAll.setBackgroundResource(mArrayTapStates[EVENT_SEARCH] ? R.drawable.searchbar_right_tap_on : R.drawable.searchbar_right_tap_off);
			
			mListViewPixta.setVisibility((mArrayTapStates[EVENT_PIXTA] | mArrayTapStates[EVENT_SEARCH]) ? View.VISIBLE : View.GONE);
			mListViewNear.setVisibility((mArrayTapStates[EVENT_LOCATION] | mArrayTapStates[EVENT_SEARCH]) ? View.VISIBLE : View.GONE);
			mListViewNational.setVisibility(mArrayTapStates[EVENT_NATIONAL] | mArrayTapStates[EVENT_SEARCH] ? View.VISIBLE : View.GONE);
			
//			mArrayAdapterPixta.notifyDataSetChanged();
//			mArrayAdapterNear.notifyDataSetChanged();
//			mArrayAdapterNational.notifyDataSetChanged();
			
			mEditTextSearch.setText(mEditTextSearch.getText());
		}
	}

	private void setEventAndStartActivity(EventModel em) {
		EventModel.setEvent(em);
		
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromInputMethod(mEditTextSearch.getWindowToken(), 0);
		
		String sPassword = em.getsPassword();
		boolean isNotNull = sPassword != null && !sPassword.equals("") && !sPassword.equals("null");
		if (isNotNull)
		{
			Intent intent = new Intent(SelectEventActivity.this, EventAuthActivity.class);
			startActivity(intent);			
		} else {
			Intent intent = new Intent(SelectEventActivity.this, SelectCameraOverlayActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	@Override
	public void onPreExecute() {

	}

	@Override
	public void onPostExecute(int nEvent) {
		
		if(nEvent < 0 || EVENT_PIXTA == nEvent) {
			mListViewPixta.onRefreshComplete();
			mArrayAdapterPixta.notifyDataSetChanged();
		}		
		
		if(nEvent < 0 || EVENT_LOCATION == nEvent) {
			mListViewNear.onRefreshComplete();
			mArrayAdapterNear.notifyDataSetChanged();
		}
		
		if(nEvent < 0 || EVENT_NATIONAL == nEvent) {
			mListViewNational.onRefreshComplete();
			mArrayAdapterNational.notifyDataSetChanged();
		}
	}

	@Override
	public void onFailure() {

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}