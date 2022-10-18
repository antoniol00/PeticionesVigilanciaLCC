package es.uma.lcc.peticionesvigilancialcc.service;

import es.uma.lcc.peticionesvigilancialcc.model.Periodo;
import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.model.PeticionPK;
import es.uma.lcc.peticionesvigilancialcc.model.Usuario;
import es.uma.lcc.peticionesvigilancialcc.repository.PeriodoRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.PeticionesRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TeacherService {

    @Autowired
    PeticionesRepository repo;

    @Autowired
    UsuariosRepository urepo;

    @Autowired
    PeriodoRepository perrepo;

    public void creaPeticion(String codigoAsignatura, String fechaHora, String profesoresExamen,
                             String numVigilantes, String profSugeridos, String comentarios, String username) {
        Peticion p = new Peticion();
        PeticionPK ppk = new PeticionPK();
        ppk.setCodigo(Integer.parseInt(codigoAsignatura));
        ppk.setFecha(fechaHora.substring(8, 10) + "/" + fechaHora.substring(5, 7) + " " + fechaHora.substring(11));
        ppk.setProfesores(profesoresExamen);

        p.setPeticionPK(ppk);
        p.setActivo(true);
        p.setComentarios(comentarios);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String format = date.format(new Date());
        p.setTimestamp(format);
        p.setProfesoresAdicionales(profSugeridos);
        p.setNumProfAdicionales(Integer.parseInt(numVigilantes));
        p.setUsuario(username);

        repo.save(p);
    }

    public String compruebaDatos(String profesoresExamen, String profSugeridos, String fechaHora) {
        if (profSugeridos.isEmpty()) {
            if (profesoresExamen.matches("(\\w+)(\\s*\\w+)*")) {
                List<Usuario> users = new ArrayList<>();
                for (Usuario u : urepo.findAll()) {
                    if (u.isActivo())
                        users.add(u);
                }
                for (String prof : profesoresExamen.split("\\s+")) {
                    Usuario u = new Usuario();
                    u.setUsername(prof);
                    if (!users.contains(u)) {
                        return "El profesor del examen \"" + prof + "\" no está dado de alta actualmente.";
                    }
                }
            } else {
                return "El formato de introducción de los profesores no es correcto, debe introducir los profesores separados por espacios";
            }
        } else {
            if (profesoresExamen.matches("(\\w+)(\\s*\\w+)*") && profSugeridos.matches("(\\w+)(\\s*\\w+)*")) {
                List<Usuario> users = new ArrayList<>();
                for (Usuario u : urepo.findAll()) {
                    if (u.isActivo())
                        users.add(u);
                }
                for (String prof : profesoresExamen.split("\\s+")) {
                    Usuario u = new Usuario();
                    u.setUsername(prof);
                    if (!users.contains(u)) {
                        return "El profesor del examen \"" + prof + "\"  no está dado de alta actualmente";
                    }
                }
                for (String prof : profSugeridos.split("\\s+")) {
                    Usuario u = new Usuario();
                    u.setUsername(prof);
                    if (!users.contains(u)) {
                        return "El profesor sugerido \"" + prof + "\" no está dado de alta actualmente";
                    }
                }
            } else {
                return "El formato de introducción de los profesores no es correcto, debe introducir los profesores separados por espacios";
            }
        }
        try {
            String fecha = fechaHora.substring(8, 10) + "/" + fechaHora.substring(5, 7) + "/" + Calendar.getInstance().get(Calendar.YEAR) + " " + fechaHora.substring(11);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateExamen = sdf.parse(fecha);
            Date dateActual = new Date();
            if (dateExamen.compareTo(dateActual) < 0) {
                return "La fecha del examen es anterior a la fecha actual. Debe indicar una fecha y hora posterior.";
            }

            if(!perrepo.findAll().isEmpty()){
                Periodo periodo = perrepo.findAll().get(0);
                String fechaInicioPeriodo = periodo.getDateInicio();
                String fechaFinPeriodo = periodo.getDateFin();

                Date dateInicioPeriodo = sdf.parse(fechaInicioPeriodo);
                Date dateFinPeriodo = sdf.parse(fechaFinPeriodo);
                if (dateExamen.compareTo(dateInicioPeriodo) < 0) {
                    return "La fecha del examen es anterior a la fecha de inicio actual de solicitudes. Debe indicar una fecha y hora posterior a " + fechaInicioPeriodo;
                }
                if (dateExamen.compareTo(dateFinPeriodo) > 0) {
                    return "La fecha del examen es posterior a la fecha de fin actual de solicitudes. Debe indicar una fecha y hora anterior a " + fechaFinPeriodo;
                }
            }else{
                return "No hay abierto ningún periodo de solicitudes";
            }

        } catch (Exception e) {
            return "Error en la creación de la petición.";
        }
        return "";
    }

    public ArrayList<Peticion> getListaPeticionesActivas(String username) {
        ArrayList<Peticion> res = new ArrayList<>();
        for (Peticion p : repo.findAll()) {
            if (p.getUsuario().equals(username) && p.isActivo()) {
                res.add(p);
            }
        }
        return res;
    }

    public ArrayList<Peticion> getListaPeticionesNoActivas(String username) {
        ArrayList<Peticion> res = new ArrayList<>();
        for (Peticion p : repo.findAll()) {
            if (p.getUsuario().equals(username) && !p.isActivo()) {
                res.add(p);
            }
        }
        return res;
    }

    public ArrayList<Peticion> getListaPeticiones() {
        ArrayList<Peticion> res = new ArrayList<>();
        for (Peticion p : repo.findAll()) {
            if (p.isActivo()) {
                res.add(p);
            }
        }
        return res;
    }

    public String getListaProfesores() {
        StringJoiner sj = new StringJoiner("\n");
        for (Usuario u : urepo.findAll()) {
            if (u.isActivo())
                sj.add("- " + u.getUsername() + " ( " + u.getNombre() + ")");
        }
        return sj.toString();
    }

    public Map<String, List<String>> getPeticionesCheck(ArrayList<Peticion> peticions) {
        List<Boolean> res = new ArrayList<>();
        List<Usuario> usuarioList = new ArrayList<>();
        List<String> usuariosError = new ArrayList<>();
        for (Usuario u : urepo.findAll()) {
            if (u.isActivo())
                usuarioList.add(u);
        }
        int x = 0;
        for (Peticion p : peticions) {
            for (String prof : p.getPeticionPK().getProfesores().split("\\s+")) {
                addUsuariosError(res, usuarioList, usuariosError, x, prof);
            }
            if (!p.getProfesoresAdicionales().isEmpty()) {
                for (String prof : p.getProfesoresAdicionales().split("\\s+")) {
                    addUsuariosError(res, usuarioList, usuariosError, x, prof);
                }
            }
            x++;
        }
        List<String> resS = new ArrayList<>();
        for (Boolean b : res) {
            resS.add(b ? "true" : "false");
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("bool", resS);
        map.put("prof", usuariosError);
        return map;
    }

    private void addUsuariosError(List<Boolean> res, List<Usuario> usuarioList, List<String> usuariosError, int x, String prof) {
        Usuario u = new Usuario();
        u.setUsername(prof);
        if (res.size() > x) {
            if (!usuarioList.contains(u)) {
                if (usuariosError.size() <= x) {
                    usuariosError.add(x, prof);
                } else {
                    usuariosError.add(x, usuariosError.get(x) + " " + prof);
                    usuariosError.remove(x + 1);
                }
            }
            res.add(x, res.get(x) && usuarioList.contains(u));
            res.remove(x + 1);
        } else {
            res.add(x, usuarioList.contains(u));
            if (!usuarioList.contains(u)) {
                if (usuariosError.size() <= x) {
                    usuariosError.add(x,prof);
                } else {
                    usuariosError.add(x,usuariosError.get(x) + " " + prof);
                    usuariosError.remove(x + 1);
                }
            }else{
                usuariosError.add(x,"");
            }
        }
    }


    public String editaPeticion(String codigo, String fecha, String numVigilantes,
                              String profesoresExamen, String profSugeridos,
                              String comentarios, String codigoAntiguo, String fechaAntigua,
                              String profesoresExamenAntiguos) {

        PeticionPK ppk = new PeticionPK();
        ppk.setCodigo(Integer.parseInt(codigoAntiguo));
        ppk.setFecha(fechaAntigua);
        ppk.setProfesores(profesoresExamenAntiguos);


        try{
            Peticion r = repo.findById(ppk).get();
            repo.delete(r);

            r.peticionPK.setCodigo(Integer.parseInt(codigo));
            r.peticionPK.setFecha(fecha.substring(8, 10) + "/" + fecha.substring(5, 7) + " " + fecha.substring(11));
            r.peticionPK.setProfesores(profesoresExamen);
            r.setComentarios(comentarios);
            r.setProfesoresAdicionales(profSugeridos);
            r.setNumProfAdicionales(Integer.parseInt(numVigilantes));

            repo.save(r);
        }catch(NoSuchElementException e){
            return "Error al editar la petición. Inténtelo de nuevo.";
        }
        return "";
    }

    public void borraPeticion(String codigo, String fecha, String prof) {
        PeticionPK ppk = new PeticionPK();
        ppk.setCodigo(Integer.parseInt(codigo));
        ppk.setFecha(fecha);
        ppk.setProfesores(prof);

        Peticion p = repo.findById(ppk).get();
        repo.delete(p);
    }

    public Peticion getPeticion(String codigo, String fecha, String prof) {
        PeticionPK ppk = new PeticionPK();
        ppk.setCodigo(Integer.parseInt(codigo));
        ppk.setFecha(fecha);
        ppk.setProfesores(prof);
        return repo.findById(ppk).get();
    }

    public List<Boolean> getPeticionesRepetidas(ArrayList<Peticion> listaPeticionesActivas) {
        Boolean[] res = new Boolean[listaPeticionesActivas.size()];
        for (int x = 0; x < listaPeticionesActivas.size(); x++) {
            Peticion p = listaPeticionesActivas.get(x);
            int codigo = p.getPeticionPK().getCodigo();
            String fecha = p.getPeticionPK().getFecha();
            for (int y = x + 1; y < listaPeticionesActivas.size(); y++) {
                Peticion p2 = listaPeticionesActivas.get(y);
                int codigo2 = p2.getPeticionPK().getCodigo();
                String fecha2 = p2.getPeticionPK().getFecha();
                if (codigo == codigo2 && fecha.equals(fecha2)) {
                    res[x] = res[y] = true;
                } else {
                    res[y] = false;
                }
            }
        }
        if (!listaPeticionesActivas.isEmpty() && res[0] == null) {
            res[0] = false;
        }
        return Arrays.asList(res);
    }

    public List<String> getBackground(List<Boolean> peticionesCheck, List<Boolean> peticionesRepetidas) {
        String[] res = new String[peticionesCheck.size()];
        for (int x = 0; x < peticionesCheck.size(); x++) {
            if (!peticionesCheck.get(x)) {
                res[x] = "background-color:#E57777";
            } else if (peticionesRepetidas.get(x)) {
                res[x] = "background-color:#F3F56D";
            } else {
                res[x] = "";
            }
        }
        return Arrays.asList(res);
    }
}
