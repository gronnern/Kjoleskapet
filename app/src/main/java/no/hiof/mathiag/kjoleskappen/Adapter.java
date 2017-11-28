package no.hiof.mathiag.kjoleskappen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Mathi on 14.10.2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private List<Vare> varer;
    private int rowLayout;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference firebaseRef = database.child(mFirebaseUser.getUid());


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vareNavn;
        public ImageView vareBilde;
        public TextView utlopsDato;
        public ImageButton slettKnapp;

        public MyViewHolder(View view) {
            super(view);
            vareNavn = (TextView) view.findViewById(R.id.vareNavn);
            vareBilde = (ImageView) view.findViewById(R.id.Bilde);
            utlopsDato = (TextView) view.findViewById(R.id.utlopsDato);
            slettKnapp = (ImageButton) view.findViewById(R.id.slettKnapp);
        }
    }


    public Adapter(List<Vare> varer, int rowLayout, Context context) {
        this.varer = varer;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MyViewHolder(view);
    }

    //Legger dataen inn i recyclerviewet.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Vare vare = varer.get(position);
        holder.vareNavn.setText(vare.getVareNavn());
        holder.utlopsDato.setText(vare.getUtlopsDato());
        Picasso.with(context).load(vare.getVareBilde()).resize(150,250).centerCrop().into(holder.vareBilde);

        //Sletter data fra firebase
        final String vareId = vare.getId();
        holder.slettKnapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseRef.orderByChild("id").equalTo(vareId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return varer.size();
    }
}

