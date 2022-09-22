package es.uma.lcc.peticionesvigilancialcc.controller;

import es.uma.lcc.peticionesvigilancialcc.model.Periodo;
import es.uma.lcc.peticionesvigilancialcc.model.Peticion;
import es.uma.lcc.peticionesvigilancialcc.repository.PeriodoRepository;
import es.uma.lcc.peticionesvigilancialcc.service.LoginService;
import es.uma.lcc.peticionesvigilancialcc.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping
public class TeacherController {
    private String username;

    @Autowired
    TeacherService teacherService;

    @Autowired
    LoginService loginService;

    @Autowired
    PeriodoRepository perrrepo;

    @GetMapping("/teacher")
    public String index(RedirectAttributes redirectAttributes,
                        Model model,
                        HttpServletRequest request,
                        @ModelAttribute("correcto") Object correcto,
                        @ModelAttribute("mensajeError") Object mensajeError,
                        @ModelAttribute("codigoAsignatura") Object codigoAsignatura,
                        @ModelAttribute("fechaHora") Object fechaHora,
                        @ModelAttribute("profesoresExamen") Object profesoresExamen,
                        @ModelAttribute("numVigilantes") Object numVigilantes,
                        @ModelAttribute("profSugeridos") Object profSugeridos,
                        @ModelAttribute("comentarios") Object comentarios,
                        @ModelAttribute("edicion") Object edicion,
                        @ModelAttribute("borrado") Object borrado,
                        @ModelAttribute("refresh") Object refresh,
                        @ModelAttribute("copiado") Object copiado) {

        try {
            if (username == null) {
                username = Arrays.stream(request.getCookies())
                        .filter(cookie -> "usernameTeacher".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findAny().get();
            }
        } catch (Exception e) {
            return "redirect:/";
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

        try {
            if ((boolean) borrado) {
                model.addAttribute("borrado", true);
            }
        } catch (Exception e) {
            model.addAttribute("borrado", false);
        }

        try {
            if ((boolean) refresh) {
                model.addAttribute("refresh", true);
            }
        } catch (Exception e) {
            model.addAttribute("refresh", false);
        }

        try {
            model.addAttribute("correcto", correcto);
            if (!(boolean) correcto) {
                model.addAttribute("mensajeError", mensajeError);
                model.addAttribute("codigoAsignatura", codigoAsignatura);
                model.addAttribute("fechaHora", fechaHora);
                model.addAttribute("profesoresExamen", profesoresExamen);
                model.addAttribute("numVigilantes", numVigilantes);
                model.addAttribute("profSugeridos", profSugeridos);
                model.addAttribute("comentarios", comentarios);
            }
        } catch (Exception e) {
            model.addAttribute("codigoAsignatura", "");
            model.addAttribute("fechaHora", "");
            model.addAttribute("profesoresExamen", "");
            model.addAttribute("numVigilantes", "");
            model.addAttribute("profSugeridos", "");
            model.addAttribute("comentarios", "");
        }

        try {
            model.addAttribute("copiado", true);
            if ((boolean) copiado) {
                model.addAttribute("codigoAsignatura", codigoAsignatura);
                model.addAttribute("fechaHora", fechaHora);
                model.addAttribute("profesoresExamen", profesoresExamen);
                model.addAttribute("numVigilantes", numVigilantes);
                model.addAttribute("profSugeridos", profSugeridos);
                model.addAttribute("comentarios", comentarios);
            }
        } catch (Exception e) {
            model.addAttribute("copiado", false);
        }

        model.addAttribute("username", username);
        ArrayList<Peticion> listaPeticionesActivas = teacherService.getListaPeticionesActivas(username);
        Collections.sort(listaPeticionesActivas);
        ArrayList<Peticion> listaPeticionesNoActivas = teacherService.getListaPeticionesNoActivas(username);
        Collections.sort(listaPeticionesNoActivas);
        model.addAttribute("listaPeticiones", listaPeticionesActivas);
        model.addAttribute("listaPeticionesN", listaPeticionesNoActivas);
        Map<String, List<String>> map = teacherService.getPeticionesCheck(listaPeticionesActivas);
        List<String> peticionesCheckS = map.get("bool");
        List<Boolean> peticionesCheck = new ArrayList<>();
        for (String s : peticionesCheckS) {
            peticionesCheck.add(s.equals("true"));
        }
        List<Boolean> peticionesRepetidas = teacherService.getPeticionesRepetidas(listaPeticionesActivas);
        List<String> background = teacherService.getBackground(peticionesCheck, peticionesRepetidas);
        model.addAttribute("peticionesCheck", peticionesCheck);
        model.addAttribute("background", background);
        model.addAttribute("repeticionEnPeticion", peticionesRepetidas.contains(true));
        model.addAttribute("errorEnPeticion", peticionesCheck.contains(false));
        String listaProfesores = teacherService.getListaProfesores();
        model.addAttribute("profesoresError", map.get("prof"));
        model.addAttribute("profDisponibles", listaProfesores);

        if(!perrrepo.findAll().isEmpty()){
            Periodo p = perrrepo.findAll().get(0);
            model.addAttribute("periodoCerrado",true);
            model.addAttribute("periodoSolicitudActual",
                    "Periodo de solicitud actual: " + p.getDateInicio() + " - " + p.getDateFin());
        }else{
            model.addAttribute("periodoSolicitudActual",
                    "No existe ningún período de solicitud abierto");
            model.addAttribute("periodoCerrado",false);
        }
        return "teacher";
    }

    @PostMapping("/teacher/add")
    public String crearPeticion(RedirectAttributes redirectAttributes,
                                @RequestParam String codigoAsignatura,
                                @RequestParam String fechaHora,
                                @RequestParam String profesoresExamen,
                                @RequestParam String numVigilantes,
                                @RequestParam String profSugeridos,
                                @RequestParam String comentarios) {
        if (username.isEmpty()) {
            return "redirect:/";
        }

        String check = teacherService.compruebaDatos(profesoresExamen, profSugeridos, fechaHora);

        if (check.isEmpty()) {
            teacherService.creaPeticion(codigoAsignatura, fechaHora, profesoresExamen,
                    numVigilantes, profSugeridos, comentarios, username);
            redirectAttributes.addFlashAttribute("correcto", true);
            redirectAttributes.addFlashAttribute("codigoAsignatura", "");
            redirectAttributes.addFlashAttribute("fechaHora", "");
            redirectAttributes.addFlashAttribute("profesoresExamen", "");
            redirectAttributes.addFlashAttribute("numVigilantes", "");
            redirectAttributes.addFlashAttribute("profSugeridos", "");
            redirectAttributes.addFlashAttribute("comentarios", "");
        } else {
            redirectAttributes.addFlashAttribute("correcto", false);
            redirectAttributes.addFlashAttribute("mensajeError", check);
            redirectAttributes.addFlashAttribute("codigoAsignatura", codigoAsignatura);
            redirectAttributes.addFlashAttribute("fechaHora", fechaHora);
            redirectAttributes.addFlashAttribute("profesoresExamen", profesoresExamen);
            redirectAttributes.addFlashAttribute("numVigilantes", numVigilantes);
            redirectAttributes.addFlashAttribute("profSugeridos", profSugeridos);
            redirectAttributes.addFlashAttribute("comentarios", comentarios);
        }

        return "redirect:/teacher";
    }

    @PostMapping("/teacher/edit")
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
            teacherService.editaPeticion(codigo, fecha, numVigilantes, profesoresExamen, profSugeridos, comentarios, codigoAntiguo, fechaAntigua, profesoresExamenAntiguos);
            redirectAttributes.addFlashAttribute("edicion", true);
        } else {
            redirectAttributes.addFlashAttribute("edicion", false);
            redirectAttributes.addFlashAttribute("mensajeError", check);
        }

        return "redirect:/teacher";
    }

