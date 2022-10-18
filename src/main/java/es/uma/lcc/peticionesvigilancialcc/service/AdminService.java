package es.uma.lcc.peticionesvigilancialcc.service;

import es.uma.lcc.peticionesvigilancialcc.model.Gestion;
import es.uma.lcc.peticionesvigilancialcc.model.Periodo;
import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.model.Usuario;
import es.uma.lcc.peticionesvigilancialcc.repository.GestionRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.PeriodoRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.PeticionesRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
public class AdminService {

    @Autowired
    UsuariosRepository repo;

    @Autowired
    PeticionesRepository prepo;

    @Autowired
    PeriodoRepository perrepo;

    @Autowired
    GestionRepository grepo;

    public List<Usuario> getListaUsuarios() {
        return repo.findAll();
    }

    public void creaPeticiones() {
        String ruta = "peticiones.txt";
        String contenido = creaContenido();
        try {
            File file = new File(ruta);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String creaContenido() {
        StringBuilder sb = new StringBuilder();
        for (Peticion p : prepo.findAll()) {
            if (p.isActivo()) {
                sb.append("#-------------").append(p.getUsuario()).append("---").append(p.getTimestamp()).append("------------\n");
                sb.append("I\n");
                sb.append(p.getPeticionPK().getCodigo()).append("\n");
                String fechaExamen = p.getPeticionPK().getFecha();
                sb.append(fechaExamen, 0, 5).append("\n");
                sb.append(fechaExamen.substring(6)).append("\n");
                sb.append(p.getPeticionPK().getProfesores().replace(" ", ", ")).append("\n");
                sb.append(p.getNumProfAdicionales()).append("\n");
                sb.append(p.getProfesoresAdicionales().isEmpty() ? "" : p.getProfesoresAdicionales().replace(" ", ", ") + "\n");
                if (!p.getComentarios().isEmpty()) {
                    try (Scanner sc = new Scanner(p.getComentarios())) {
                        while (sc.hasNextLine()) {
                            sb.append("* ").append(sc.nextLine()).append("\n");
                        }
                    }
                }
                sb.append("\n\n");
            }
        }
        String rawString = sb.toString();
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(rawString);
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    public void addUsers(Path path) throws IOException {
        try {
            //ponemos primero todos como inactivos
            for (Usuario u : repo.findAll()) {
                u.setActivo(false);
                repo.save(u);
            }
            //leemos fichero y colocamos como activos aquellos que aparezcan, y añadimos los que no se encuentren
            File file = path.toFile();
            Scanner sc = new Scanner(file);
            List<Usuario> usuarioList = repo.findAll();
            while (sc.hasNextLine()) {
                String data = sc.nextLine().trim();
                if (!data.startsWith("#") && !data.isEmpty() && Character.isLowerCase(data.charAt(0))) {
                    String[] line = data.split("\\s+");
                    Usuario user = new Usuario();
                    user.setUsername(line[0]);
                    if (usuarioList.contains(user)) {
                        // actualizamos valor
                        Usuario u = repo.findById(line[0]).get();
                        u.setActivo(true);
                        repo.save(u);
                    } else {
                        // lo añadimos
                        StringBuilder nombre = new StringBuilder();
                        for (int x = 5; x < line.length; x++) {
                            nombre.append(line[x]).append(" ");
                        }
                        user.setNombre(nombre.toString());
                        user.setActivo(true);
                        repo.save(user);
                    }
                }
            }
            sc.close();
            file.delete();
        } catch (FileNotFoundException e) {
            throw new IOException();
        }
    }

    public boolean borraPeticiones(String fechaInicio, String fechaFin) {
        //establecemos fechas
        Periodo p = new Periodo();
        String dateInicio = fechaInicio.substring(8,10)+"/"+fechaInicio.substring(5,7)+"/"+
                fechaInicio.substring(0,4) + " " + fechaInicio.substring(11);
        String dateFin = fechaFin.substring(8,10)+"/"+fechaFin.substring(5,7)+"/"+
                fechaFin.substring(0,4) + " " + fechaFin.substring(11);
        //comparamos fechas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try{
            Date parseDateInicio = sdf.parse(dateInicio);
            Date parseDateFin = sdf.parse(dateFin);
            if(parseDateFin.compareTo(parseDateInicio) <= 0){
                return false;
            }
        }catch(ParseException e){

        }
        p.setDateInicio(dateInicio);
        p.setDateFin(dateFin);
        perrepo.deleteAll();
        perrepo.save(p);
        //desactivamos peticiones
        for (Peticion pet : prepo.findAll()) {
            pet.setActivo(false);
            prepo.save(pet);
        }
        //borramos usuarios
        repo.deleteAll();
        return true;
    }

    public boolean desactivaUsuarios() {
        List<Gestion> list = grepo.findAll();
        if(list.isEmpty()){
            Gestion g = new Gestion();
            Boolean b = Boolean.FALSE;
            g.setUserOn(b);
            grepo.save(g);
            return false;
        }else{
            boolean newValue = !list.get(0).isUserOn();
            grepo.deleteAll();
            Gestion g = new Gestion();
            g.setUserOn(newValue);
            grepo.save(g);
            return newValue;
        }
    }

    public void editaEstadoUsuario(String username) {
        Usuario u = repo.findById(username).get();
        u.setActivo(!u.isActivo());
        repo.save(u);
    }
}