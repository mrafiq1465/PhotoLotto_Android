package com.abir.photolotto.database;

public class DatabaseConstants {

	public static final String TABLE_PHOTOLOTTO = "photolottotable";
	public static final String TABLE_EMIAL = "email";
	public static final String EVENT_ID = "event_id";
	public static final String DISTANCE = "distance";
	public static final String ID = "id";
	public static final String PASSWORD = "password";
	public static final String NUMBER_OF_OVERLAY = "number_of_overlay";
	public static final String COMPANY_NAME = "company_name";
	public static final String FACEBOOK_MSG = "facebook_msg";
	public static final String FACEBOOK_URL = "facebook_url";
	public static final String HTML_AFTER = "html_after";
	public static final String HTML_BEFORE = "html_before";
	public static final String IMG_THUMB = "img_thumb";
	public static final String NAME = "name";
	public static final String SHORTDESCRIPTION_LINE_1 = "shortdescription_line_1";
	public static final String SHORTDESCRIPTION_LINE_2 = "shortdescription_line_2";
	public static final String EVENT_TYPE = "event_type";
	public static final String TERMS = "t_c";
	public static final String TWITTER_MSG = "twitter_msg";
	public static final String IMG_OVERLAY_5 = "img_overlay_5";
	public static final String IMG_OVERLAY_4 = "img_overlay_4";
	public static final String IMG_OVERLAY_3 = "img_overlay_3";
	public static final String IMG_OVERLAY_2 = "img_overlay_2";
	public static final String IMG_OVERLAY_1 = "img_overlay_1";

	public static final String CREATE_TABLE_PHOTOLOTTO = "create table "
			+ TABLE_PHOTOLOTTO + "(" + EVENT_ID
			+ " integer primary key autoincrement, " + DISTANCE + " text, "
			+ ID + " long, " + PASSWORD + " text, " + NUMBER_OF_OVERLAY
			+ " integer, " + COMPANY_NAME + " text, " + FACEBOOK_MSG
			+ " text, " + FACEBOOK_URL + " text," + HTML_AFTER + " text, "
			+ HTML_BEFORE + " text, " + IMG_THUMB + " text, " + NAME
			+ " text, " + SHORTDESCRIPTION_LINE_1 + " text,"
			+ SHORTDESCRIPTION_LINE_2 + " text, " + EVENT_TYPE + " text,"
			+ TERMS + " text, " + TWITTER_MSG + " text," + IMG_OVERLAY_1
			+ " text, " + IMG_OVERLAY_2 + " text, " + IMG_OVERLAY_3 + " text, "
			+ IMG_OVERLAY_4 + " text, " + IMG_OVERLAY_5 + " text)";

	public static final String EMAIL_ID = "email_id";
	public static final String SURL = "url";
	public static final String SUBJECT = "subject";
	public static final String MESSAGE = "message";
	public static final String IMAGEURL = "imgurl";
	public static final String ISSENT = "isSent";
	public static final String IMAGEOVERLAYID = "imageOverlaySelectedUrl";

	public static final String CREATE_TABLE_EMAIL = "create table "
			+ TABLE_EMIAL + "(" + EMAIL_ID
			+ " integer primary key autoincrement, " + SURL + " text, "
			+ SUBJECT + " long, " + MESSAGE + " text, " + IMAGEURL + " text, "
			+ IMAGEOVERLAYID + " long, " + ISSENT + " string)";

}
