package com.example.appdcedausp;

import android.*;
import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

/**
 * Created by yago_ on 12/01/2018.
 */

public class MapActivity extends AppCompatActivity
    implements GoogleMap.OnMarkerClickListener
{

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1307;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float FOCUS_ZOOM = 17f;
    private Boolean mLocationPermissionGranted = false;

    // Marcadores do mapa
    private Marker mark1;
    private Marker mark2;
    private Marker mark3;
    private Marker mark4;

    // Coordenadas dos marcadores
    private float caaso1 = -22.006875f;
    private float caaso2 = -47.8968879f;
    private float arq1 = -22.0043909f;
    private float arq2 = -47.8975817f;
    private float obs1 = -22.0113144f;
    private float obs2 = -47.8953311f;
    private float fis1 = -22.0094621f;
    private float fis2 = -47.8963324f;

    // Fragmentos
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    EmptyFragment emptyFragment;
    DescriptionFragment descriptionFragment;

    // Elementos
    TextView descriptionTitle;
    TextView descriptionContent;
    FloatingActionButton fabDesc;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

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
                            Toast.makeText(MapActivity.this,"Localização encontrada!",Toast.LENGTH_LONG).show();

                            currLocation = (Location)task.getResult();

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),currLocation.getLongitude()),DEFAULT_ZOOM));
                        } else {
                            Toast.makeText(MapActivity.this,"Erro na localização!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

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
                Toast.makeText(MapActivity.this,"Mapa está pronto!",Toast.LENGTH_LONG).show();
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
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    descriptionFragment = new DescriptionFragment();
                    fragmentTransaction.add(R.id.description_fragment_container,descriptionFragment);
                    fragmentTransaction.hide(descriptionFragment);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    private void setMarkers() {
        BitmapDrawable icon1 = (BitmapDrawable) getResources().getDrawable(R.drawable.caaso);
        Bitmap small1 = Bitmap.createScaledBitmap(icon1.getBitmap(),100,100,false);

        mark1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(caaso1,caaso2))
                .title("CAASO")
                .snippet("Centro Acadêmico da USP São Carlos")
                .icon(BitmapDescriptorFactory.fromBitmap(small1)));

        mark2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(arq1,arq2))
                .title("Arquitetura"));

        mark3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(obs1,obs2))
                .title("Observatório"));

        mark4 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(fis1,fis2))
                .title("Física"));
    }

    // Inflar fragmentos e dar zoom quando clicarem nos markers
    @Override
    public boolean onMarkerClick(Marker marker) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        descriptionTitle = (TextView)findViewById(R.id.descTitle);
        descriptionContent = (TextView)findViewById(R.id.descContent);
        fabDesc = (FloatingActionButton)findViewById(R.id.fabDesc);

        fabDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabDesc.setVisibility(View.GONE);

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(descriptionFragment);
                fragmentTransaction.commit();
            }
        });

        if (marker.equals(mark1)) { // CAASO
            moveCamera(mark1.getPosition(),FOCUS_ZOOM);
            if (fabDesc.getVisibility() == View.GONE) { // Daí precisamos mostrar o fragmento da descrição
                fabDesc.setVisibility(View.VISIBLE);
                fragmentTransaction.show(descriptionFragment);
                fragmentTransaction.commit();
            }
            descriptionTitle.setText("CAASO");
            descriptionContent.setText("Centro Acadêmico Armando de Salles Oliveira.\nEsse é o CA mais TOP da USP!");
            return true;
        } else if (marker.equals(mark2)) {  // Arq
            moveCamera(mark2.getPosition(),FOCUS_ZOOM);
            if (fabDesc.getVisibility() == View.GONE) { // Daí precisamos mostrar o fragmento da descrição
                fabDesc.setVisibility(View.VISIBLE);
                fragmentTransaction.show(descriptionFragment);
                fragmentTransaction.commit();
            }
            descriptionTitle.setText("IAU");
            descriptionContent.setText("Instituto de Arquitetura e Urbanismo.\nOnde vc faz arquitetura, duh!");
            return true;
        } else if (marker.equals(mark3)) {  // Observatório
            moveCamera(mark3.getPosition(),FOCUS_ZOOM);
            if (fabDesc.getVisibility() == View.GONE) { // Daí precisamos mostrar o fragmento da descrição
                fabDesc.setVisibility(View.VISIBLE);
                fragmentTransaction.show(descriptionFragment);
                fragmentTransaction.commit();
            }
            descriptionTitle.setText("Observatório");
            descriptionContent.setText("Observatório na saída da produção.\nAqui vc pode ver as estrelas. :)");
            return true;
        } else if (marker.equals(mark4)) {  // Física
            moveCamera(mark4.getPosition(),FOCUS_ZOOM);
            if (fabDesc.getVisibility() == View.GONE) { // Daí precisamos mostrar o fragmento da descrição
                fabDesc.setVisibility(View.VISIBLE);
                fragmentTransaction.show(descriptionFragment);
                fragmentTransaction.commit();
            }
            descriptionTitle.setText("IFSC");
            descriptionContent.setText("Instituto de Física de São Carlos.\nAqui é tipo um buraco negro, tente evitar entrar nesse prédio.");
            return true;
        }
        return false;
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        // Checa as permissões do aplicativo
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                Toast.makeText(MapActivity.this,"Permissões OK",Toast.LENGTH_LONG).show();
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
}

/**
 * TODO: Criar mapas customizados dos campi para transferir para o app
 * TODO: Criar fragmentos para mostrar detalhes dos markers do mapa do campus
 * TODO: Resolver dependência da configuração de campus (abrir mapas diferentes dependendo da escolha)
 */
