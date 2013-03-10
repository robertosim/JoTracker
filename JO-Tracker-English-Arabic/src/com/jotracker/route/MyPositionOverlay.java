package com.jotracker.route;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MyPositionOverlay extends ItemizedOverlay<OverlayItem> {

public List<GeoPoint> geoPointsArrayList = new ArrayList<GeoPoint>();
public ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
Context context;
int listSize = overlayItemList.size();
//Vector <overlayItemList> vectorList;
public MyPositionOverlay(Drawable marker, Context c) {
    super(boundCenterBottom(marker));
    // TODO Auto-generated constructor stub
    populate();
    context = c;
}
private final int mRadius = 5;
Location location;
public Location getLocation() {
    return location;
}
public void setLocation(Location location) {;
this.location = location;
}

public void addItem(GeoPoint point, String title, String snippet){
    OverlayItem newItem = new OverlayItem(point, title, snippet);
    overlayItemList.add(newItem);
    populate();
}
@Override
public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    Projection projection = mapView.getProjection();
    Double latitude = location.getLatitude()*1E6;
    Double longitude = location.getLongitude()*1E6;
    GeoPoint geoPoint; 
    geoPoint = new 
            GeoPoint(latitude.intValue(),longitude.intValue());
    GeoPoint prePoint = null, currentPoint = null;
    Path linePath = new Path();
    
    Point screenCoords = new Point();
    Point screenCoords2 = new Point();
    Point lastPoint = new Point();
    Point linePoint = new Point();
    if (shadow == false) {
        // Get the current location    
        // Convert the location to screen pixels     
        Point point = new Point();
        projection.toPixels(geoPoint, point);
        RectF oval = new RectF(point.x - mRadius, point.y - mRadius, 
                point.x + mRadius, point.y + mRadius);
        // Setup the paint
        Paint paint = new Paint();
        paint.setARGB(250, 255, 255, 255);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        //Text set up
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        Paint backPaint = new Paint();
        backPaint.setARGB(175, 50, 50, 50);
        backPaint.setAntiAlias(true);
        RectF backRect = new RectF(point.x + 2 + mRadius, 
                point.y - 3*mRadius,
                point.x + 65, point.y + mRadius);
        // Draw the marker    
        canvas.drawOval(oval, paint);
        canvas.drawRoundRect(backRect, 5, 5, backPaint);
        canvas.drawText("Here I Am", 
                point.x + 2*mRadius, point.y, 
                textPaint);

        for (int j = 1; j < overlayItemList.size();j++){
            linePath.setLastPoint(4, j - 1);
            linePath.lineTo(4, j);
        }
        linePath.close();
        canvas.drawPath(linePath, textPaint);
    } // End of If statement
    super.draw(canvas, mapView, shadow);
} // End of draw method
public void drawLine(MapView mapView){

    System.out.println("Drawing line");
    Path linePath = new Path();
    Canvas lineCanvas = new Canvas();
    Projection lineProjection = mapView.getProjection();


    //Line Settings
    Paint linePaint = new Paint();
    linePaint.setStyle(Style.FILL);
    linePaint.setStrokeWidth(2);
    linePaint.setARGB(0, 0, 0, 255);
    linePaint.setAntiAlias(true);
    linePaint.setStrokeJoin(Paint.Join.ROUND);
    linePaint.setStrokeCap(Paint.Cap.ROUND);
    /****************************************************************************/

    for (int k = 0; k<overlayItemList.size(); k++){
        if(k == overlayItemList.size() -1){
            break;
        }
        Point from = new Point();
        Point to = new Point();

        //lineProjection.toPixels(overlayItemList.get(k), from);
        //lineProjection.toPixels(overlayItemList.get(k + 1), to);

        linePath.moveTo(from.x, from.y);
        linePath.lineTo(to.x, to.y);
    }

    lineCanvas.drawPath(linePath, linePaint);


}
@Override
protected OverlayItem createItem(int i) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public int size() {
	// TODO Auto-generated method stub
	return 0;
}
}
