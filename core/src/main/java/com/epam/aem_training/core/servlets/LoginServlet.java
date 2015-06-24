package com.epam.aem_training.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.Principal;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.principal.JackrabbitPrincipal;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.util.AccessControlUtil;


@SlingServlet(
        label = "Google Login Servlet",
        paths = {"/services/login"}
)
public class LoginServlet extends SlingSafeMethodsServlet {
	
	@Reference
    private SlingRepository repository;
	
	private String login = "test";
	private String pwd = "test";
	
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    
    	PrintWriter out = resp.getWriter();
    	
    	login = req.getParameter("login");
    	pwd = req.getParameter("pwd");
    	
    	Credentials adminCredentials = new SimpleCredentials("admin", "admin".toCharArray());
    	
    	try {
			Session session = repository.login(adminCredentials);
			UserManager userManager = AccessControlUtil.getUserManager(session);
			out.println(userManager);
			User user = userManager.createUser(login, pwd);
			out.println(user.getPath());
			Group group = (Group) (userManager.getAuthorizable("administrators"));
			out.println(group.getPath());
            group.addMember(user);
            session.save();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			out.println(e.getMessage());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			out.println(e.getMessage());
		}
    	
    }
    
    private class PrincipalImpl implements JackrabbitPrincipal, Serializable {
        private final String name;
        public PrincipalImpl(String name) {
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("Principal name can neither be null nor empty String.");
            }

            this.name = name;
        }
        
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof JackrabbitPrincipal) {
                return name.equals(((Principal) obj).getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return getClass().getName() + ":" + name;
        }
    }
}
