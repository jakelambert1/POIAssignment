package com.example.a0lambj41.poiassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {


    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    private List<POIs> listPOIs;
    Map<String, Drawable> markersType;
    private boolean savetoweb;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);

        mv = (MapView) findViewById(R.id.map1);

        mv.setBuiltInZoomControls(true);
        mv.getController().setZoom(13);
        mv.getController().setCenter(new GeoPoint(50.9136, -1.4112));

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);
        this.listPOIs = new ArrayList<>();
        //this.markersType = new HashMap<>();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.addpoi) {
            // react to the menu item being selected...
            // Launch second activity
            Intent intent = new Intent(this, AddPOIActivity.class);
            startActivityForResult(intent, 0);
            return true;
        } else if (item.getItemId() == R.id.savepoi) {
            savePOIs();
            return true;
        } else if (item.getItemId() == R.id.preferences) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }else if (item.getItemId() == R.id.loadpoi) {
            loadPOIs();
            return true;
        }  else if (item.getItemId() == R.id.loadPOIweb) {
            LoadPOIWeb load = new LoadPOIWeb();
            load.execute();
            return true;
        }
        return false;
    }




    public class POIs {
        private String name, type, description;
        private double latitude, longitude;

        public POIs(String nameArray, String typeArray, String descriptionArray, double latArray, double longArray) {
            this.name = nameArray; this.type = typeArray; this.description = descriptionArray; this.latitude = latArray; this.longitude = longArray;
        }

        public String getName() {
            return this.name;
        }
        public String getType() {
            return this.type;
        }
        public String getDescription() {
            return this.description;
        }
        public Double getLatitude() {
            return this.latitude;
        }
        public Double getLongitude() {
            return this.longitude;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            Bundle bundle = intent.getExtras();

            String markerName = bundle.getString("com.example.pointofinterestapp.name");
            String markerType = bundle.getString("com.example.pointofinterestapp.type");
            String markerDesc = bundle.getString("com.example.pointofinterestapp.desc");
            double latitude = mv.getMapCenter().getLatitude();
            double longitude = mv.getMapCenter().getLongitude();

            OverlayItem addpoi = new OverlayItem(markerName, markerType + markerDesc, new GeoPoint(latitude, longitude));
            this.listPOIs.add(new POIs(markerName, markerType, markerDesc, latitude, longitude));
            items.addItem(addpoi);

            mv.getOverlays().add(items);
            mv.refreshDrawableState();

            Toast.makeText(MainActivity.this, "Marker Created!", Toast.LENGTH_SHORT).show();

        } else if (requestCode == 1) {
        }
    }

    private void savePOIs() {

        if (savetoweb != true)
        {
            String savedDetails = "";
            for (POIs poi:listPOIs)
            {
                savedDetails += poi.getName() + "," + poi.getType() + "," + poi.getDescription() + "," + poi.getLatitude() + "," + poi.getLongitude() + "\n";
            }
            try
            {

                PrintWriter pw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/markers.csv", true));
                pw.println(savedDetails);
                pw.flush();
                pw.close();
            } catch (IOException e)
            {
                new AlertDialog.Builder(this).setMessage("ERROR: " + e).setPositiveButton("OK", null).show();
            }
            Toast.makeText(MainActivity.this, "Marker Added!", Toast.LENGTH_LONG).show();
        } else {
            savePOIsWeb save = new savePOIsWeb();
            save.execute();
        }
    }

    class savePOIsWeb extends AsyncTask<Void, Void, String>
    {
        @Override
        public String doInBackground(Void... params)
                    {

                        System.out.println("hello");
                        HttpURLConnection conn = null;
                        try
                        {
                            URL url = new URL("http://www.free-map.org.uk/course/mad/ws/add.php");
                            conn = (HttpURLConnection) url.openConnection();

                            String postDetails = "";
                            for (POIs p : listPOIs) {
                                postDetails = "year=17&username=user032" + "&name=" + p.getName() + "&type=" + p.getType() + "&description" + p.getDescription() + "&lat=" + p.getLatitude() + "&lon=" + p.getLongitude() + "\n";
                            }

                            // For POST
                            conn.setDoOutput(true);
                            conn.setFixedLengthStreamingMode(postDetails.length());

                            OutputStream out = null;
                            out = conn.getOutputStream();

                            System.out.println("postData: " + postDetails);
                            out.write(postDetails.getBytes());

                            if (conn.getResponseCode() == 200) {
                                InputStream in = conn.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                String all = "", line;
                                while ((line = br.readLine()) != null)
                                    all += line;
                    return all;

                } else {
                    System.out.println("error");
                    return "HTTP ERROR: " + conn.getResponseCode();
                }

            } catch (IOException e) {
                return e.toString();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        }
    }



    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        savePOIs();
    }

    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savetoweb = prefs.getBoolean("savetoweb", false);
    }


    private void loadPOIs() {
        try {
            Toast.makeText(MainActivity.this, "Markers Loaded", Toast.LENGTH_LONG).show();
            FileReader fr = new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/markers.csv");
            BufferedReader reader = new BufferedReader(fr);
            String line = "";

            while ((line = reader.readLine()) != null) {
                String[] components = line.split(",");

                if (components.length > 4) {

                    //Toast.makeText(MainActivity.this, line, Toast.LENGTH_LONG).show();

                    String name = components[0];
                    String type = components[1];
                    String description = components[2];
                    double latitude = Double.parseDouble(components[3]);
                    double longitude = Double.parseDouble(components[4]);

                    OverlayItem loadingitem = new OverlayItem(name, type + description, new GeoPoint(latitude, longitude));
                    items.addItem(loadingitem);
                    mv.getOverlays().add(items);
                }
            }
            reader.close();
        } catch (IOException e) {
            new AlertDialog.Builder(this).setMessage("ERROR: " + e).setPositiveButton("OK", null).show();
        }
    }





    class LoadPOIWeb extends AsyncTask<Void, Void, String> {

        public String doInBackground(Void... unused) {
            HttpURLConnection conn = null;
            try {
                // Open the connection to tyhe URL
                URL urlObj = new URL("http://www.free-map.org.uk/course/mad/ws/get.php?year=17&username=user032&format=json");
                conn = (HttpURLConnection) urlObj.openConnection();
                InputStream in = conn.getInputStream();

                if (conn.getResponseCode() == 200) {
                    // get back json data
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String jsonData = "", line;

                    while ((line = br.readLine()) != null)
                        jsonData += line;

                    return jsonData;

                } else
                    return "HTTP ERROR: " + conn.getResponseCode();

            } catch (Exception e) {
                e.toString();
            }
            finally
            {
                if(conn!=null)
                    conn.disconnect();
            }
            return "Error Something Went Wrong";
        }

        public void onPostExecute(String jsonData) {
            try {
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    String name = object.getString("name");
                    String type = object.getString("type");
                    String description = object.getString("description");
                    double latitude = object.getDouble("lat");
                    double longitude = object.getDouble("lon");

                    OverlayItem loadMarkers = new OverlayItem(name, type + description, new GeoPoint(latitude, longitude));

                    //POIs addMarkers = new MainActivity.POIs(name, type, description, latitude, longitude);

                    items.addItem(loadMarkers);

                    mv.getOverlays().add(items);
                }
                mv.refreshDrawableState();

                Toast.makeText(MainActivity.this, "Markers Loaded from Web!", Toast.LENGTH_LONG).show();
            }
            catch (JSONException e)
            {
                new AlertDialog.Builder(MainActivity.this).setMessage(e.toString()).setPositiveButton("OK", null).show();
            }
        }

    }

}


































