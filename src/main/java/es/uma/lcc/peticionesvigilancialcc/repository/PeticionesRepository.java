package es.uma.lcc.peticionesvigilancialcc.repository;

import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.model.PeticionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeticionesRepository extends JpaRepository<Peticion, PeticionPK> {

}
