package es.uma.lcc.peticionesvigilancialcc.controller;

import es.uma.lcc.peticionesvigilancialcc.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login/admin")
    public String adminLogin(Model model, @ModelAttribute("msg") Object msg) {
        try {
            if ((boolean) msg) {
                model.addAttribute("msg", msg);
            }
        } catch (Exception e) {
            model.addAttribute("msg", false);
        }
        model.addAttribute("rol", "admin");
        model.addAttribute("teacher", false);
        return "login";
    }

    @PostMapping("/login/admin")
    public String adminLogin(HttpServletResponse response,
                             RedirectAttributes redirectAttributes, @RequestParam String password,
                             @RequestParam String username) {
        if (loginService.checkAdminLogin(username, password)) {
            Cookie cookie = new Cookie("usernameAdmin", username);
            cookie.setPath("/admin");
            response.addCookie(cookie);
            return "redirect:/admin";
        }
        redirectAttributes.addFlashAttribute("msg", true);
        return "redirect:/login/admin";
    }

    @GetMapping("/login/teacher")
    public String teacherLogin(Model model, @ModelAttribute("msg") Object msg,
                               @ModelAttribute("msgCheck") Object msgCheck) {
        try {
            if ((boolean) msg) {
                model.addAttribute("msg", msg);
            }
        } catch (Exception e) {
            model.addAttribute("msg", false);
        }

        try {
            if ((boolean) msgCheck) {
                model.addAttribute("msgCheck", msgCheck);
            }
        } catch (Exception e) {
            model.addAttribute("msgCheck", false);
        }

        model.addAttribute("rol", "teacher");
        model.addAttribute("teacher", true);
        return "login";
    }

    @PostMapping("/login/teacher")
    public String teacherLogin(HttpServletResponse response, RedirectAttributes redirectAttributes, @RequestParam String password,
                               @RequestParam String username) {


        String msg = loginService.checkTeacherLogin(username, password);
        if (msg.isEmpty()) {
            Cookie cookie;
            if (username.indexOf("@") != -1) {
                cookie = new Cookie("usernameTeacher", username.substring(0, username.indexOf("@")));
            } else {
                cookie = new Cookie("usernameTeacher", username);
            }
            cookie.setPath("/teacher");
            response.addCookie(cookie);
            return "redirect:/teacher";
        }
        redirectAttributes.addFlashAttribute("msg", true);
        redirectAttributes.addFlashAttribute("msgMostrar", msg);
        return "redirect:/login/teacher";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("usernameTeacher", "");
        cookie.setPath("/teacher");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        Cookie cookie2 = new Cookie("usernameAdmin", "");
        cookie2.setPath("/admin");
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);
        return "redirect:/";
    }
}
