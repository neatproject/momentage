package com.test.momentage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.test.fisco.R;

public class main extends Activity {

	private static final String TAG = "MainFragment";
	private static final String LOG_TAG = "image-test";

	public static String user_ID = "";
	public static String profileName = "";
	public static final String AUTENTICATE_SERVICE = "https://familymelon.com/momentage/authenticate.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		/*
		 * try { PackageInfo info =
		 * getApplicationContext().getPackageManager().getPackageInfo(
		 * "com.test.fisco", PackageManager.GET_SIGNATURES); //Your package name
		 * here for (Signature signature : info.signatures) { MessageDigest md =
		 * MessageDigest.getInstance("SHA"); md.update(signature.toByteArray());
		 * Log.v("KeyHash:", Base64.encodeToString(md.digest(),
		 * Base64.DEFAULT)); } } catch (NameNotFoundException e) { } catch
		 * (NoSuchAlgorithmException e) { }
		 */

		// LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "Responde !!");
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		// final Session session = Session.getActiveSession();
		if (resultCode == RESULT_OK) {

			Session session = Session.getActiveSession();
			if (session == null) {
				session = new Session(this);
				Session.setActiveSession(session);
			}
			/*
			 * if (!session.isOpened() && !session.isClosed())) {
			 * session.openForRead(new Session.OpenRequest(this)
			 * .setPermissions(Arrays.asList("basic_info","email"))
			 * .setCallback(statusCallback)); }
			 */

			// If the session is open, make an API call to get user data
			// and define a new callback to handle the response
			Request request = Request.newMeRequest(Session.getActiveSession(),
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							Log.d("HOLA", "HOLA");

							user_ID = user.getId();// user id
							Log.d("USER_ID", user_ID);
							profileName = user.getName();// user's profile
							LoginOnAPI lOA = new LoginOnAPI();
							lOA.execute();
							// userNameView.setText(user.getName());
						}
					});
			Request.executeBatchAsync(request);

			/*
			 * newGame.putExtra("map_path", (String)
			 * map_selected.get(ConfigKeys.KEY_PATH));
			 */

		}

	}

	class LoginOnAPI extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						AUTENTICATE_SERVICE);

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("userId", user_ID));					
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}

			} catch (Exception e) {
			}

		}

		@Override
		protected String doInBackground(String... args) {

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				public void run() {
					Intent newGame = new Intent(getApplicationContext(),
							Dashboard.class);
					startActivity(newGame);
				}
			});
		}
	}

	/*
	 * private void onSessionStateChange(Session session, SessionState state,
	 * Exception exception) { if (state.isOpened()) { Log.i(TAG,
	 * "Logged in..."); } else if (state.isClosed()) { Log.i(TAG,
	 * "Logged out..."); } }
	 */
}