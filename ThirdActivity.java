import java.util.Objects; 
import android.Manifest; 
import android.content.Intent; 
import android.content.pm.PackageManager; 
import android.location.Location; 
import android.net.Uri; 
import android.os.Bundle; 
import android.util.Log; 
import android.widget.Button; 
import android.widget.Toast; 
import androidx.appcompat.widget.Toolbar; 
import androidx.annotation.NonNull; 
import androidx.appcompat.app.ActionBarDrawerToggle; 
import androidx.appcompat.app.AppCompatActivity; 
import androidx.core.app.ActivityCompat; 
import androidx.core.view.GravityCompat; 
import androidx.drawerlayout.widget.DrawerLayout; 
import com.google.android.gms.common.GoogleApiAvailability; 
import com.google.android.gms.location.FusedLocationProviderClient; 
import com.google.android.gms.location.LocationCallback; 
import com.google.android.gms.location.LocationRequest; 
import com.google.android.gms.location.LocationResult; 
import com.google.android.gms.location.LocationServices; 
import com.google.android.gms.maps.CameraUpdateFactory; 
import com.google.android.gms.maps.GoogleMap; 
import com.google.android.gms.maps.OnMapReadyCallback; 
import com.google.android.gms.maps.SupportMapFragment; 
import com.google.android.gms.maps.model.LatLng; 
import com.google.android.gms.maps.model.MarkerOptions; 
import com.google.android.material.navigation.NavigationView; 
import com.google.firebase.auth.FirebaseAuth; 
import com.google.firebase.database.DataSnapshot; 
import com.google.firebase.database.DatabaseError; 
import com.google.firebase.database.DatabaseReference; 
import com.google.firebase.database.FirebaseDatabase; 
import com.google.firebase.database.ValueEventListener; 
import com.locationtracker.R; 
public class ThirdActivity extends AppCompatActivity implements 
OnMapReadyCallback { 
private GoogleMap mMap; 
private FusedLocationProviderClient fusedLocationClient; 
DrawerLayout drawerLayout; 
NavigationView navigationView; 
Toolbar toolbar; 
private static final int LOCATION_PERMISSION_CODE = 1; 
private LatLng friendLocation = null; 
private LocationCallback locationCallback; 
private LocationRequest locationRequest; 
 
    private void startLocationSharing() { 
        locationRequest = LocationRequest.create(); 
        locationRequest.setInterval(5000); // 5 seconds 
        locationRequest.setFastestInterval(3000); // 3 seconds 
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 
 
        locationCallback = new LocationCallback() { 
            @Override 
            public void onLocationResult(LocationResult locationResult) { 
                if (locationResult == null) return; 
                Location location = locationResult.getLastLocation(); 
                if (location != null) { 
                    double lat = location.getLatitude(); 
                    double lng = location.getLongitude(); 
 
                    // Update Firebase realtime database 
                    DatabaseReference ref = FirebaseDatabase.getInstance() 
                            .getReference("shared_locations") 
                            
.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())); 
                    ref.setValue(new LatLng(lat, lng)); 
                } 
            } 
        }; 
 
        if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) != 
