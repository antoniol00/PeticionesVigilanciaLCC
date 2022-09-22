package es.uma.lcc.peticionesvigilancialcc.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PeticionPK implements Serializable {
    public int codigo;
    public String fecha;
    public String profesores;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProfesores() {
        return profesores;
    }

    public void setProfesores(String profesores) {
        this.profesores = profesores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeticionPK that = (PeticionPK) o;
        return codigo == that.codigo && Objects.equals(fecha, that.fecha) && Objects.equals(profesores, that.profesores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, fecha, profesores);
    }
}
