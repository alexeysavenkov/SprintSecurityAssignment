package com.naukma;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Controller

public class SecuredWebServer implements WebMvcConfigurer {

	@GetMapping("/")
	public String home(Map<String, Object> model) {
		model.put("message", "Hello World");
		model.put("title", "Hello Home");
		model.put("date", new Date());
		return "home";
	}

	@GetMapping("/admin/only")
    public String test() {
	    return "adminOnly";
    }

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(SecuredWebServer.class).run(args);
	}

	@Configuration
    @PropertySource(value={"classpath:database.properties"})
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Value("${spring.queries.users-query}")
        private String usersQuery;

        @Value("${spring.queries.roles-query}")
        private String rolesQuery;

        @Qualifier("dataSource")
        @Autowired
        private DataSource dataSource;

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.
                    jdbcAuthentication()
                    .usersByUsernameQuery(usersQuery)
                    .authoritiesByUsernameQuery(rolesQuery)
                    .dataSource(dataSource)
                    .passwordEncoder(new PasswordEncoder() {
                        @Override
                        public String encode(CharSequence charSequence) {
                            return charSequence.toString();
                        }

                        @Override
                        public boolean matches(CharSequence charSequence, String s) {
                            return  encode(charSequence).equals(s);
                        }
                    });
        }

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .anyRequest() .fullyAuthenticated()
                    .and()
                    .formLogin().loginPage("/login")
					.failureUrl("/login?error").permitAll().and().logout().permitAll();
		}


	}

}
