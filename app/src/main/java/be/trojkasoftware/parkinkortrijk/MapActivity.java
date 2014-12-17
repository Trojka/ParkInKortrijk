package be.trojkasoftware.parkinkortrijk;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import be.trojkasoftware.osmdroid.directions.DirectionsMapVisualizer;
import be.trojkasoftware.osmdroid.directions.DirectionsProvider;

public class MapActivity extends Activity implements MapViewConstants {

    private MapView mapView;
    private MapController mapController;
    //private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    protected ResourceProxy mResourceProxy;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this;

        setContentView(R.layout.mapview);

        mapView = (MapView) this.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        mResourceProxy = new ResourceProxyImpl(this.getLayoutInflater().getContext().getApplicationContext());

        mapController = (MapController)this.mapView.getController();
        mapController.setZoom(13);
        GeoPoint geoLocKortrijk = new GeoPoint(50833333, 3266667);
        GeoPoint geoLocBrugge =   new GeoPoint(51130000, 3140007);
        mapController.setCenter(geoLocKortrijk);

        //this.mMyLocationOverlay = new SimpleLocationOverlay(this);
        //this.mapView.getOverlays().add(mMyLocationOverlay);

        this.mScaleBarOverlay = new ScaleBarOverlay(this);
        //this.mapView.getOverlays().add(mScaleBarOverlay);

        //loadPage();
        loadDirections();
    }

    public void loadDirections() {
        GeoPoint geoLocKortrijk = new GeoPoint(50833333, 3266667);
        GeoPoint geoLocBrugge =   new GeoPoint(51130000, 3140007);

        ArrayList<GeoPoint> wayPoints = new ArrayList<GeoPoint>();
        wayPoints.add(geoLocKortrijk);
        wayPoints.add(geoLocBrugge);
        new GetDirections().execute(wayPoints);
    }

    public void loadPage() {
        //new DownloadXmlTask().execute("http://www.parkodata.be/OpenData/parko_info.xml");
        new DownloadXmlTask().execute("parko_info.xml");
    }

    private class GetDirections extends AsyncTask<ArrayList<GeoPoint>, Void, Road> {
        @Override
        protected Road doInBackground(ArrayList<GeoPoint>... waypoints) {

            DirectionsProvider directionsProvider = new DirectionsProvider();
            directionsProvider.addWayPoints(waypoints[0]);

            Road returnValue = directionsProvider.getDirections();

            return returnValue;
        }

        @Override
        protected void onPostExecute(Road result) {
            DirectionsMapVisualizer visualizer = new DirectionsMapVisualizer(MapActivity.this.mapView, MapActivity.this);

            //Polyline roadLine = visualizer.getRouteOverlay(result);
            //mapView.getOverlays().add(roadLine);

            FolderOverlay instructionPoints = visualizer.getInstructionsOverlay(result);
            mapView.getOverlays().add(instructionPoints);

        }

    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, List<ParkoDataParser.Entry>> {
        @Override
        protected List<ParkoDataParser.Entry> doInBackground(String... targets) {
            List<ParkoDataParser.Entry> returnValue = null;
            try {
                //return loadXmlFromNetwork(targets[0]);
                returnValue = loadXmlFromAssets(targets[0]);
            } catch (IOException e) {
                Log.d("EXCEPTION(IO)", e.getMessage());
                //returnValue = "Oeps, io foutje: " + e.getMessage();
            } catch (XmlPullParserException e) {
                Log.d("EXCEPTION(XML)", e.getMessage());
                //returnValue = "Oeps, xml foutje: " + x.getMessage();
            }

            return returnValue;
        }

        @Override
        protected void onPostExecute(List<ParkoDataParser.Entry> result) {
		    /* Itemized Overlay */
            {
			    /* Create a static ItemizedOverlay showing some Markers on various cities. */
                final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
                for(ParkoDataParser.Entry entry : result) {
                    items.add(new OverlayItem(entry.name,
                            "Description",
                            new GeoPoint(Double.parseDouble(entry.latitude),
                                    Double.parseDouble(entry.longitude))));
                }

			    /* OnTapListener for the Markers, shows a simple Toast. */
                ItemizedOverlayWithFocus<OverlayItem> myLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                        new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                            @Override
                            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                Toast.makeText(
                                        MapActivity.this,
                                        "Item '" + item.getTitle() + "' (index=" + index
                                                + ") got single tapped up", Toast.LENGTH_LONG).show();
                                return true;
                            }

                            @Override
                            public boolean onItemLongPress(final int index, final OverlayItem item) {
                                Toast.makeText(
                                        MapActivity.this,
                                        "Item '" + item.getTitle() + "' (index=" + index
                                                + ") got long pressed", Toast.LENGTH_LONG).show();
                                return false;
                            }
                        }, mResourceProxy);

                myLocationOverlay.setFocusItemsOnTap(true);
                myLocationOverlay.setFocusedItem(0);

                mapView.getOverlays().add(myLocationOverlay);

                //mRotationGestureOverlay = new RotationGestureOverlay(context, mMapView);
                //mRotationGestureOverlay.setEnabled(false);
                //mapView.getOverlays().add(mRotationGestureOverlay);
            }
        }
    }

    private List<ParkoDataParser.Entry> loadXmlFromAssets(String assetString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        ParkoDataParser parkoDataXmlParser = new ParkoDataParser();
        List<ParkoDataParser.Entry> entries = null;

        try {
            stream = downloadAsset(assetString);
            entries = parkoDataXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }


    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadAsset(String assetString) throws IOException {
        InputStream stream = this.getAssets().open(assetString);

        return stream;
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private List<ParkoDataParser.Entry> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        ParkoDataParser parkoDataXmlParser = new ParkoDataParser();
        List<ParkoDataParser.Entry> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = parkoDataXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        //conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
