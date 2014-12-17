package be.trojkasoftware.osmdroid.directions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.views.MapView;

import be.trojkasoftware.parkinkortrijk.R;

/**
 * Created by sergedesmedt on 17/12/14.
 */
public class DirectionsMapVisualizer {
    //olyline roadLine;
    //FolderOverlay instructionPoints;

    MapView mapView;
    Context context;

    public DirectionsMapVisualizer(MapView mapView, Context context)
    {
        this.mapView = mapView;
        this.context = context;
    }

    public Polyline getRouteOverlay(Road road)
    {
        Polyline roadLine = RoadManager.buildRoadOverlay(road, this.context);
        return  roadLine;
    }

    public FolderOverlay getInstructionsOverlay(Road road)
    {
        FolderOverlay instructionPoints = new FolderOverlay(this.context);

        for(RoadNode node : road.mNodes)
        {
            Marker marker = getMarkerForWaypoint(road, node);
            instructionPoints.add(marker);
        }

        return instructionPoints;
    }

    private Marker getMarkerForWaypoint(Road road, RoadNode node)
    {
        Drawable icon = this.context.getResources().getDrawable(R.drawable.marker_node);

        Marker marker = new Marker(this.mapView);

        marker.setPosition(node.mLocation);
        marker.setIcon(icon);

        return marker;
    }
}
