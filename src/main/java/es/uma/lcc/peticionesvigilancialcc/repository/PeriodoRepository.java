package es.uma.lcc.peticionesvigilancialcc.repository;

import es.uma.lcc.peticionesvigilancialcc.model.Periodo;
import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.model.PeticionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

}