    @PostMapping("/teacher/borra")
    public String borrarPeticion(RedirectAttributes redirectAttributes,
                                 @RequestParam String codigo,
                                 @RequestParam String fecha,
                                 @RequestParam String prof) {

        teacherService.borraPeticion(codigo, fecha, prof);
        redirectAttributes.addFlashAttribute("borrado", true);
        redirectAttributes.addFlashAttribute("msgBorrado", "Petición del examen de la asignatura " + codigo + " con fecha " + fecha + " borrada correctamente.");
        return "redirect:/teacher";
    }

    @PostMapping("/teacher/copia")
    public String copiarPeticion(RedirectAttributes redirectAttributes,
                                 @RequestParam String codigo,
                                 @RequestParam String fecha,
                                 @RequestParam String prof) {

        Peticion p = teacherService.getPeticion(codigo, fecha, prof);

        redirectAttributes.addFlashAttribute("copiado", true);
        redirectAttributes.addFlashAttribute("codigoAsignatura", p.getPeticionPK().getCodigo());
        redirectAttributes.addFlashAttribute("fechaHora", Calendar.getInstance().get(Calendar.YEAR) + "-" +
                p.getPeticionPK().getFecha().substring(3, 5) + "-" +
                p.getPeticionPK().getFecha().substring(0, 2) + "T" +
                p.getPeticionPK().getFecha().substring(6));
        redirectAttributes.addFlashAttribute("profesoresExamen", p.getPeticionPK().getProfesores());
        redirectAttributes.addFlashAttribute("numVigilantes", p.getNumProfAdicionales());
        redirectAttributes.addFlashAttribute("profSugeridos", p.getProfesoresAdicionales());
        redirectAttributes.addFlashAttribute("comentarios", p.getComentarios());

        return "redirect:/teacher";
    }

    @PostMapping("/teacher/refresh")
    public String refresh(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("refresh", true);
        return "redirect:/teacher";
    }
}
