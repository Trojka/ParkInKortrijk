package be.trojkasoftware.osmdroid.directions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

import be.trojkasoftware.parkinkortrijk.R;

/**
 * Created by sergedesmedt on 17/12/14.
 */
public class DirectionsMapVisualizer {

    MapView mapView;
    Context context;

    public DirectionsMapVisualizer(MapView mapView, Context context)
    {
        this.mapView = mapView;
        this.context = context;
    }

    public void addToMapFromRoad(Road road, ArrayList<GeoPoint> wayPoints)
    {
        if(road != null) {
            Overlay routeOverlay = getRouteOverlay(road);
            this.mapView.getOverlays().add(routeOverlay);

            Overlay instructionsOverlay = getInstructionsOverlay(road);
            this.mapView.getOverlays().add(instructionsOverlay);
        }

        if(wayPoints != null) {
            Overlay waypointsOverlay = getWaypointOverlay(wayPoints);
            this.mapView.getOverlays().add(waypointsOverlay);
        }
    }

    public Polyline getRouteOverlay(Road road)
    {
        Polyline routeOverlay = RoadManager.buildRoadOverlay(road, this.context);
        return  routeOverlay;
    }

    public FolderOverlay getWaypointOverlay(ArrayList<GeoPoint> wayPoints)
    {
        FolderOverlay wayPointOverlay = new FolderOverlay(this.context);

        int index = 0;
        int ofCount = wayPoints.size();
        for(index = 0; index < ofCount; index++)
        {
            GeoPoint wayPoint = wayPoints.get(index);
            Marker marker = getMarkerForWaypoint(wayPoint, index, ofCount);
            wayPointOverlay.add(marker);
        }

        return wayPointOverlay;
    }

    private Marker getMarkerForWaypoint(GeoPoint wayPoint, int index, int ofCount)
    {
        Drawable icon = this.context.getResources().getDrawable(R.drawable.marker_via);
        if(index == 0) {
            icon = this.context.getResources().getDrawable(R.drawable.marker_departure);
        }
        else if (index == ofCount-1) {
            icon = this.context.getResources().getDrawable(R.drawable.marker_destination);
        }

        Marker marker = new Marker(this.mapView);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setPosition(wayPoint);
        marker.setIcon(icon);

        return marker;
    }

    public FolderOverlay getInstructionsOverlay(Road road)
    {
        FolderOverlay instructionsOverlay = new FolderOverlay(this.context);

        for(RoadNode node : road.mNodes)
        {
            Marker marker = getMarkerForNode(road, node);
            instructionsOverlay.add(marker);
        }

        return instructionsOverlay;
    }

    private Marker getMarkerForNode(Road road, RoadNode node)
    {
        Drawable icon = this.context.getResources().getDrawable(R.drawable.marker_node);

        Marker marker = new Marker(this.mapView);

        marker.setPosition(node.mLocation);
        marker.setIcon(icon);

        return marker;
    }
}
