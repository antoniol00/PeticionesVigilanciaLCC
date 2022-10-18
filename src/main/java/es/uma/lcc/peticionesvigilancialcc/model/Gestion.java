package es.uma.lcc.peticionesvigilancialcc.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Gestion {

    @Id
    public Boolean userOn;

    public Boolean isUserOn() {
        return userOn;
    }

    public void setUserOn(Boolean userOn) {
        this.userOn = userOn;
    }
}
