package com.abir.photolotto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.abir.photolotto.database.DatabaseConstants;
import com.abir.photolotto.database.DatabaseHandler;

public class ReadFeed extends AsyncTask<String, Void, String> {
	private int mEvent = -1;
	ReadFeedEventHandler eventHandler;
	boolean mIsFailed = true;
	public Location mLocation;
	private Context context;
	private DatabaseHandler db;

	public ReadFeed(Context context) {
		mEvent = -1;
		this.context = context;
		db = new DatabaseHandler(context);
	}

	public ReadFeed(int nEvent, Context context) {
		mEvent = nEvent;
		this.context = context;
		db = new DatabaseHandler(context);
	}

	private double getLatitude() {
		if (mLocation == null)
			return 0.0;
		return mLocation.getLatitude();
	}

	private double getLongitude() {
		if (mLocation == null)
			return 0.0;
		return mLocation.getLongitude();
	}

	public void setLocation(Location location) {
		mLocation = location;
	}

	public void setEventHandler(ReadFeedEventHandler handler) {
		eventHandler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		double gpslat = getLatitude();
		double gpslong = getLongitude();
		HttpGet httpGet = new HttpGet(
				"http://www.pixta.com.au/eventlist?gpslat=" + gpslat
						+ "&gpslong=" + gpslong);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("ReadFeed", "Failed to download file");
				mIsFailed = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			mIsFailed = true;
		}

