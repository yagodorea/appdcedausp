package com.dceusp.appdcedausp.ui;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dceusp.appdcedausp.utils.DescriptionFragment;
import com.dceusp.appdcedausp.R;
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

import java.util.HashMap;


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
    private static final LatLng saudeInicio = new LatLng(-23.552324,-46.6559853);
    private Boolean mLocationPermissionGranted = false;
    HashMap<String,Marker> markerHashMap;

    private boolean searching = false;

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
    FloatingActionButton fabDrawer;
    FloatingActionButton fabBack;
    EditText searchBox;
    ImageView searchButton;
    LinearLayout searchContainer;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;

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

        searchBox = findViewById(R.id.searchBox);
        searchContainer = findViewById(R.id.searchBoxContainer);
        searchBox.setVisibility(View.GONE);
        searchContainer.setVisibility(View.INVISIBLE);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateAnimation translate = new TranslateAnimation(1000, 0, 0, 0);
                translate.setDuration(500);
                TranslateAnimation back = new TranslateAnimation(0,1000,0,0);
                back.setDuration(500);
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if (searching) {
                    searchBox.startAnimation(back);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchBox.getWindowToken(),0);
                    }
                    searchBox.setVisibility(View.GONE);
                    searchContainer.setVisibility(View.INVISIBLE);
                    searching = false;
                    // Procurar
                    String text = searchBox.getText().toString();
                    if(markerHashMap.containsKey(text.toLowerCase())) {
                        Log.d(TAG, "Shazam! ->onClick: text:" + text);
                        onMarkerClick(markerHashMap.get(text.toLowerCase()));
                    }
                } else {
                    if (imm != null) {
                        imm.showSoftInputFromInputMethod(searchBox.getWindowToken(),0);
                    }
                    searchBox.setText("");
                    searchBox.setVisibility(View.VISIBLE);
                    searchContainer.setVisibility(View.VISIBLE);
                    searchBox.setAnimation(translate);
                    searching = true;
                }
            }
        });

        // MARKER DRAWER
        mDrawerLayout = findViewById(R.id.mapDrawer);
        mDrawerList = findViewById(R.id.left_drawer);
        fabDrawer = findViewById(R.id.fabDrawer);
        fabDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START,true);
            }
        });

        fabBack = findViewById(R.id.fabLeaveMap);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                            if (task.getResult() != null) {

                                currLocation = (Location) task.getResult();

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()), DEFAULT_ZOOM));
                                switch (campus) {
                                    case 0: {
                                        moveCamera(butantaInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 1: {
                                        moveCamera(sancaInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 2: {
                                        moveCamera(eachInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 3: {
                                        moveCamera(sanfranInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 4: {
                                        moveCamera(piracicInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 5: {
                                        moveCamera(ribeiraoInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 6: {
                                        moveCamera(pirassuInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 7: {
                                        moveCamera(bauruInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 8: {
                                        moveCamera(santosInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 9: {
                                        moveCamera(lorenaInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    case 10: {
                                        moveCamera(saudeInicio, DEFAULT_ZOOM);
                                        break;
                                    }
                                    default: {
                                        moveCamera(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()), DEFAULT_ZOOM);
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(MapActivity.this, "Ative sua localização!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(MapActivity.this,"Erro na localização!",Toast.LENGTH_LONG).show();
                            finish();
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
        Log.d(TAG, "Shazam! ->setMarkers: entrou, campus: " + campus);
        Resources res = getResources();
        TypedArray lats, lons, icons;
        String[] titles,desc;
        switch(campus) {
            case 0: {
                lats = res.obtainTypedArray(R.array.latButanta);
                lons = res.obtainTypedArray(R.array.lonButanta);
                titles = res.getStringArray(R.array.titlesButanta);
                desc = res.getStringArray(R.array.descButanta);
                icons = res.obtainTypedArray(R.array.iconsButanta);
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
                Toast.makeText(this, "Marcadores ainda não definidos, aguarde uma atualização", Toast.LENGTH_LONG).show();
                break;
            }case 3: {
                lats = res.obtainTypedArray(R.array.latSanfran);
                lons = res.obtainTypedArray(R.array.lonSanfran);
                titles = res.getStringArray(R.array.titlesSanfran);
                desc = res.getStringArray(R.array.descSanfran);
                icons = res.obtainTypedArray(R.array.iconsSanfran);
                break;
            }case 4: {
                lats = res.obtainTypedArray(R.array.latPiracicaba);
                lons = res.obtainTypedArray(R.array.lonPiracicaba);
                titles = res.getStringArray(R.array.titlesPiracicaba);
                desc = res.getStringArray(R.array.descPiracicaba);
                icons = res.obtainTypedArray(R.array.iconsPiracicaba);
                break;
            }case 5: {
                lats = res.obtainTypedArray(R.array.latRibeirao);
                lons = res.obtainTypedArray(R.array.lonRibeirao);
                titles = res.getStringArray(R.array.titlesRibeirao);
                desc = res.getStringArray(R.array.descRibeirao);
                icons = res.obtainTypedArray(R.array.iconsRibeirao);
                break;
            }case 6: {
                lats = res.obtainTypedArray(R.array.latPirassununga);
                lons = res.obtainTypedArray(R.array.lonPirassununga);
                titles = res.getStringArray(R.array.titlesPirassununga);
                desc = res.getStringArray(R.array.descPirassununga);
                icons = res.obtainTypedArray(R.array.iconsPirassununga);
                break;
            }case 7: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                Toast.makeText(this, "Marcadores ainda não definidos, aguarde uma atualização", Toast.LENGTH_LONG).show();
                break;
            }case 8: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                Toast.makeText(this, "Marcadores ainda não definidos, aguarde uma atualização", Toast.LENGTH_LONG).show();
                break;
            }case 9: {
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                Toast.makeText(this, "Marcadores ainda não definidos, aguarde uma atualização", Toast.LENGTH_LONG).show();
                break;
            }case 10: {
                lats = res.obtainTypedArray(R.array.latSaude);
                lons = res.obtainTypedArray(R.array.lonSaude);
                titles = res.getStringArray(R.array.titlesSaude);
                desc = res.getStringArray(R.array.descSaude);
                icons = res.obtainTypedArray(R.array.iconsSaude);
                break;
            }
            default:
                lats = res.obtainTypedArray(R.array.latSaoCarlos);
                lons = res.obtainTypedArray(R.array.lonSaoCarlos);
                titles = res.getStringArray(R.array.titlesSaoCarlos);
                desc = res.getStringArray(R.array.descSaoCarlos);
                icons = res.obtainTypedArray(R.array.iconsSanca);
                Toast.makeText(this, "Marcadores ainda não definidos, aguarde uma atualização", Toast.LENGTH_LONG).show();
                break;
        }

        markerHashMap = new HashMap<>();

        for(int i=0;i<res.getIntArray(R.array.markerNumber)[campus];i++)
        {
            Log.d(TAG, "Shazam! -> setMarkers: setando marcador " + titles[i]);
            //Bitmap small1 = Bitmap.createScaledBitmap(icon1.getBitmap(),100,100,false);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(lats.getFloat(i,0f),lons.getFloat(i,0f)))
                    .title(titles[i])
                    .snippet(desc[i])
                    .icon(BitmapDescriptorFactory.fromResource(icons.getResourceId(i,-1)));
            mark = mMap.addMarker(markerOptions);
            markerHashMap.put(titles[i].toLowerCase(),mark);
        }
        lats.recycle();
        lons.recycle();
        icons.recycle();

        // Set list adapter
        if (res.getIntArray(R.array.markerNumber)[campus] > 0) { // Se não pega a lista de sanca
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.map_list_item, titles));
            final String[] list = titles;
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mDrawerLayout.closeDrawer(Gravity.START, true);
                    onMarkerClick(markerHashMap.get(list[i].toLowerCase()));
                }
            });
        }
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
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
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

/*
 * TODO: Criar mapas customizados dos campi para transferir para o app
 */
