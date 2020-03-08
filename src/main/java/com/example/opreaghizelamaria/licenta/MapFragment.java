package com.example.opreaghizelamaria.licenta;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scaledrone.lib.HistoryRoomListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.maps.UiSettings.*;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, RoomListener {

    private static String text;
    private GoogleMap mMap;
    String n;
    Location mylocation;
    HashMap<String, Marker> hashMapMarker = new HashMap<>();

    private Boolean prima = true;
    int nr = 0;

    private Button btnRequestDirection;
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyDxBEEmr2G2dc4u0RDpcWngsA6b0nHJiSw";
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private LatLng destination = new LatLng(37.7814432, -122.4460177);



    private String channelID = "W8omkWoMZbzCoUAY";
    private String roomName = "observable-room";
    private TextView mesaj;
    private Scaledrone scaledrone;

    public MapFragment() {
    }


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment f = new MapFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        onLocationChanged(null);
        if (locationManager != null)
            System.out.println("");



        return v;


    }    ArrayList markerPoints= new ArrayList();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        uiSettings.setCompassEnabled(false);


        mMap.addMarker(new MarkerOptions().position(origin));
        mMap.addMarker(new MarkerOptions().position(destination));



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng latLng) {
                SharedPreferences sharedPreferences=((AppCompatActivity)getActivity()).getSharedPreferences("Start",MODE_PRIVATE);
                final String nume=sharedPreferences.getString("Name","null");

                final Dialog alert = new Dialog(getContext());
                alert.setContentView(R.layout.marcaj);
                alert.show();
                Button btn=alert.findViewById(R.id.button3);
                Button btn1=alert.findViewById(R.id.button4);
                mesaj=alert.findViewById(R.id.marcajMesaj);

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alert.cancel();
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marcaj);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        Date date = new Date();
                        String dateTime = dateFormat.format(date);
                        System.out.println("Current Date Time : " + dateTime);
                        MsgMap  msgMap= new MsgMap(mesaj.getText().toString(),nume,dateTime,latLng);
                        scaledrone.publish(roomName,msgMap);
                        markerOptions.title(mesaj.getText().toString());

                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        mMap.addMarker(markerOptions);
                        alert.cancel();
                    }
                });

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

                alert.getWindow().setAttributes(layoutParams);

            }
        });


        scaledrone = new Scaledrone(channelID);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");


                scaledrone.subscribe(roomName, (RoomListener) MapFragment.this, new SubscribeOptions(50)).listenToHistoryEvents(new HistoryRoomListener() {
                    @Override
                    public void onHistoryMessage(Room room, com.scaledrone.lib.Message recmessage) {

                        String dataReceiver = recmessage.getData().toString();

                        try {
                            JSONObject obj = new JSONObject(dataReceiver);
                            JSONObject obj1 =obj.getJSONObject("pozitie");
                            Double lat=Double.parseDouble(obj1.get("latitude").toString());
                            Double lng=Double.parseDouble(obj1.get("longitude").toString());
                            final LatLng latLng=new LatLng(lat,lng);
                            final String mesaj=obj.get("mesaj").toString();
                            nr++;

                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            Date date = new Date();
                            String dateTime = format.format(date);

                            String dataInit= obj.get("timp").toString();
                            Date d1 = null;
                            Date d2 = null;


                            try {
                                d1 = format.parse(dataInit);
                                d2 = format.parse(dateTime);
                                long diff = d2.getTime() - d1.getTime();

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000) % 24;
                                long diffDays = diff / (24 * 60 * 60 * 1000);

                                 if(diffDays<1&&diffHours<1&&diffMinutes<30){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marcaj);
                                            Bitmap b = bitmapdraw.getBitmap();
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                                            MarkerOptions markerOptions = new MarkerOptions();


                                            markerOptions.position(latLng);
                                            markerOptions.title(mesaj);
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                                            mMap.addMarker(markerOptions);

                                        }
                                    });


                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } catch (Exception e) {
                        }

                    }

                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });


        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {

            @Override
            public void onInfoWindowLongClick(Marker marker) {
                String infoTitle = marker.getTitle();
                System.out.println(marker.getPosition().latitude);
                if(mylocation!=null) {
                    final LatLng origin=new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
                    final LatLng destination=new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);

                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(destination)
                            .transportMode(TransportMode.DRIVING)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    if(direction.isOK()) {
                                        try{
                                            mMap.addMarker(new MarkerOptions().position(origin));
                                            mMap.addMarker(new MarkerOptions().position(destination));

                                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(), stepList, 5, Color.RED, 3, Color.BLUE);
                                            for (PolylineOptions polylineOption : polylineOptionList) {
                                                mMap.addPolyline(polylineOption);
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    else {


                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // TODO
                                    System.out.println(t.getMessage());

                                }
                            });

                }

            }
        });

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap!=null){
            mylocation=location;
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }

        }
    }



    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex+"    fail");
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {



        String dataReceiver=receivedMessage.getData().toString();
        try {
            JSONObject obj = new JSONObject(dataReceiver);
            JSONObject obj1 =obj.getJSONObject("pozitie");
            Double lat=Double.parseDouble(obj1.get("latitude").toString());
            Double lng=Double.parseDouble(obj1.get("longitude").toString());
            final LatLng latLng=new LatLng(lat,lng);
            final String mesaj=obj.get("mesaj").toString();
            nr++;

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            String dateTime = format.format(date);

            String dataInit= obj.get("timp").toString();
            Date d1 = null;
            Date d2 = null;


            try {
                d1 = format.parse(dataInit);
                d2 = format.parse(dateTime);
                long diff = d2.getTime() - d1.getTime();

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);

                System.out.print(diffDays + " days, ");
                System.out.print(diffHours + " hours, ");
                System.out.print(diffMinutes + " minutes, ");
                System.out.print(diffSeconds + " seconds.");
                if(diffDays<1&&diffHours<1&&diffMinutes<30){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marcaj);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

                            MarkerOptions markerOptions = new MarkerOptions();


                            markerOptions.position(latLng);
                            markerOptions.title(mesaj);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                            mMap.addMarker(markerOptions);

                        }
                    });


                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            System.out.println("nu e json");
        }

    }

}
class MsgMap{

    private String mesaj,utilizator;
    private String timp;
    private LatLng pozitie;

    public MsgMap(String mesaj,String utilizator, String timp, LatLng pozitie) {
        this.mesaj = mesaj;
        this.timp = timp;
        this.pozitie = pozitie;
        this.utilizator=utilizator;
    }
    public String getUtilizator(){
        return utilizator;
    }
    public String getMesaj() {
        return mesaj;
    }

    public String getTimp() {
        return timp;
    }

    public LatLng getPozitie() {
        return pozitie;
    }
}
