package com.sust.kinblood.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sust.kinblood.MainActivity;
import com.sust.kinblood.NeedDonorActivity;
import com.sust.kinblood.R;
import com.sust.kinblood.directionhelpers.FetchURL;
import com.sust.kinblood.directionhelpers.TaskLoadedCallback;

public class RequestStatusFragment extends Fragment  implements OnMapReadyCallback, TaskLoadedCallback {

    private Context context;

    private GoogleMap mMap;
    private String requestStatus_;
    private TextView requestStatus;
    private Polyline polyline;
    private MarkerOptions start=null, end=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request_status, container, false);

        context = getContext();

        requestStatus = root.findViewById(R.id.fragment_request_status_requestStatus_TextView);

        if (NeedDonorActivity.latLng != null){
            start = new MarkerOptions().position(NeedDonorActivity.latLng);
            end = new MarkerOptions().position(new LatLng(23.667491, 90.3208583));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_request_status_map_Fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        MainActivity.DOCUMENT_REFERENCE.get().addOnSuccessListener(documentSnapshot -> {
            requestStatus_ = documentSnapshot.getString("requestStatus");
            if (requestStatus_ != null){
                if (requestStatus_.equals("negative")){
                    requestStatus.setText(R.string.requestStatus_negative);
                }else if (requestStatus_.equals("processing")){
                    requestStatus.setText(R.string.requestStatus_processing);
                }else {
                    if (start != null && end != null) {
                        new FetchURL(context).execute(getUrl(start.getPosition(), end.getPosition()), "driving");
                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());

        return root;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + dest.latitude + "," + dest.longitude + "&key=" +
                getString(R.string.google_maps_key);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (start != null && end != null){
            mMap.addMarker(start);
            mMap.addMarker(end).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_donor));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(start.getPosition());
            builder.include(end.getPosition());
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if(polyline != null) {
            polyline.remove();
            polyline = mMap.addPolyline((PolylineOptions) values[0]);
        }
    }
}