package com.epam.aem_training.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.aem_training.core.GoogleAuthHelperService;


@SlingServlet(
        label = "Google Login Servlet",
        paths = {"/services/googlelogin"}
)
public class GoogleLoginServlet extends SlingSafeMethodsServlet {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	private GoogleAuthHelperService helper;
	
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    
    	resp.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
    	HttpSession session = req.getSession();
    	PrintWriter out = resp.getWriter();

    	logger.debug("Test logger start");
    	
    	Cookie cookie = req.getCookie("userId");
    	
    	if (req.getParameter("code") != null) {
    		String userId = helper.exchangeCodeForCredential(req.getParameter("code"));
    		out.println(userId);
    		Cookie tmp = new Cookie("userId", userId);
    		tmp.setMaxAge(30);
    		tmp.setPath("/");
    		resp.addCookie(tmp);
    		String referer = (req.getHeader("referer") != null) ? req.getHeader("referer") : "/content/aemtraining/en/news.html";
    		resp.sendRedirect(referer);
    	}
    	
    	if (cookie == null) {
    		out.println("<a href='" + helper.buildLoginUrl() + "'>log in with google</a>");
    	} else {
    		out.println("<pre>");
			out.println(helper.getUserInfo(cookie.getValue()));
			out.println("</pre>");
    	}
    	
    }  
}