		return builder.toString();
	}

	@Override
	protected void onPostExecute(String result) {

		mIsFailed = false;
		try {

			JSONArray jsonArray = new JSONArray(result);
			// Log.i(ParseJSON.class.getName(), "Number of entries " +
			// jsonArray.length());
			EventModel.lstPixtaEvents.clear();
			EventModel.lstNearEvents.clear();
			EventModel.lstNationalEvents.clear();
			db.drop();

			ContentValues values = new ContentValues();
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.d("SelectEventActivity", jsonObject.toString());
				EventModel eventModel = new EventModel();
				try {
					eventModel.setfDistance(jsonObject.getString("distance"));
					values.put(DatabaseConstants.DISTANCE,
							eventModel.getfDistance());
					eventModel.setlId(jsonObject.getLong("id"));
					values.put(DatabaseConstants.ID, eventModel.getlId());
					eventModel.setsPassword(jsonObject.getString("password"));
					values.put(DatabaseConstants.PASSWORD,
							eventModel.getsPassword());
					eventModel.setnNumberOfOverlay(jsonObject
							.getInt("number_of_overlay"));
					values.put(DatabaseConstants.NUMBER_OF_OVERLAY,
							eventModel.getnNumberOfOverlay());
					eventModel.setsCompanyName(jsonObject
							.getString("company_name"));
					values.put(DatabaseConstants.COMPANY_NAME,
							eventModel.getsCompanyName());
					eventModel.setsFacebookMessage(jsonObject
							.getString("facebook_msg"));
					values.put(DatabaseConstants.FACEBOOK_MSG,
							eventModel.getsFacebookMessage());
					eventModel.setsFacebookUrl(jsonObject
							.getString("facebook_url"));
					values.put(DatabaseConstants.FACEBOOK_URL,
							eventModel.getsFacebookUrl());
					eventModel
							.setsHtmlAfter(jsonObject.getString("html_after"));
					values.put(DatabaseConstants.HTML_AFTER,
							eventModel.getsHtmlAfter());
					eventModel.setsHtmlBefore(jsonObject
							.getString("html_before"));
					values.put(DatabaseConstants.HTML_BEFORE,
							eventModel.getsHtmlBefore());
					eventModel.setsImgThumb(jsonObject.getString("img_thumb"));

					eventModel.setsName(jsonObject.getString("name"));
					values.put(DatabaseConstants.NAME, eventModel.getsName());
					eventModel.setsShortDescription1(jsonObject
							.getString("shortdescription_line_1"));
					values.put(DatabaseConstants.SHORTDESCRIPTION_LINE_1,
							eventModel.getsShortDescription1());
					eventModel.setsShortDescription2(jsonObject
							.getString("shortdescription_line_2"));
					values.put(DatabaseConstants.SHORTDESCRIPTION_LINE_2,
							eventModel.getsShortDescription2());
					eventModel
							.setsEventType(jsonObject.getString("event_type"));
					values.put(DatabaseConstants.EVENT_TYPE,
							eventModel.getsEventType());
					eventModel.setsTerms(jsonObject.getString("t_c"));
					values.put(DatabaseConstants.TERMS, eventModel.getsTerms());
					eventModel.setsTwitterMsg(jsonObject
							.getString("twitter_msg"));
					values.put(DatabaseConstants.TWITTER_MSG,
							eventModel.getsTwitterMsg());
					int number_of_overlay = jsonObject
							.getInt("number_of_overlay");
					eventModel.setnNumberOfOverlay(number_of_overlay);
					values.put(DatabaseConstants.IMG_THUMB,
							eventModel.getsImgThumb());
					switch (number_of_overlay) {
					case 5:
						eventModel.setsImageOverlay5(jsonObject
								.getString("img_overlay_5"));
						values.put(DatabaseConstants.IMG_OVERLAY_5,
								eventModel.getsImageOverlay5());
					case 4:
						eventModel.setsImageOverlay4(jsonObject
								.getString("img_overlay_4"));
						values.put(DatabaseConstants.IMG_OVERLAY_4,
								eventModel.getsImageOverlay4());
					case 3:
						eventModel.setsImageOverlay3(jsonObject
								.getString("img_overlay_3"));
						values.put(DatabaseConstants.IMG_OVERLAY_3,
								eventModel.getsImageOverlay3());
					
					case 2:
						eventModel.setsImageOverlay2(jsonObject
								.getString("img_overlay_2"));
				
						values.put(DatabaseConstants.IMG_OVERLAY_2,
								eventModel.getsImageOverlay2());
					case 1:
						eventModel.setsImageOverlay1(jsonObject
								.getString("img_overlay_1"));
						values.put(DatabaseConstants.IMG_OVERLAY_1,
								eventModel.getsImageOverlay1());
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mIsFailed = true;
				}

				if (eventModel.getsEventType().equalsIgnoreCase("pixta-play"))
					EventModel.lstPixtaEvents.add(eventModel);
				else if (eventModel.getsEventType()
						.equalsIgnoreCase("national")) {
					EventModel.lstNationalEvents.add(eventModel);
				} else {
					String[] sDistance = eventModel.getfDistance()
							.split("\\s+");
					if (Float.parseFloat(sDistance[0]) < 25)
						EventModel.lstNearEvents.add(eventModel);
				}
				
				db.insertRecord(DatabaseConstants.TABLE_PHOTOLOTTO, values);
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
			if (PreferenceManager.getDefaultSharedPreferences(context)
					.getBoolean(Constants.ISDOWNLOADEDALLIMAGES, false)) {
				createModels();
			} else
				mIsFailed = true;
		}

		if (mIsFailed)
			eventHandler.onFailure();
		else
			eventHandler.onPostExecute(mEvent);
	}

	private void createModels() {
		ArrayList<HashMap<String, String>> allEventsList = db
				.executeQuery("select * from "
						+ DatabaseConstants.TABLE_PHOTOLOTTO);
		EventModel.lstPixtaEvents.clear();
		EventModel.lstNearEvents.clear();
		EventModel.lstNationalEvents.clear();
		for (int i = 0; i < allEventsList.size(); i++) {
			HashMap<String, String> singleEvent = allEventsList.get(i);
			EventModel eventModel = new EventModel();
			eventModel
					.setfDistance(singleEvent.get(DatabaseConstants.DISTANCE));

			eventModel.setlId(Long.parseLong(singleEvent
					.get(DatabaseConstants.ID)));
			eventModel
					.setsPassword(singleEvent.get(DatabaseConstants.PASSWORD));

			eventModel.setsCompanyName(singleEvent
					.get(DatabaseConstants.COMPANY_NAME));

			eventModel.setsFacebookMessage(singleEvent
					.get(DatabaseConstants.FACEBOOK_MSG));

			eventModel.setsFacebookUrl(singleEvent
					.get(DatabaseConstants.FACEBOOK_URL));

			eventModel.setsHtmlAfter(singleEvent
					.get(DatabaseConstants.HTML_AFTER));
			eventModel.setsHtmlBefore(singleEvent
					.get(DatabaseConstants.HTML_BEFORE));

			eventModel.setsImgThumb(singleEvent
					.get(DatabaseConstants.IMG_THUMB));

			eventModel.setsName(singleEvent.get(DatabaseConstants.NAME));

			eventModel.setsShortDescription1(singleEvent
					.get(DatabaseConstants.SHORTDESCRIPTION_LINE_1));
			eventModel.setsShortDescription2(singleEvent
					.get(DatabaseConstants.SHORTDESCRIPTION_LINE_2));

			eventModel.setsEventType(singleEvent
					.get(DatabaseConstants.EVENT_TYPE));
			eventModel.setsTerms(singleEvent.get(DatabaseConstants.TERMS));
			eventModel.setsTwitterMsg(singleEvent
					.get(DatabaseConstants.TWITTER_MSG));

			int number_of_overlay = Integer.parseInt(singleEvent
					.get(DatabaseConstants.NUMBER_OF_OVERLAY));
			eventModel.setnNumberOfOverlay(number_of_overlay);
			switch (number_of_overlay) {
			case 5:
				eventModel.setsImageOverlay5(singleEvent
						.get(DatabaseConstants.IMG_OVERLAY_5));
			case 4:
				eventModel.setsImageOverlay4(singleEvent
						.get(DatabaseConstants.IMG_OVERLAY_4));
			case 3:
				eventModel.setsImageOverlay3(singleEvent
						.get(DatabaseConstants.IMG_OVERLAY_3));
			case 2:
				eventModel.setsImageOverlay2(singleEvent
						.get(DatabaseConstants.IMG_OVERLAY_2));

			case 1:
				eventModel.setsImageOverlay1(singleEvent
						.get(DatabaseConstants.IMG_OVERLAY_1));

				break;
			default:
				break;
			}
			if (eventModel.getsEventType().equalsIgnoreCase("pixta-play"))
				EventModel.lstPixtaEvents.add(eventModel);
			else if (eventModel.getsEventType()
					.equalsIgnoreCase("national")) {
				EventModel.lstNationalEvents.add(eventModel);
			} else {
				String[] sDistance = eventModel.getfDistance()
						.split("\\s+");
				if (Float.parseFloat(sDistance[0]) < 25)
					EventModel.lstNearEvents.add(eventModel);
			}
		}

	}

	@Override
	protected void onPreExecute() {
		eventHandler.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
}

interface ReadFeedEventHandler {
	public void onPreExecute();

	public void onPostExecute(int nEvent);

	public void onFailure();
}