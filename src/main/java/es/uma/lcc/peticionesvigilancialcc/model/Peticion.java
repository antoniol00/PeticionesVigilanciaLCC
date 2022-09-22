package es.uma.lcc.peticionesvigilancialcc.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

@Entity
public class Peticion implements Comparable<Peticion> {

    @EmbeddedId
    public PeticionPK peticionPK;

    @Column(nullable = false)
    public String timestamp;

    @Column(nullable = false)
    public int numProfAdicionales;

    public String profesoresAdicionales;
    public String comentarios;

    public String usuario;

    public boolean activo;

    public PeticionPK getPeticionPK() {
        return peticionPK;
    }

    public void setPeticionPK(PeticionPK peticionPK) {
        this.peticionPK = peticionPK;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getNumProfAdicionales() {
        return numProfAdicionales;
    }

    public void setNumProfAdicionales(int numProfAdicionales) {
        this.numProfAdicionales = numProfAdicionales;
    }

    public String getProfesoresAdicionales() {
        return profesoresAdicionales;
    }

    public void setProfesoresAdicionales(String profesoresAdicionales) {
        this.profesoresAdicionales = profesoresAdicionales;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Peticion{" +
                "peticionPK=" + peticionPK +
                ", timestamp='" + timestamp + '\'' +
                ", numProfAdicionales=" + numProfAdicionales +
                ", profesoresAdicionales='" + profesoresAdicionales + '\'' +
                ", comentarios='" + comentarios + '\'' +
                ", usuario='" + usuario + '\'' +
                ", activo=" + activo +
                '}';
    }

    @Override
    public int compareTo(Peticion o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = sdf.parse(this.getTimestamp());
            Date dateO = sdf.parse(o.getTimestamp());
            return dateO.compareTo(date);
        } catch (ParseException e) {
            return 0;
        }
    }


}
