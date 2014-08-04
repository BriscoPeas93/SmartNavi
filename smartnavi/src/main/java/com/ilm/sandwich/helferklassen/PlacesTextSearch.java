package com.ilm.sandwich.helferklassen;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ilm.sandwich.Config;
import com.ilm.sandwich.GoogleMapActivity;
import com.ilm.sandwich.R;

import android.content.Context;
import android.graphics.BitmapFactory;

public class PlacesTextSearch {

	//private static final String	LOG_TAG	= "PlacesTextSearch";
	private static final String	TYPE	= "/textsearch";
	private static final String	FORMAT	= "/json";
	private Context context;
	
	public PlacesTextSearch(Context _context) {
		context = _context;
	}
	
	public JSONObject getDestinationCoordinates(String input) {

		JSONObject location = null;
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
			
		String randomAPI;
		if (Math.random() > 0.5) {
			//Log.d("Zufall_Text", "API MA");
			randomAPI = Config.PLACES_API_KEY_MA;
		} else {
			//Log.d("Zufall_Text", "API CH");
			randomAPI = Config.PLACES_API_KEY_CH;
		}
		
		try {

			StringBuilder sb = new StringBuilder(Config.PLACES_API_URL + TYPE + FORMAT);
			sb.append("?sensor=true&key=" + randomAPI);
			//sb.append("&components=country:" + Locale.getDefault().getLanguage());
			sb.append("&query=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
			
			// check for OVER_QUERY_LIMIT | already done in AutoComplete
			
		
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray results = jsonObj.getJSONArray("results");
			location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
			
			JSONArray types = results.getJSONObject(0).getJSONArray("types");
			
			for (int i = 0; i < types.length(); i++) {
				if (types.get(i).equals("street_address") || types.get(i).equals("locality")) {
					
					GoogleMapActivity.drawableZiel = BitmapFactory.decodeResource(context.getResources(), R.drawable.finish2);
					break;
					
				}else{
					// load custom target image from Places API
					//URL iconurl = new URL((String)results.getJSONObject(0).get("icon"));
					// change resource
					//Karte.drawableZiel = BitmapFactory.decodeStream(iconurl.openConnection().getInputStream());				
				}
			}
			

		} catch (MalformedURLException e) {
			//Log.e(LOG_TAG, "Error processing Places API URL TextSearch", e);
		} catch (IOException e) {
			//Log.e(LOG_TAG, "Error connecting to Places API TextSearch", e);
		} catch (JSONException e) {
			//Log.e(LOG_TAG, "Cannot process JSON results", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return location;
	}
}