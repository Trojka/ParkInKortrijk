package be.trojkasoftware.osmdroid.directions;


import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class DirectionsProvider {

    public DirectionsProvider () {
        wayPoints = new ArrayList<GeoPoint>();
    }

    public void addWayPoint(GeoPoint wayPoint)
    {
        this.wayPoints.add(wayPoint);
    }

    public void addWayPoints(ArrayList<GeoPoint> wayPoints)
    {
        this.wayPoints.addAll(wayPoints);
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
