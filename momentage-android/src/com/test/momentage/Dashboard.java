package com.test.momentage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

public class Dashboard extends Activity {

	final int SELECT_MEDIA = 3;
	public static final String UPLOAD_SERVICE = "https://familymelon.com/momentage/upload.php";// "http://192.168.1.86/islafuerte/isla_web_page/fisco_test.php";//
	private String videoPath;
	private ProgressDialog pDialog;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		Button newMoments = (Button) findViewById(R.id.button2);
		newMoments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("media/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Seleccione foto/video "),
						SELECT_MEDIA);

			}
		});

		Button moments = (Button) findViewById(R.id.button1);
		moments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), Moments.class);
				startActivity(i);

			}
		});

	}

	/*
	 * private String getPath(Uri uri) { String[] projection = {
	 * MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE,
	 * MediaStore.Video.Media.DURATION }; Cursor cursor = managedQuery(uri,
	 * projection, null, null, null); cursor.moveToFirst(); String filePath =
	 * cursor.getString(cursor
	 * .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); return filePath; }
	 */

	// url = file path or whatever suitable URL you want.
	public String getMimeType(File file) {
		Uri uri = Uri.fromFile(file);
		Log.d("URI", uri.toString());
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		Log.d("TYPE", type);
		return type;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == SELECT_MEDIA) {
				Uri selectedVideoUri = data.getData();
				videoPath = selectedVideoUri.getPath();

				UploadImage uI = new UploadImage();
				uI.execute();
			}
		}
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	class UploadImage extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				flag = false;
				pDialog = new ProgressDialog(Dashboard.this);
				pDialog.setMessage("Subiendo Media");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			} catch (Exception e) {
			}

		}

		@Override
		protected String doInBackground(String... args) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(UPLOAD_SERVICE);

				File file = new File(videoPath);
				ContentType cT = ContentType.create(getMimeType(file));

				FileBody filebodyVideo = new FileBody(file);
				// FileBody filebodyVideo = new FileBody(file, cT);

				// Log.d("FILEBODYTYPE",filebodyVideo.getFilename());
				StringBody title = new StringBody("Filename: " + videoPath);
				StringBody description = new StringBody(
						"This is a description of the video");

				MultipartEntity reqEntity = new MultipartEntity();
				reqEntity.addPart("file", filebodyVideo);
				reqEntity.addPart("client_id", new StringBody(main.user_ID));
				reqEntity.addPart("submit", new StringBody("1"));
				httppost.setEntity(reqEntity);

				// DEBUG
				// System.out.println( "executing request " +
				// httppost.getRequestLine( ) );
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				Log.d("request response", convertStreamToString(is));

				HttpEntity resEntity = response.getEntity();

				// DEBUG
				/*
				 * System.out.println( response.getStatusLine( ) ); if
				 * (resEntity != null) { System.out.println(
				 * EntityUtils.toString( resEntity ) ); } // end if
				 */

				if (resEntity != null) {
					resEntity.consumeContent();
				} // end if

				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				flag = true;
				Log.d("Error", e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				public void run() {
					pDialog.dismiss();

					String mensaje = "";
					if (!flag) {
						mensaje = "Fichero subido con exito :-)";
						// Toast.makeText(getApplicationContext(),
						// "Fichero subido con exito :)", Toast.LENGTH_LONG);
					} else {
						mensaje = "Fichero NO subido con exito :_(";
						// Toast.makeText(getApplicationContext(),
						// "Fichero NO subido con exito :(", Toast.LENGTH_LONG);
					}

					Toast toast1 = Toast.makeText(getApplicationContext(),
							mensaje, Toast.LENGTH_LONG);

					toast1.show();
				}
			});
		}
	}

}
