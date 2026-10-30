import android.Manifest; 
import android.content.Context; 
import android.content.Intent; 
import android.content.pm.PackageManager; 
import android.location.Location; 
import android.location.LocationListener; 
import android.location.LocationManager; 
import android.os.Bundle; 
import android.os.Handler; 
import android.widget.Button; 
import android.widget.TextView; 
import android.widget.Toast; 
import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity; 
import androidx.core.app.ActivityCompat; 
import com.google.android.gms.location.FusedLocationProviderClient; 
import com.google.android.gms.location.LocationServices; 
import com.google.firebase.auth.FirebaseAuth; 
import com.google.firebase.database.DatabaseReference; 
import com.google.firebase.database.FirebaseDatabase; 
import com.google.firebase.database.Transaction; 
import com.locationtracker.R; 
import java.util.Objects; 
public class SecondActivity extends AppCompatActivity { 
TextView tvTitle, tvLatitude, tvLongitude, tvStatus; 
Button btnRefresh; 
FusedLocationProviderClient fusedLocationClient; 
FirebaseAuth mAuth; 
DatabaseReference dbRef; 
    LocationManager locationManager; 
 
    private final int LOCATION_PERMISSION_CODE = 100; 
 
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_second); 
 
        // Initialize views 
        tvTitle = findViewById(R.id.tvTitle); 
        tvLatitude = findViewById(R.id.tvLatitude); 
        tvLongitude = findViewById(R.id.tvLongitude); 
        tvStatus = findViewById(R.id.tvStatus); 
        btnRefresh = findViewById(R.id.btnRefresh); 
 
        // Initialize Firebase and location services 
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); 
        mAuth = FirebaseAuth.getInstance(); 
        dbRef = FirebaseDatabase.getInstance().getReference("Locations"); 
        locationManager = (LocationManager) 
getSystemService(Context.LOCATION_SERVICE); 
 
        // Button click to refresh location 
        btnRefresh.setOnClickListener(v -> getLocationAndUpload()); 
        getLocationAndUpload(); 
        new Handler().postDelayed(() -> { 
            Intent intent = new Intent(SecondActivity.this, ThirdActivity.class); 
            startActivity(intent); 
            finish(); 
        }, 5000);  // 3000ms = 3 seconds delay 
    } 
 
    private void getLocationAndUpload() { 
        if (ActivityCompat.checkSelfPermission(this, 
Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) { 
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
LOCATION_PERMISSION_CODE); 
            return; 
        } 
 
        tvStatus.setText("Fetching location..."); 
 
        // Realtime updates using LocationManager 
        locationManager.requestLocationUpdates( 
                LocationManager.GPS_PROVIDER, 
                5000, 
                10, 
                new LocationListener() { 
                    @Override 
                    public void onLocationChanged(@NonNull Location location) { 
                        updateLocation(location); 
                        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class); 
                        startActivity(intent); 
                        finish(); 
                    } 
                } 
        ); 
 
        // Instant location using FusedLocationProviderClient 
        fusedLocationClient.getLastLocation() 
                .addOnSuccessListener(location -> { 
                    if (location != null) { 
                        updateLocation(location); 
                        Toast.makeText(this, "Location Uploaded", 
Toast.LENGTH_SHORT).show(); 
                    } 
                }); 
    } 
 
    private void updateLocation(Location location) { 
        double lat = location.getLatitude(); 
        double lon = location.getLongitude(); 
 
        tvLatitude.setText("Latitude: " + lat); 
        tvLongitude.setText("Longitude: " + lon); 
        tvTitle.setText("Lat: " + lat + "\nLon: " + lon); 
        tvStatus.setText("Location Uploaded   "); 
 
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid(); 
        dbRef.child(userId).setValue(new LocationData(lat, lon)); 
    } 
 
    // Handle permission result 
    @Override 
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] 
permissions, 
                                           @NonNull int[] grantResults) { 
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); 
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) { 
            getLocationAndUpload(); 
        } else { 
            Toast.makeText(this, "Location permission denied", 
Toast.LENGTH_SHORT).show(); 
        } 
    } 
 
    // Firebase requires this model class 
    public static class LocationData { 
        public double latitude, longitude; 
 
        public LocationData() {}  // Needed by Firebase 
 
        public LocationData(double latitude, double longitude) { 
            this.latitude = latitude; 
            this.longitude = longitude; 
        } 
    } 
}