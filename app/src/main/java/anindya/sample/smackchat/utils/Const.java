package anindya.sample.smackchat.utils;

public class Const {

	//openFire server communication data
	public static final String CHAT_SERVER_ADDRESS = "192.168.0.102"; //"ictsadaf.dhaka.org"; //  you could replace with your local pc ip if mobile device is in same network
	public static final String REST_API_TOKEN = "qZKC0FfkfDppNsHk"; //"MP8KwYlwoCP1rUdY"; // you could replace with yours
	public static final String CHAT_SERVER_SERVICE_NAME = "desktop-r1pbkha"; // "ictsadaf.dhaka.org"; //you could replace with yours
	public static final String CHAT_DEMO_OPPONENT_NAME = "belly";
	public static final int CHAT_SERVER_PORT = 5222;
	public static final String REST_API_ADDRESS = "http://"+CHAT_SERVER_ADDRESS+":9090/plugins/restapi/v1/";
	public static final String CHAT_SERVER_RESOURCE_NAME = "Android";
	public static final String CHAT_ROOM_SERVICE_NAME = "scibd.";// "conference.";
	public static final String ALTERNATE_CHAT_ROOM_REFERENCE = "demo";

	// id to handle the notification in the notification tray
	public static final int NOTIFICATION_ID = 100;
	public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

	public static final int DATABASE_VERSION = 3;
	public static final String DB_NAME = "smackChat";

}
