package es.uma.lcc.peticionesvigilancialcc.repository;

import es.uma.lcc.peticionesvigilancialcc.model.Gestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestionRepository extends JpaRepository<Gestion, Boolean> {

}
