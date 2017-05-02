package com.example.a0lambj41.poiassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    private List<POIs> listPOIs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);

        mv = (MapView)findViewById(R.id.map1);

        mv.setBuiltInZoomControls(true);
        mv.getController().setZoom(14);
        mv.getController().setCenter(new GeoPoint(50.9,-1.4));

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);

        this.listPOIs = new ArrayList<>();
    }

    private class POIs {
        private String name, type, description;
        private double longitude, latitude;

        public POIs(String nameArray, String typeArray, String descriptionArray, double longArray, double latArray) {
            this.name = nameArray;
            this.type = typeArray;
            this.description = descriptionArray;
            this.longitude = longArray;
            this.latitude = latArray;
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

        public Double getLongitude() {
            return this.longitude;
        }

        public Double getLatitude() {
            return this.latitude;
        }
    }



    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }



    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.addpoi) {
            // react to the menu item being selected...
            // Launch second activity
            Intent intent = new Intent(this, AddPOIActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        else if(item.getItemId() == R.id.save) {

            System.out.println("Saving.");
            SaveTask saveTask = new SaveTask();
            saveTask.execute("places.txt");
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == 0) {
            Bundle bundle = intent.getExtras();

            String poiname = bundle.getString("com.example.pointofinterestapp.name");
            String poitype = bundle.getString("com.example.pointofinterestapp.type");
            String poidesc = bundle.getString("com.example.pointofinterestapp.desc");

            double latitude = mv.getMapCenter().getLatitude();
            double longitude = mv.getMapCenter().getLongitude();

            OverlayItem addpoi = new OverlayItem(poiname, poitype + poidesc, new GeoPoint(latitude, longitude));

            this.listPOIs.add(new POIs(poiname, poitype, poidesc, latitude, longitude));

            items.addItem(addpoi);

            mv.getOverlays().add(items);

            mv.refreshDrawableState();

            Toast.makeText(MainActivity.this, "Marker Created!", Toast.LENGTH_SHORT).show();

        }else if(requestCode == 1) {
        }
    }

    class SaveTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            String message = "Saved successfully.";
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + params[0]);
                if(!file.exists())
                    file.getParentFile().mkdirs();
                PrintWriter pw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/" + params[0]));
                int size = items.size();
                for (int i = 0; i < size; i++)
                {
                    OverlayItem item = items.getItem(i);
                    pw.write(item.getTitle() + "," + item.getSnippet() + "," + item.getPoint().getLatitude() + "," + item.getPoint().getLongitude());
                    pw.write("\n");
                }
                pw.close();

            } catch(IOException e) {
                message = e.toString();
            }
            return message;
        }

        public void onPostExecute(String message)
        {
            System.out.println(message);
        }

    }
}
