package be.trojkasoftware.parkinkortrijk;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
//import org.osmdroid.views.overlay.ScaleBarOverlay;
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

public class MapActivity extends Activity implements MapViewConstants {

    private MapView mapView;
    private MapController mapController;
    //private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
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


//		/* Itemized Overlay */
//        {
//			/* Create a static ItemizedOverlay showing some Markers on various cities. */
//            final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//            items.add(new OverlayItem("Hannover", "Tiny SampleDescription", new GeoPoint(52370816,
//                    9735936))); // Hannover
//            items.add(new OverlayItem("Kortrijk", "P Schouwburg", new GeoPoint(50.82612384626342,
//                    3.26671018966681))); // Kortrijk
//            //items.add(new OverlayItem("Berlin", "This is a relatively short SampleDescription.",
//            //        new GeoPoint(52518333, 13408333))); // Berlin
//            //items.add(new OverlayItem(
//            //        "Washington",
//            //        "This SampleDescription is a pretty long one. Almost as long as a the great wall in china.",
//            //        new GeoPoint(38895000, -77036667))); // Washington
//            //items.add(new OverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300,
//            //        -122419200))); // San Francisco
//
//			/* OnTapListener for the Markers, shows a simple Toast. */
//            ItemizedOverlayWithFocus<OverlayItem> myLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
//                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//                        @Override
//                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                            Toast.makeText(
//                                    context,
//                                    "Item '" + item.getTitle() + "' (index=" + index
//                                            + ") got single tapped up", Toast.LENGTH_LONG).show();
//                            return true;
//                        }
//
//                        @Override
//                        public boolean onItemLongPress(final int index, final OverlayItem item) {
//                            Toast.makeText(
//                                    context,
//                                    "Item '" + item.getTitle() + "' (index=" + index
//                                            + ") got long pressed", Toast.LENGTH_LONG).show();
//                            return false;
//                        }
//                    }, mResourceProxy);
//
//            myLocationOverlay.setFocusItemsOnTap(true);
//            myLocationOverlay.setFocusedItem(0);
//
//            mapView.getOverlays().add(myLocationOverlay);
//
//            //mRotationGestureOverlay = new RotationGestureOverlay(context, mMapView);
//            //mRotationGestureOverlay.setEnabled(false);
//            //mapView.getOverlays().add(mRotationGestureOverlay);
//        }


        mapController = (MapController)this.mapView.getController();
        mapController.setZoom(13);
        GeoPoint gPt = new GeoPoint(51500000, -150000);
        //Centre map near to Hyde Park Corner, London
        mapController.setCenter(gPt);

        //this.mMyLocationOverlay = new SimpleLocationOverlay(this);
        //this.mapView.getOverlays().add(mMyLocationOverlay);

        //this.mScaleBarOverlay = new ScaleBarOverlay(this);
        //this.mapView.getOverlays().add(mScaleBarOverlay);

        loadPage();
    }

    public void loadPage() {
        //new DownloadXmlTask().execute("http://stackoverflow.com/feeds/tag?tagnames=android&sort=newest");
        //new DownloadXmlTask().execute("http://www.parkodata.be/OpenData/parko_info.xml");
        new DownloadXmlTask().execute("parko_info.xml");
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

        StringBuilder htmlString = new StringBuilder();

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

//        for (ParkoDataParser.Entry entry : entries) {
//            htmlString.append("<p><a href='");
//            htmlString.append(entry.link);
//            htmlString.append("'>" + entry.title + "</a></p>");
//            // If the user set the preference to include summary text,
//            // adds it to the display.
////            if (pref) {
////                htmlString.append(entry.summary);
////            }
//        }
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
//        String title = null;
//        String url = null;
//        String summary = null;
//        Calendar rightNow = Calendar.getInstance();
//        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");

//        // Checks whether the user set the preference to include summary text
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
//        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
//        htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
//                formatter.format(rightNow.getTime()) + "</em>");

        try {
            stream = downloadUrl(urlString);
            entries = parkoDataXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

//        // StackOverflowXmlParser returns a List (called "entries") of Entry objects.
//        // Each Entry object represents a single post in the XML feed.
//        // This section processes the entries list to combine each entry with HTML markup.
//        // Each entry is displayed in the UI as a link that optionally includes
//        // a text summary.
//        for (ParkoDataParser.Entry entry : entries) {
//            htmlString.append("<p><a href='");
//            htmlString.append(entry.link);
//            htmlString.append("'>" + entry.title + "</a></p>");
//            // If the user set the preference to include summary text,
//            // adds it to the display.
////            if (pref) {
////                htmlString.append(entry.summary);
////            }
//        }
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
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
