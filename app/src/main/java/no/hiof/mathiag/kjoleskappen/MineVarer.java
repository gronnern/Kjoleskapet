package no.hiof.mathiag.kjoleskappen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MineVarer extends AppCompatActivity {
    private static final String TAG = MineVarer.class.getSimpleName();
    private String BARCODE = "";
    private List<Vare> vareList;
    private Adapter adapter;
    private RecyclerView rv;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference firebase = databaseRef.child(mFirebaseUser.getUid()).push();
    private Calendar myCalendar = Calendar.getInstance();
    private DatabaseReference offlineRef = FirebaseDatabase.getInstance().getReference(mFirebaseUser.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_varer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MineVarer.this, SkannVare.class));
            }
        });

        offlineRef.keepSynced(true);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Call<VareResponse> call = apiService.getVare(BARCODE);
                call.enqueue(new Callback<VareResponse>() {
                    @Override
                    public void onResponse(Call<VareResponse> call, Response<VareResponse> response) {
                        vareList = response.body().getProducts();

                        //legger data til firebase
                        adapter = new Adapter(vareList,R.layout.vare_list,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        Vare product = vareList.get(vareList.size() - 1);
                        //lager et unikt id til hver og en vare.
                        String uuid = String.valueOf(UUID.randomUUID());
                        String myFormat = "dd.MM.yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        product.setUtlopsDato(sdf.format(myCalendar.getTime()));
                        Vare data = new Vare(uuid, product.getVareNavn(), product.getVareBilde(), product.getUtlopsDato());
                        firebase.setValue(data);

                    }

                    @Override
                    public void onFailure(Call<VareResponse> call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
            }

        };

        vareList = new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(vareList,R.layout.vare_list,getApplicationContext());
        rv.setAdapter(adapter);
        //henter data fra firebase
        database.getReference().child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vareList.removeAll(vareList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vare order = snapshot.getValue(Vare.class);
                    vareList.add(order);

                    //sorterer listen etter dato. Den som går ut først er først
                    Collections.sort(vareList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        BARCODE = bundle.getString("barcode");

        if (BARCODE.isEmpty()) {
            //ingen strekkode skannet
        }
        else {
            //Sjekker om strekkoden er ugyldig før jeg tar frem datepickeren
            Call<VareResponse> call = apiService.getVare(BARCODE);
            call.enqueue(new Callback<VareResponse>() {
                @Override
                public void onResponse(Call<VareResponse> call, Response<VareResponse> response) {
                    vareList = response.body().getProducts();


                    try {
                        //det er denne som blir feil hvis strekkoden er ugyldig.
                        Vare product = vareList.get(vareList.size() - 1);

                        //starter datepicker
                        new DatePickerDialog(MineVarer.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        Toast.makeText(getApplicationContext(),"Vennligst legg til en utløpsdato. Har ikke varen en utløpsdato kan du velge datoen du tror varen går ut.",Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),BARCODE + " er en ugyldig strekkode, eller ikke i systemet.",Toast.LENGTH_LONG).show();

                        //starter manuellvare ved ugyldig strekkode for å kunne legge til vare.
                        startManuellVare();
                    }
                }

                @Override
                public void onFailure(Call<VareResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Hovedmeny.class);
        startActivity(intent);
    }

    public void startManuellVare() {
        Intent intent = new Intent(this, ManuellVare.class);
        startActivity(intent);
    }
}
