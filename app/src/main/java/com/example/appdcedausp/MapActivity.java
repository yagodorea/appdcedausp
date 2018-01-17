package com.example.appdcedausp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by yago_ on 12/01/2018.
 */

public class MapActivity extends AppCompatActivity
    implements GoogleMap.OnMarkerClickListener
{

    private static final String TAG = MapActivity.class.getName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1307;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float FOCUS_ZOOM = 18.5f;
    private static final LatLng butantaInicio = new LatLng(-23.560919f,-46.7365807f);
    private static final LatLng sancaInicio = new LatLng(-22.0062709f,-47.8967704f);
    private static final LatLng eachInicio = new LatLng(-23.4824907f,-46.5007256f);
    private static final LatLng sanfranInicio = new LatLng(-23.5497903f,-46.637328f);
    private static final LatLng piracicInicio = new LatLng(-22.7111494f,-47.6306825f);
    private static final LatLng ribeiraoInicio = new LatLng(-21.168679f,-47.8622834f);
    private static final LatLng pirassuInicio = new LatLng(-21.9674265f,-47.467445f);
    private static final LatLng bauruInicio = new LatLng(-22.3345195f,-49.0671509f);
    private static final LatLng santosInicio = new LatLng(-22.0050875f,-47.9102792f);
    private static final LatLng lorenaInicio = new LatLng(-23.9424612f,-46.3327931f);
    private Boolean mLocationPermissionGranted = false;

    // Marcador
    private Marker mark;

    // Fragmentos
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DescriptionFragment descriptionFragment;

    // Elementos
    TextView descriptionTitle;
    TextView descriptionContent;
    FloatingActionButton fabDesc;
    FloatingActionButton fabGo;

    // Mapa e localização
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currLocation;

    // Settings
    SharedPreferences pref;
    private int campus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        campus = pref.getInt("Campus",0);

        getLocationPermission();
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            currLocation = (Location)task.getResult();

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),currLocation.getLongitude()),DEFAULT_ZOOM));
                            switch (campus) {
                                case 0: {
                                    moveCamera(butantaInicio, DEFAULT_ZOOM);
                                    break;
                                }case 1: {
                                    moveCamera(sancaInicio, DEFAULT_ZOOM);
                                    break;
                                }case 2: {
                                    moveCamera(eachInicio, DEFAULT_ZOOM);
                                    break;
                                }case 3: {
                                    moveCamera(sanfranInicio, DEFAULT_ZOOM);
                                    break;
                                }case 4: {
                                    moveCamera(piracicInicio, DEFAULT_ZOOM);
                                    break;
                                }case 5: {
                                    moveCamera(ribeiraoInicio, DEFAULT_ZOOM);
                                    break;
                                }case 6: {
                                    moveCamera(pirassuInicio, DEFAULT_ZOOM);
                                    break;
                                }case 7: {
                                    moveCamera(bauruInicio, DEFAULT_ZOOM);
                                    break;
                                }case 8: {
                                    moveCamera(santosInicio, DEFAULT_ZOOM);
                                    break;
                                }case 9: {
                                    moveCamera(lorenaInicio, DEFAULT_ZOOM);
                                    break;
                                }default : { moveCamera(new LatLng(currLocation.getLatitude(),currLocation.getLongitude()), DEFAULT_ZOOM); break; }
                            }
                        } else {
                            Toast.makeText(MapActivity.this,"Erro na localização!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
            .target(latLng)
            .zoom(zoom)
            .build()));
    }

    private void initMap() { // Prepara o mapa e atribui à variável mMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMarkerClickListener(MapActivity.this);

                // Se conseguirmos o mapa, pegar localização
                if (mLocationPermissionGranted) {
                    getDeviceLocation();

                    // Ativar "Minha Localização" (pontinho azul)
                    if(ContextCompat.checkSelfPermission(MapActivity.this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if(ContextCompat.checkSelfPermission(MapActivity.this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                    setMarkers();
                }
            }
        });
    }

    private void setMarkers() {
        Resources res = getResources();
        TypedArray lats, lons, icons;
        String[] titles,desc;
        switch(campus) {
            case 0: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 1: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 2: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 3: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 4: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 5: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 6: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 7: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 8: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }case 9: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
            }
            default:
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                break;
        }

        for(int i=0;i<res.getIntArray(R.array.markerNumber)[campus];i++)
        {
            Log.d(TAG, "Shazam! -> setMarkers: setando marcador " + titles[i]);
            //Bitmap small1 = Bitmap.createScaledBitmap(icon1.getBitmap(),100,100,false);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lats.getFloat(i,0f),lons.getFloat(i,0f)))
                    .title(titles[i])
                    .snippet(desc[i])
                    .icon(BitmapDescriptorFactory.fromResource(icons.getResourceId(i,-1))));
        }
        lats.recycle();
        lons.recycle();
        icons.recycle();
    }

    // Inflar fragmentos e dar zoom quando clicarem nos markers
    @Override
    public boolean onMarkerClick(Marker marker) {
        mark = marker;
        moveCamera(mark.getPosition(),FOCUS_ZOOM);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        descriptionTitle = findViewById(R.id.descTitle);
        descriptionContent = findViewById(R.id.descContent);

        fabDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Shazam! ->run: entering thread for animation");

                Log.d(TAG, "Shazam! ->run: animating in different thread");
                // Animate buttons
                AnimationSet sets = new AnimationSet(false);
                sets.addAnimation(AnimationUtils.loadAnimation(MapActivity.this,R.anim.anim_fadeout));
                sets.addAnimation(AnimationUtils.loadAnimation(MapActivity.this,R.anim.anim_gotobottom));

                View view = findViewById(R.id.description_fragment_container);
                view.startAnimation(sets);
                fabDesc.startAnimation(sets);

                sets.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fabDesc.setVisibility(View.GONE);
                        fabGo.setVisibility(View.GONE);

                        fragmentManager = getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.hide(descriptionFragment);
                        fragmentTransaction.commit();
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationStart(Animation animation) {}
                });
            }
        });

        fabGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fazer ligação com o aplicativo maps pra mostrar as direções
                double lat = mark.getPosition().latitude;
                double lon = mark.getPosition().longitude;
                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+lat+","+lon+"&travelmode=walking");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        focusMarker(mark);
        return true;
    }

    private void focusMarker(Marker m) {
        moveCamera(m.getPosition(),FOCUS_ZOOM);
        descriptionFragment.setText(m.getTitle(),m.getSnippet());
        if (fabDesc.getVisibility() == View.GONE) { // Daí precisamos mostrar o fragmento da descrição
            // Animate fragment
            AnimationSet sets = new AnimationSet(false);
            sets.addAnimation(AnimationUtils.loadAnimation(MapActivity.this,R.anim.anim_fadein));
            sets.addAnimation(AnimationUtils.loadAnimation(MapActivity.this,R.anim.anim_appearfrombottom));

            fabDesc.setVisibility(View.VISIBLE);
            fabGo.setVisibility(View.VISIBLE);
            fragmentTransaction.show(descriptionFragment);
            fragmentTransaction.commit();

            View v = findViewById(R.id.description_fragment_container);
            v.startAnimation(sets);
            fabDesc.startAnimation(sets);
            fabGo.startAnimation(sets);
        }
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        // Checa as permissões do aplicativo
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            }
        }
        else { // Caso não tenha, tenta pegar em tempo de execução ()
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false; // Assumir que não temos permissão ainda

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {// Se for maior que zero, significa que alguma coisa foi permitida
                    for (int i = 0;i < grantResults.length;i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                        mLocationPermissionGranted = true;
                        // inicializar o mapa
                        initMap();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fabDesc.setVisibility(View.GONE);
        fabGo.setVisibility(View.GONE);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(descriptionFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fabDesc = findViewById(R.id.fabDesc);
        fabGo = findViewById(R.id.fabGo);
        descriptionFragment = new DescriptionFragment();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.description_fragment_container,descriptionFragment);
        fragmentTransaction.hide(descriptionFragment);
        fragmentTransaction.commit();
    }
}

/**
 * TODO: Criar mapas customizados dos campi para transferir para o app
 * TODO: Criar fragmentos para mostrar detalhes dos markers do mapa do campus
 * TODO: Resolver dependência da configuração de campus (abrir mapas diferentes dependendo da escolha)
 */
