package no.hiof.mathiag.kjoleskappen;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Mathi on 14.10.2017.
 */

public class Vare implements Comparable<Vare> {
    private String id;
    @SerializedName("name")
    private String vareNavn;
    @SerializedName("image_url")
    private String vareBilde;
    private String utlopsDato;

    public Vare(String id, String vareNavn, String vareBilde, String utlopsDato) {
        this.id = id;
        this.vareNavn = vareNavn;
        this.vareBilde = vareBilde;
        this.utlopsDato = utlopsDato;
    }

    public String getVareNavn() {
        return vareNavn;
    }

    public void setVareNavn(String vareNavn) {
        this.vareNavn = vareNavn;
    }

    public String getVareBilde() {
        return vareBilde;
    }

    public void setVareBilde(String vareBilde) {
        this.vareBilde = vareBilde;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUtlopsDato() {
        return utlopsDato;
    }

    public void setUtlopsDato(String utlopsDato) {
        this.utlopsDato = utlopsDato;
    }

    //sorterer etter dato
    @Override
    public int compareTo(Vare o) {
        int result = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        try {
            result = dateFormat.parse(getUtlopsDato()).compareTo(dateFormat.parse(o.getUtlopsDato()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
