package vn.edu.hcmute.uteshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@ActiveProfiles("test")
class WebFlowTest {
    @Autowired WebApplicationContext context;
    MockMvc mvc;
    @BeforeEach void setup() { mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build(); }
    MockHttpSession login(String account, String password) throws Exception {
        return (MockHttpSession) mvc.perform(post("/login").with(csrf()).param("login",account).param("password",password))
            .andExpect(authenticated()).andExpect(redirectedUrl("/dashboard"))
            .andReturn().getRequest().getSession(false);
    }
    @Test void publicTemplatesRender() throws Exception {
        for(String url:new String[]{"/", "/login"}) mvc.perform(get(url)).andExpect(status().isOk());
    }
    @Test void protectedDashboardRedirectsGuest() throws Exception {
        mvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    }
    @Test void invalidPasswordRejected() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login","admin@uteshop.vn").param("password","wrong"))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
    }
    @Test void missingCsrfRejected() throws Exception {
        mvc.perform(post("/login").param("login","admin@uteshop.vn").param("password","Admin@12345"))
            .andExpect(status().isForbidden()).andExpect(unauthenticated());
    }
    @Test void adminLoginHeaderAndLogout() throws Exception {
        var session=login("admin@uteshop.vn","Admin@12345");
        mvc.perform(get("/dashboard").session(session)).andExpect(status().isOk())
           .andExpect(content().string(org.hamcrest.Matchers.containsString("Quản trị viên")));
        mvc.perform(get("/admin").session(session)).andExpect(status().isOk());
        mvc.perform(post("/logout").session(session).with(csrf())).andExpect(redirectedUrl("/login?logout"));
        org.junit.jupiter.api.Assertions.assertTrue(session.isInvalid());
    }
    @Test void normalUserCannotAccessAdmin() throws Exception {
        var session=login("user@uteshop.vn","Demo@12345");
        mvc.perform(get("/admin").session(session)).andExpect(status().isForbidden());
    }

    @Test void usernameAlsoWorks() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login", "admin").param("password", "Admin@12345"))
            .andExpect(authenticated()).andExpect(redirectedUrl("/dashboard"));
    }

}
