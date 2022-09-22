package es.uma.lcc.peticionesvigilancialcc.service;

import es.uma.lcc.peticionesvigilancialcc.model.Usuario;
import es.uma.lcc.peticionesvigilancialcc.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LoginService {

    //TODO
    private static final String ADMINUSER = "admin";
    private static final String ADMINPASSWORD = "1234";

    @Autowired
    UsuariosRepository urepo;

    public boolean checkAdminLogin(String user, String pass) {
        return user.equals(ADMINUSER) && pass.equals(ADMINPASSWORD);
    }

    public String checkTeacherLogin(String user, String pass) {
        try {
            Usuario u;
            String usuario;
            if(user.indexOf("@")!=-1){
                u = urepo.findById(user.substring(0,user.indexOf("@"))).get();
                usuario = user;
            }else{
                u = urepo.findById(user).get();
                usuario = user + "@lcc.uma.es";
            }
            if (!u.isActivo()) {
                return "El usuario no se encuentra activo. Póngase en contacto con el administrador del sistema si piensa que ha sido un error.";
            } else {
                if (!compruebaLdap(usuario, pass)) {
                    return "Usuario o contraseña incorrectos.";
                } else {
                    return "";
                }
            }
        } catch (NoSuchElementException e) {
            return "El usuario no existe / no está dado de alta en el sistema. Póngase en contacto con el administrador del sistema si piensa que ha sido un error.";
        }
    }
    @Value("${LDAPkey}")
    String LDAP_PASSWORD;
    public boolean compruebaLdap(String user, String pass) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://ldap.uma.es:389");
            String base = "ou=lcc.search,idnc=sist,dc=uma,dc=es";
            env.put(Context.SECURITY_PRINCIPAL, base);
            env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);

            DirContext ctx = new InitialDirContext(env);

            if (ctx != null){
                String filter = "(irisMailAlternateAddress=" + user + ")";
                SearchControls ctrl = new SearchControls();
                ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
                NamingEnumeration<SearchResult> answer = ctx.search("idnc=usr,dc=uma,dc=es", filter, ctrl);

                if (answer.hasMore()) {
                    SearchResult result = answer.next();
                    return testBind(result.getNameInNamespace(),pass);
                }
                answer.close();
                ctx.close();
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testBind (String dn, String password) throws Exception {
        Hashtable<String,String> env = new Hashtable <String,String>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://ldap.uma.es:389");
            DirContext ctx = new InitialDirContext(env);
        }
        catch (javax.naming.AuthenticationException e) {
            return false;
        }
        return true;
    }

    /*public boolean checkEstadoUsuario() {
        List<Gestion> list = grepo.findAll();
        return list.get(0).isUserOn();
    }*/
}