PackageManager.PERMISSION_GRANTED) { 
            ActivityCompat.requestPermissions(this, new 
String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
LOCATION_PERMISSION_CODE); 
            return; 
        } 
        fusedLocationClient.requestLocationUpdates(locationRequest, 
locationCallback, null); 
    } 
 
    private void stopLocationSharing() { 
        if (locationCallback != null) { 
            fusedLocationClient.removeLocationUpdates(locationCallback); 
        } 
        // Stop updates and remove location from Firebase 
        DatabaseReference ref = FirebaseDatabase.getInstance() 
                .getReference("shared_locations") 
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())); 
        ref.removeValue(); 
    } 
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_third); 
        // Google Play Services check 
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance(); 
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this); 
        if (resultCode != 
com.google.android.gms.common.ConnectionResult.SUCCESS) { 
            Objects.requireNonNull(apiAvailability.getErrorDialog(this, resultCode, 
1)).show(); 
            return; 
        } 
 
        drawerLayout = findViewById(R.id.drawer_layout); 
        navigationView = findViewById(R.id.navigation_view); 
        toolbar = findViewById(R.id.toolbar); 
        setSupportActionBar(toolbar); 
        listenToFriendLocation("friendUid_here"); 
 
        if (getSupportActionBar() != null) { 
            getSupportActionBar().setTitle("Home"); 
        } 
 
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); 
 
        SupportMapFragment mapFragment = (SupportMapFragment) 
                getSupportFragmentManager().findFragmentById(R.id.map); 
        if (mapFragment != null) { 
            mapFragment.getMapAsync(this); 
        } 
 
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( 
                this, drawerLayout, toolbar, R.string.open_drawer, 
R.string.close_drawer); 
        drawerLayout.addDrawerListener(toggle); 
        toggle.syncState(); 
 
        navigationView.setNavigationItemSelectedListener(item -> { 
            int id = item.getItemId(); 
            if (id == R.id.nav_logout) { 
                FirebaseAuth.getInstance().signOut(); 
                startActivity(new Intent(this, MainActivity.class)); 
                finish(); 
            } else if (id == R.id.nav_share) { 
                String shareText = "Track your friends and family in real-time with this 
awesome location tracker app!\n\n" + 
                        "Download 
now:https://yourwebsite.com/download/locationtracker.apk\n"; 
                Intent shareIntent = new Intent(Intent.ACTION_SEND); 
                shareIntent.setType("text/plain"); 
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText); 
                startActivity(Intent.createChooser(shareIntent, "Share via")); 
            }else if (id == R.id.nav_toggle_location) { 
                // Toggle logic 
                boolean isChecked = !item.isChecked(); 
                item.setChecked(isChecked); 
 
                if (isChecked) { 
                    startLocationSharing(); // Start sharing live location 
                    Toast.makeText(this, "Live location sharing ON", 
Toast.LENGTH_SHORT).show(); 
                } else { 
                    stopLocationSharing(); // Stop sharing live location 
                    Toast.makeText(this, "Live location sharing OFF", 
Toast.LENGTH_SHORT).show(); 
                } 
            } 
            drawerLayout.closeDrawer(GravityCompat.START); 
            return true; 
        }); 
        //    SHARE LOCATION BUTTON LOGIC 
        Button btnShareLocation = findViewById(R.id.btnShareLocation); 
 
        btnShareLocation.setOnClickListener(v -> { 
            if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) 
                    != PackageManager.PERMISSION_GRANTED) { 
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
LOCATION_PERMISSION_CODE); 
                return; 
            } 
 
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> { 
                if (location != null) { 
                    double lat = location.getLatitude(); 
                    double lng = location.getLongitude(); 
                    String link = "https://maps.google.com/?q=" + lat + "," + lng; 
 
                    Intent sendIntent = new Intent(); 
                    sendIntent.setAction(Intent.ACTION_SEND); 
                    sendIntent.putExtra(Intent.EXTRA_TEXT, link); 
                    sendIntent.setType("text/plain"); 
                    startActivity(Intent.createChooser(sendIntent, "Share location via")); 
                } else { 
                    Toast.makeText(this, "Unable to get current location", 
Toast.LENGTH_SHORT).show(); 
                } 
            }); 
        }); 
        //   Check for shared location in the intent 
        Intent intent = getIntent(); 
        Uri data = intent.getData(); 
        if (data != null && data.getQueryParameter("q") != null) { 
            String[] coords = 
Objects.requireNonNull(data.getQueryParameter("q")).split(","); 
            if (coords.length == 2) { 
                double friendLat = Double.parseDouble(coords[0]); 
                double friendLng = Double.parseDouble(coords[1]); 
                friendLocation = new LatLng(friendLat, friendLng); // You'll define this 
field 
            } 
        } 
    } 
 
 
    @Override 
    public void onMapReady(@NonNull GoogleMap googleMap) { 
        mMap = googleMap; 
        Log.d("ThirdActivity", "onMapReady: Map is ready"); 
 
        // Try showing a hardcoded location first to check if the map works at all 
        LatLng testLocation = new LatLng(33.6844, 73.0479); // Islamabad 
        mMap.addMarker(new MarkerOptions().position(testLocation).title("Test 
Location")); 
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 
15)); 
 
        // Then check permission for user location 
        if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) { 
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
LOCATION_PERMISSION_CODE); 
            return; 
        } 
 
        // If permission is already granted, show user location 
        showUserLocation(); 
    } 
 
 
    private void showUserLocation() { 
        if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) != 
