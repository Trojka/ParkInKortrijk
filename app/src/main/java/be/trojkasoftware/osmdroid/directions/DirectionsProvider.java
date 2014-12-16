package be.trojkasoftware.osmdroid.directions;


import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class DirectionsProvider {

    public void setWayPoints(ArrayList<GeoPoint> wayPoints)
    {
        this.wayPoints = wayPoints;
    }

    public ArrayList<GeoPoint> getWayPoints()
    {
        return this.wayPoints;
    }

    public Road getDirections()
    {
        RoadManager roadManager = new OSRMRoadManager();
        return roadManager.getRoad(wayPoints);
    }

    private ArrayList<GeoPoint> wayPoints;
}
