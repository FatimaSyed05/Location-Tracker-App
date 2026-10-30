import android.content.Intent; 
 
 
import android.os.Bundle; 
import android.text.InputType; 
import android.view.View; 
import android.widget.Button; 
import android.widget.EditText; 
import android.widget.ImageView; 
import android.widget.Toast; 
 
import androidx.annotation.Nullable; 
import androidx.appcompat.app.AppCompatActivity; 
 
import com.google.firebase.auth.FirebaseAuth; 
import com.google.firebase.auth.FirebaseAuthUserCollisionException; 
import com.google.firebase.auth.FirebaseUser; 
import com.locationtracker.R; 
 
import java.util.Objects; 
 
public class MainActivity extends AppCompatActivity { 
 
    EditText etEmail, etPassword; 
    Button btnLogin, signupButton; 
    ImageView ivTogglePassword; 
    boolean isPasswordVisible = false; 
 
    FirebaseAuth mAuth; 
 
    @Override 
    protected void onCreate(@Nullable Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main); 
 
        // Firebase Auth instance 
        mAuth = FirebaseAuth.getInstance(); 
 
        // Initialize views 
        etEmail = findViewById(R.id.etEmail); 
        etPassword = findViewById(R.id.etPassword); 
        btnLogin = findViewById(R.id.btnLogin); 
        signupButton = findViewById(R.id.signupButton); 
        ivTogglePassword = findViewById(R.id.ivTogglePassword); 
 
        // Check if user already logged in 
        FirebaseUser currentUser = mAuth.getCurrentUser(); 
        if (currentUser != null) { 
            startActivity(new Intent(MainActivity.this, ThirdActivity.class)); 
            finish(); 
        } 
 
        // Toggle password visibility 
        ivTogglePassword.setOnClickListener(v -> { 
            if (isPasswordVisible) { 
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | 
InputType.TYPE_TEXT_VARIATION_PASSWORD); 
                ivTogglePassword.setImageResource(R.drawable.eye_hidden); 
            } else { 
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | 
InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); 
                ivTogglePassword.setImageResource(R.drawable.eye_open); 
            } 
            etPassword.setSelection(etPassword.getText().length()); 
            isPasswordVisible = !isPasswordVisible; 
        }); 
 
        // Signup logic 
        signupButton.setOnClickListener(v -> { 
            String email = etEmail.getText().toString().trim(); 
            String password = etPassword.getText().toString().trim(); 
 
            if (email.isEmpty() || password.isEmpty()) { 
                Toast.makeText(this, "Enter email and password", 
Toast.LENGTH_SHORT).show(); 
                return; 
            } 
 
            mAuth.createUserWithEmailAndPassword(email, password) 
                    .addOnCompleteListener(task -> { 
                        if (task.isSuccessful()) { 
                            Toast.makeText(this, "Signup successful", 
Toast.LENGTH_SHORT).show(); 
                            startActivity(new Intent(MainActivity.this, ThirdActivity.class)); 
                            finish(); 
                        } else { 
                            if (task.getException() instanceof 
FirebaseAuthUserCollisionException) { 
                                Toast.makeText(this, "Already registered. Please login.", 
Toast.LENGTH_SHORT).show(); 
                                btnLogin.setVisibility(View.VISIBLE); 
                                signupButton.setVisibility(View.GONE); 
                            } else { 
                                Toast.makeText(this, "Signup failed: " + 
Objects.requireNonNull(task.getException()).getMessage(), 
Toast.LENGTH_SHORT).show(); 
                            } 
                        } 
                    }); 
        }); 
 
        // Login logic 
        btnLogin.setOnClickListener(v -> { 
            String email = etEmail.getText().toString().trim(); 
            String password = etPassword.getText().toString().trim(); 
 
            if (email.isEmpty() || password.isEmpty()) { 
                Toast.makeText(this, "Enter email and password", 
Toast.LENGTH_SHORT).show(); 
                return; 
            } 
 
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) 
                    .addOnCompleteListener(task -> { 
                        if (task.isSuccessful()) { 
                            Toast.makeText(this, "Login successful", 
Toast.LENGTH_SHORT).show(); 
                            startActivity(new Intent(MainActivity.this, SecondActivity.class)); 
                            finish(); 
                        } else { 
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show(); 
                        } 
                    }); 
        }); 
    } 
}