PackageManager.PERMISSION_GRANTED && 
                ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_COARSE_LOCATION) != 
PackageManager.PERMISSION_GRANTED) { 
 
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                    LOCATION_PERMISSION_CODE); 
            return; 
        } 
        mMap.setMyLocationEnabled(true); 
        fusedLocationClient.getLastLocation() 
                .addOnSuccessListener(this, location -> { 
                    if (location != null && mMap != null) { 
                        LatLng myLatLng = new LatLng(location.getLatitude(), 
location.getLongitude()); 
                        mMap.addMarker(new 
MarkerOptions().position(myLatLng).title("You are here")); 
                        
mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15)); 
                        mMap.setMyLocationEnabled(true); 
                        // Show friend's location if available 
                        if (friendLocation != null) { 
                            mMap.addMarker(new 
MarkerOptions().position(friendLocation).title("Friend's Location")); 
 
                            // Calculate distance 
                            float[] result = new float[1]; 
                            Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, 
                                    friendLocation.latitude, friendLocation.longitude, result); 
 
                            Toast.makeText(this, "Distance to friend: " + result[0] + " meters", 
Toast.LENGTH_LONG).show(); 
                        } 
                    } else { 
                        Toast.makeText(this, "Location not found", 
Toast.LENGTH_SHORT).show(); 
                    } 
                }); 
    } 
 
 
    @Override 
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] 
permissions, 
                                           @NonNull int[] grantResults) { 
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); 
 
        if (requestCode == LOCATION_PERMISSION_CODE) { 
            if (grantResults.length > 0 && grantResults[0] == 
PackageManager.PERMISSION_GRANTED) { 
                if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) 
                        == PackageManager.PERMISSION_GRANTED && mMap != null) { 
                    mMap.setMyLocationEnabled(true); 
                    showUserLocation(); 
                } 
            } else { 
                Toast.makeText(this, "Location permission denied", 
Toast.LENGTH_SHORT).show(); 
            } 
        } 
    } 
    private void listenToFriendLocation(String friendUid) { 
        DatabaseReference ref = FirebaseDatabase.getInstance() 
                .getReference("shared_locations") 
                .child(friendUid); 
 
        ref.addValueEventListener(new ValueEventListener() { 
            @Override 
            public void onDataChange(@NonNull DataSnapshot snapshot) { 
                if (snapshot.exists()) { 
                    Double lat = snapshot.child("latitude").getValue(Double.class); 
                    Double lng = snapshot.child("longitude").getValue(Double.class); 
 
                    if (lat != null && lng != null) { 
                        friendLocation = new LatLng(lat, lng); 
 
                        // Update map with new friend location 
                        if (mMap != null) { 
                            mMap.addMarker(new 
MarkerOptions().position(friendLocation).title("Friend's Location")); 
                            
mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(friendLocation, 
15)); 
                        } 
                    } 
                } else { 
                    Toast.makeText(ThirdActivity.this, "Friend location not found", 
Toast.LENGTH_SHORT).show(); 
                } 
            } 
 
            @Override 
            public void onCancelled(@NonNull DatabaseError error) { 
                Toast.makeText(ThirdActivity.this, "Failed to get friend's location", 
Toast.LENGTH_SHORT).show(); 
            } 
        }); 
    } 
}