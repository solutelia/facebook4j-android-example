package facebook4j.examples.android.sns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.conf.ConfigurationBuilder;
import facebook4j.examples.android.R;

public class facebook_main {
    public static final String DATA_KEY_FACEBOOK = "facebook";
    public static final int FEED_LIMIT = 10;

    //検索系
    public static final int SEACH_POSTS = 0;
    public static final int SEACH_USERS = 1;
    public static final int SEACH_PLACES =2;
    public static final int SEACH_LOCATIONS =3;
    public static final int SEACH_EVENTS =4;
    public static final int SEACH_GROUPS =5;
    public static final int SEACH_CHECKINS =6;

    //投稿系
    public static final int POST_STATUS = 0;
    public static final int POST_PHOTE = 1;
    public static final int POST_FEED =2;
    
    
    public static String FB_APPID;
    public static String FB_APPSECRET;
    public static String FB_PERMISSIONS;
    public static String FB_LOGIN_URL;
    
    public static Facebook m_facebook;
    public static AccessToken m_accessToken;
    
	private static String TAG = "facebook_main";
	private static String path = "ftest.dat";
    
	public static Activity activity;

	public static void init(Activity activity_){
		activity = activity_;
    	Resources m_r = activity.getResources();
    	FB_APPID = m_r.getString(R.string.fb_appId);
    	FB_APPSECRET = m_r.getString(R.string.fb_appSecret);
    	FB_PERMISSIONS = m_r.getString(R.string.fb_permissions);
    	FB_LOGIN_URL = m_r.getString(R.string.fb_login_url);
	}
	
	public static Facebook loginOAuth(){
		Log.v(TAG, "loginOAuth");
		m_accessToken = loadAccessToken();
		if (m_accessToken == null) {
			m_facebook = null;
			Log.v(TAG, "m_accessToken = null");
			return null;
		}
		//facebookの作成
		m_facebook = createInstance();
		return m_facebook;
	}
	public static Facebook createInstance() {
		ConfigurationBuilder confbuilder  = new ConfigurationBuilder();
		if(m_accessToken!=null){
			confbuilder.setOAuthAccessToken(m_accessToken.getToken());
		}
		confbuilder
		.setOAuthAppId(FB_APPID) 
		.setOAuthAppSecret(FB_APPSECRET) 
		.setOAuthPermissions(FB_PERMISSIONS);
		
		m_facebook = new FacebookFactory(confbuilder.build()).getInstance();
		return  m_facebook;
	}

	//===========================================================================
		// AccesTokenの読み込み
		public static AccessToken loadAccessToken() {
			Log.v(TAG, "loadAccessToken");
			FileInputStream fis = null;
			try {
				File file = activity.getFileStreamPath(path);
				if(!file.exists())return null;
				fis = activity.openFileInput(path);
			} catch (FileNotFoundException e1) {
				Log.e(TAG, "openFileInput ",e1);
				return null; //ファイルが読めない（存在しない）場合はnullを返す
			}

			ObjectInputStream is = null;
			try {
				is = new ObjectInputStream(fis);
				AccessToken accessToken = (AccessToken) is.readObject();
				Log.v(TAG, "loadAccessToken ok");
				return accessToken;

			} catch (Exception e) {
				Log.e(TAG, "loadAccessToken ",e);
				return null; //ファイルが読めない（存在しない）場合はnullを返す
			} finally {
				try {
					if (is != null)is.close();
				} catch (IOException e) {  }
			}
		}

		// AccesTokenの保存
		public static void storeAccessToken() {
			Log.v(TAG, "storeAccessToken");
			FileOutputStream fos = null;
			try {
				fos = activity.openFileOutput(path,Context.MODE_PRIVATE);
			} catch (FileNotFoundException e1) {
				Log.e(TAG,"FileNotFoundException",e1);
			}

			ObjectOutputStream os = null;
			try {
				os = new ObjectOutputStream( fos );
				os.writeObject(m_accessToken);
				Log.v(TAG, "storeAccessToken ok");
				//tnum4.m_twitter = m_twitter;
			} catch (IOException e) {
				Log.e(TAG,"IOException",e);
			} finally {
				try { if (os != null)os.close(); } catch (IOException e) {}
			}
		}
		
		public static void eraseAccessToken(){
			activity.deleteFile(path);
			m_facebook = null;
			m_accessToken = null;
		}
		
		public static boolean isFacebookLogin() {
	    	return m_accessToken==null ? false:true;
	    }


}
