package es.uma.lcc.peticionesvigilancialcc.repository;

import es.uma.lcc.peticionesvigilancialcc.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, String> {

}
