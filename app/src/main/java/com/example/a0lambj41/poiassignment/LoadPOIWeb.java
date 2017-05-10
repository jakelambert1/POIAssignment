package com.example.a0lambj41.poiassignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.osmdroid.util.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 0lambj41 on 10/05/2017.
 */
public class LoadPOIWeb extends AppCompatActivity implements View.onClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_web);

        Button loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText restaurantEditText = (EditText) findViewById(R.id.restaurantEditText);
        String load = restaurantEditText.getText().toString();

        (new GetSongAsyncTask()).execute(load);
    }

    // 1st parameter to AsyncTaak is input data type for the  doInBackground method
    // 2nd parameter to AsyncTask is the progress data type
    // 3rd parameter to AsyncTask is the return type of doInBackground method
    class GetRestaurantAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String load = params[0];

            try {
                // Open the connection to tyhe URL
                String url = "http://www.free-map.org.uk/course/mad/ws/get.php?year=17&username=user002&type=" + load + "&format=json";
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                if (connection.getResponseCode() == 200) {
                    // get back json data
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String jsonData = "";
                    String line = br.readLine();

                    while (line != null) {
                        jsonData += line;
                        line = br.readLine();
                    }

                    JSONArray jsonArray = new JSONArray(jsonData);

                    String result = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        String name = object.getString("name");
                        String type = object.getString("type");
                        String description = object.getString("description");
                        double latitude = object.getString("lat");
                        double longitude = object.getString("long");

                        /*String pretty = "Name of Place: " + name;
                        pretty += ", Type: " + type;
                        pretty += ", Description: " + description;
                        pretty += ", Latitude: " + latitude;
                        pretty += ", Longitude: " + longitude + "\n";**/
                        OverlayItem loadMarkers = new OverlayItem(name, type + description, GeoPoint(latitude, longitude));

                    }
                    //result += pretty;
                }
                return result;

            }
            else {
                return connection.getResponseCode() + " Error! ";
            }
        }

        catch(IOException e){
            return "Error! " + e.getMessage();
        }

        catch(JSONException e){
            return "Error! " + e.getMessage();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // display the string the result text view
            TextView resultTextView = (TextView) findViewById(R.id.resultTextView);
            resultTextView.setText(s);
        }
    }