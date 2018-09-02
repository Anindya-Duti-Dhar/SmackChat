package anindya.sample.smackchat.utils;

public class Const {

	//openFire server communication data
	public static final String CHAT_SERVER_ADDRESS = "192.168.0.102"; //"123.200.14.11";   // "153.126.152.115"; // you could replace with your local pc ip if mobile device is in same network
	public static final int CHAT_SERVER_PORT = 5222;
	public static final String CHAT_SERVER_SERVICE_NAME = "desktop-r1pbkha";//"webhawksit";  // "153.126.152.115";
	public static final String CHAT_SERVER_RESOURCE_NAME = "Android";
	public static final String CHAT_ROOM_SERVICE_NAME = "conference.";
	public static final String ALTERNATE_CHAT_ROOM_REFERENCE = "demo";
	public static final String CHAT_DEMO_OPPONENT_NAME = "duti";


	// global topic to receive app wide push notifications
	public static final String TOPIC_GLOBAL = "global";

	// broadcast receiver intent filters
	public static final String REGISTRATION_COMPLETE = "registrationComplete";
	public static final String PUSH_NOTIFICATION = "pushNotification";

	// id to handle the notification in the notification tray
	public static final int NOTIFICATION_ID = 100;
	public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

	public static final String SHARED_PREF = "ah_firebase";

}
