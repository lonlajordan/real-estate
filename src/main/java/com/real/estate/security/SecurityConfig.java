package com.real.estate.security;

import com.real.estate.models.User;
import com.real.estate.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserRepository userRepository;


    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new CustomAuthenticationProvider(userRepository));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .and()
            .exceptionHandling().accessDeniedPage("/error")
            .and()
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/home", true)
            .failureHandler(new AuthenticationFailureHandler())
            .and()
            .logout().deleteCookies("JSESSIONID")
            .logoutUrl("/logout")
            .logoutSuccessHandler(new AuthenticationLogoutSuccessHandler())
            .logoutSuccessUrl("/")
            .invalidateHttpSession(false)
            .and()
            .authorizeRequests()
            .antMatchers("/", "/login", "/css/**", "/js/**", "/styles/**", "/scripts/**", "/images/**", "/img/**", "/fonts/**", "/font-awesome/**").permitAll()
            .antMatchers("/home").fullyAuthenticated()
            .anyRequest().permitAll();
    }

    @Component
    public static class CustomAuthenticationProvider implements AuthenticationProvider {
        private final UserRepository userRepository;

        public CustomAuthenticationProvider(UserRepository userRepository) {
            this.userRepository = userRepository;
        }


        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            String username = authentication.getName();
            String credentials = (String) authentication.getCredentials();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            User user = userRepository.findByUsername(username);
            if (user == null) throw new UsernameNotFoundException("incorrect.username");
            if(!new BCryptPasswordEncoder().matches(credentials, user.getPassword())){
                throw new BadCredentialsException("incorrect.password");
            }
            if(user.getEnabled() != 1) throw new BadCredentialsException("account.disabled");
            HttpSession session = attributes.getRequest().getSession(true);
            session.setAttribute("user", user);
            Collection<SimpleGrantedAuthority> authorities = user.getRoleList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(username, credentials, authorities);
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return authentication.equals(UsernamePasswordAuthenticationToken.class);
        }

    }

    private static class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            String url = "/login";
            if(exception != null) {
                String message = exception.getMessage();
                if("incorrect.username".equals(message)){
                    url = "/login?error=1";
                }else if("incorrect.password".equals(message)){
                    url = "/login?error=2";
                }else if("account.disabled".equals(message)){
                    url = "/login?error=3";
                }
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);
        }
    }

    private static class AuthenticationLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
