/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.jotracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context context;
	private Geocoder gcd ;
	private String addressText = null;
	private double latitude = 0;
	private double longitude = 0;
	
	public MapItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
	}

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}
	

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		String address = getAddress(item.getPoint());
		Toast.makeText(context, String.valueOf(address).toString(),Toast.LENGTH_LONG).show();
		return true;
	}
	
	public String  getAddress(GeoPoint point){
		setLatitude(point.getLatitudeE6()/1E6);
		setLongitude(point.getLongitudeE6()/1E6);
		try{
			gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = 
                gcd.getFromLocation(getLatitude(), getLongitude(),1);
            if (addresses.size() > 0) {
                StringBuilder result = new StringBuilder();
                result.append("Lat: "+getLatitude()+" Long: "+getLongitude()+"\n"+context.getString(R.string.address)+":\n");
                for(int i = 0; i < addresses.size(); i++){
                    Address address =  addresses.get(i);
                    int maxIndex = address.getMaxAddressLineIndex();
                    for (int x = 0; x <= maxIndex; x++ ){
                        result.append(address.getAddressLine(x));
                        result.append(",");
                    }               
                    //result.append(address.getPostalCode());
                }
                addressText = result.toString();
            }
        }
        catch(IOException ex){
            addressText = ex.getMessage().toString();
        }
		return addressText;
    }

	private double getLatitude() {
		return latitude;
	}

	private void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	private double getLongitude() {
		return longitude;
	}

	private void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
