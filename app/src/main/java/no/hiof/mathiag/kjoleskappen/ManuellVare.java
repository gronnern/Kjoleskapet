package no.hiof.mathiag.kjoleskappen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class ManuellVare extends AppCompatActivity {
    private Calendar myCalendar = Calendar.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
    private DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference firebase = database2.child(mFirebaseUser.getUid()).push();
    private String utlopsDato;
    private String vareNavn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuell_vare);

        final EditText editText_Utlops = (EditText) findViewById(R.id.editText_utlops);
        editText_Utlops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd.MM.yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        utlopsDato = sdf.format(myCalendar.getTime());
                        editText_Utlops.setText(utlopsDato);
                    }
                };
                new DatePickerDialog(ManuellVare.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    //sendknappen
    public void sendButt(View view) {
        final EditText editText_name = (EditText) findViewById(R.id.editText_name);
        final EditText editText_Utlops = (EditText) findViewById(R.id.editText_utlops);

        //lager et unikt id til hver og en vare.
        String id = String.valueOf(UUID.randomUUID());
        vareNavn = String.valueOf(editText_name.getText());
        utlopsDato = String.valueOf(editText_Utlops.getText());

        if(vareNavn.isEmpty()){
            Toast.makeText(getApplicationContext(),"Vennligst gi varen et navn.",Toast.LENGTH_LONG).show();
        }
        else if(utlopsDato.isEmpty()){
            Toast.makeText(getApplicationContext(),"Vennligst gi varen en utløpsdato. Har ikke varen en utløpsdato kan du velge datoen du tror varen går ut.",Toast.LENGTH_LONG).show();
        }
        else {
            final Vare vare = new Vare(id,vareNavn,"https://www.shareicon.net/data/128x128/2016/08/19/817434_food_512x512.png",utlopsDato);

            firebase.setValue(vare);

            editText_name.setText("");
            editText_Utlops.setText("");

            Intent intent = new Intent(this, MineVarer.class);
            intent.putExtra("barcode", "");
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Hovedmeny.class);
        startActivity(intent);
    }
}
