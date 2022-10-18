package es.uma.lcc.peticionesvigilancialcc.controller;

import es.uma.lcc.peticionesvigilancialcc.model.Gestion;
import es.uma.lcc.peticionesvigilancialcc.model.Periodo;
import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.model.Usuario;
import es.uma.lcc.peticionesvigilancialcc.repository.GestionRepository;
import es.uma.lcc.peticionesvigilancialcc.repository.PeriodoRepository;
import es.uma.lcc.peticionesvigilancialcc.service.AdminService;
import es.uma.lcc.peticionesvigilancialcc.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    TeacherService teacherService;

    @Autowired
    PeriodoRepository perrrepo;

    @Autowired
    GestionRepository grepo;

    String username;

    @GetMapping("/admin")
    public String index(Model model, HttpServletRequest request,
                        @ModelAttribute("msg") Object msg,
                        @ModelAttribute("msgActiva") Object msgActiva,
                        @ModelAttribute("modifica") Object modifica,
                        @ModelAttribute("upload") Object upload,
                        @ModelAttribute("edicion") Object edicion,
                        @ModelAttribute("mensajeError") Object mensajeError,
                        @ModelAttribute("borrado") Object borrado,
                        @ModelAttribute("refresh") Object refresh) {

        try {
            if (username == null) {
                username = Arrays.stream(request.getCookies())
                        .filter(cookie -> "usernameAdmin".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findAny().get();
            }
        } catch (Exception e) {
            return "redirect:/";
        }

        ArrayList<Peticion> listaPeticiones = teacherService.getListaPeticiones();
        Collections.sort(listaPeticiones);
        model.addAttribute("listaPeticiones", listaPeticiones);
        Map<String, List<String>> map = teacherService.getPeticionesCheck(listaPeticiones);
        List<String> peticionesCheckS = map.get("bool");
        List<Boolean> peticionesCheck = new ArrayList<>();
        for (String s : peticionesCheckS) {
            peticionesCheck.add(s.equals("true"));
        }
        model.addAttribute("peticionesCheck", peticionesCheck);
        model.addAttribute("errorEnPeticion", peticionesCheck.contains(false));
        String listaProfesores = teacherService.getListaProfesores();
        model.addAttribute("profesoresError", map.get("prof"));
        model.addAttribute("profDisponibles", listaProfesores);
        model.addAttribute("nombre_user", username);
        List<Usuario> listaUsuarios;
        listaUsuarios = adminService.getListaUsuarios();
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAttribute("emptylistaUsuarios", listaUsuarios.isEmpty());

        try {
            if ((boolean) msg) {
                model.addAttribute("msg", true);
            } else {
                model.addAttribute("msg", false);
            }
        } catch (Exception e) {
        }

        try {
            model.addAttribute("msgActiva", msgActiva);
        } catch (Exception e) {
        }

        try {
            if ((boolean) modifica) {
                model.addAttribute("modifica", true);
            }
        } catch (Exception e) {
            model.addAttribute("modifica", false);
        }

        try {
            if ((boolean) upload) {
                model.addAttribute("upload", true);
            }
        } catch (Exception e) {
            model.addAttribute("upload", false);
        }

        try {
            if ((boolean) refresh) {
                model.addAttribute("refresh", true);
            }
        } catch (Exception e) {
            model.addAttribute("refresh", false);
        }

        try {
            if ((boolean) borrado) {
                model.addAttribute("borrado", true);
            } else {
                model.addAttribute("borrado", false);
            }
        } catch (Exception e) {
        }

        try {
            model.addAttribute("contEdicion", edicion);
            model.addAttribute("edicion", true);
            if (!(boolean) edicion) {
                model.addAttribute("mensajeError", mensajeError);
            }
        } catch (Exception e) {
            model.addAttribute("edicion", false);
        }
        if (!perrrepo.findAll().isEmpty()) {
            Periodo p = perrrepo.findAll().get(0);
            model.addAttribute("periodoSolicitudActual",
                    "Periodo de solicitud actual: " + p.getDateInicio() + " - " + p.getDateFin());
        } else {
            model.addAttribute("periodoSolicitudActual",
                    "No existe ningún período de solicitud abierto");
        }

        List<Gestion> list = grepo.findAll();
        if (!list.isEmpty()) {
            model.addAttribute("gestionActiva", list.get(0).isUserOn() ? "Desactivar acceso docente" : "Activar acceso docente");
        }

        File file = new File("peticiones.txt");
        if (file.exists()) {
            file.delete();
        }
        return "admin";
    }

    @PostMapping("/admin/descarga")
    public ResponseEntity<InputStreamResource> download() throws FileNotFoundException {
        adminService.creaPeticiones();
        File file = new File("peticiones.txt");
        InputStreamResource resource = new InputStreamResource(new FileInputStream("peticiones.txt"));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peticiones.txt");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @PostMapping("/admin/borra")
    public String borraPeticiones(@RequestParam("fechaInicio") String fechaInicio,
                                  @RequestParam("fechaFin") String fechaFin,
                                  RedirectAttributes redirectAttributes) {
        if (username.isEmpty()) {
            return "redirect:/";
        }
        if (adminService.borraPeticiones(fechaInicio, fechaFin)) {
            redirectAttributes.addFlashAttribute("borrado", true);
        } else {
            redirectAttributes.addFlashAttribute("borrado", false);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            return "redirect:/admin";
        }

        if (username.isEmpty()) {
            return "redirect:/";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(Objects.requireNonNull(file.getOriginalFilename()));
            Files.write(path, bytes);
            adminService.addUsers(path);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("msg", false);
        }
        redirectAttributes.addFlashAttribute("upload", true);
        redirectAttributes.addFlashAttribute("msg", true);
        return "redirect:/admin";
    }

    @PostMapping("/admin/desactiva")
    public String desactivaUsuarios(RedirectAttributes redirectAttributes) {
        if (username.isEmpty()) {
            return "redirect:/";
        }
        boolean msgActiva = adminService.desactivaUsuarios();
        redirectAttributes.addFlashAttribute("msgActiva", msgActiva);
        return "redirect:/admin";
    }

    @PostMapping("/admin/editaEstado")
    public String editarEstadoUsuario(@RequestParam("username") String username,
                                      RedirectAttributes redirectAttributes) {
        if (this.username.isEmpty()) {
            return "redirect:/";
        }
        adminService.editaEstadoUsuario(username);
        redirectAttributes.addFlashAttribute("modifica", true);
        return "redirect:/admin";
    }

    @PostMapping("/admin/edit")
    public String editarPeticion(RedirectAttributes redirectAttributes,
                                 @RequestParam String codigo,
                                 @RequestParam String fecha,
                                 @RequestParam String numVigilantes,
                                 @RequestParam String profesoresExamen,
                                 @RequestParam String profSugeridos,
                                 @RequestParam String comentarios,
                                 @RequestParam String codigoAntiguo,
                                 @RequestParam String fechaAntigua,
                                 @RequestParam String profesoresExamenAntiguos) {
        if (username.isEmpty()) {
            return "redirect:/";
        }

        String check = teacherService.compruebaDatos(profesoresExamen, profSugeridos, fecha);

        if (check.isEmpty()) {
            String errorEdicion = teacherService.editaPeticion(codigo, fecha, numVigilantes, profesoresExamen, profSugeridos, comentarios, codigoAntiguo, fechaAntigua, profesoresExamenAntiguos);
            if (errorEdicion.isEmpty()) {
                redirectAttributes.addFlashAttribute("edicion", true);
            } else {
                redirectAttributes.addFlashAttribute("edicion", false);
                redirectAttributes.addFlashAttribute("mensajeError", errorEdicion);
            }
        } else {
            redirectAttributes.addFlashAttribute("edicion", false);
            redirectAttributes.addFlashAttribute("mensajeError", check);
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/refresh")
    public String refresh(RedirectAttributes redirectAttributes) {
        if (username.isEmpty()) {
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("refresh", true);
        return "redirect:/admin";
    }
}