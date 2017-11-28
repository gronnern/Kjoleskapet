package no.hiof.mathiag.kjoleskappen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Hovedmeny extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hovedmeny);

        //spør bruker om å tilltate kamera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        // Sjekker om bruker er logget inn, og gjennomfører innlogging dersom dette ikke er tilfellet.
        anonymousUserLogin();
    }

    public void startMineVarer(View view) {
        Intent intent = new Intent(this, MineVarer.class);
        intent.putExtra("barcode", "");
        startActivity(intent);
    }

    public void startSkannVare(View view) {
        Intent intent = new Intent(this, SkannVare.class);
        startActivity(intent);
    }

    public void startManuellVare(View view) {
        Intent intent = new Intent(this, ManuellVare.class);
        startActivity(intent);
    }

    public void anonymousUserLogin(){

        // Sjekker om brukeren allerede er logget inn anonymt.
        if(mFirebaseUser != null){
            updateUI(mFirebaseUser);
        }
        else{
            // Logger bruker inn anonymt.
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Hovedmeny.this, "User was signed in anonymously", Toast.LENGTH_SHORT).show();
                        updateUI(mAuth.getCurrentUser());
                    }
                    else{
                        Toast.makeText(Hovedmeny.this, "Anonymous sign-in failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void updateUI(FirebaseUser firebaseUser){
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
