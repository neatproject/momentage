package com.test.momentage;

import gson.JSONMedia;
import gson.JSONRoot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Moments extends Activity {
	private ProgressDialog pDialog;
	private JSONRoot response;
	private List<HashMap<String, String>> medias_maps;
	private ListView mediaList;
	public static final String AUTENTICATE_SERVICE = "https://familymelon.com/momentage/fetch.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moments_list);

		mediaList = (ListView) findViewById(R.id.listView1);

		LoadData lD = new LoadData();
		lD.execute();

	}

	public InputStream retrieveStreamPost(String url, List<NameValuePair> params) {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost getRequest = new HttpPost(url);

		try {
			getRequest.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// Log.w(getClass().getSimpleName(), "Error " + statusCode +
				// " for URL " + url);
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		}

		catch (IOException e) {
			getRequest.abort();
			// Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;

	}

	class LoadData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			try {
				pDialog = new ProgressDialog(Moments.this);
				pDialog.setMessage("Leyendo Media");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			} catch (Exception e) {
			}
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				// System.gc();
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				//params2.add(new BasicNameValuePair("client_id", main.user_ID));

				InputStream source = retrieveStreamPost(AUTENTICATE_SERVICE+"?client_id="+main.user_ID,
						params2);
				
				

				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				
				/**/
				//String line;
				//BufferedReader br = new BufferedReader(new InputStreamReader(source));
				//while ((line = br.readLine()) != null) {
					//Log.d("Line -------> ",line);
				//}
				/**/
				
				
				response = gson.fromJson(reader, JSONRoot.class);
				
				Log.d("success", response.success+"");
				Log.d("uid", response.data.uid);
				
				
				medias_maps = new ArrayList<HashMap<String, String>>();

				for (JSONMedia media : response.data.media) {

					HashMap<String, String> media_item = new HashMap<String, String>();
					media_item.put("id", media.id);
					media_item.put("src", media.src);

					medias_maps.add(media_item);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.gc();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			SimpleAdapter dataAdapter = new SimpleAdapter(Moments.this,
					medias_maps, R.layout.media_item, new String[] { "id" },
					new int[] { R.id.media_label });

			mediaList.setAdapter(dataAdapter);
			
			mediaList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int pos,
						long id) {//pos
					
					
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(medias_maps.get(pos).get("src")));
					startActivity(i);
					
				}
			});

			pDialog.dismiss();
		}

	}

}
