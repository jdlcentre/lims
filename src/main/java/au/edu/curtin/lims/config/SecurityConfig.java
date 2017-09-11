/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebMvcSecurity
//See: Spring in Action, 4th Edition. P248 - 249:
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Inject
    private DataSource dataSource;

    // Override to configure how requests are secured by interceptors.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/Admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .permitAll();
    }

    // Override to configure Spring Security's filter chain.
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    // Override to configure user-details services.
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(""
                    + "SELECT username, password, enabled "
                    + "FROM   person "
                    + "WHERE   username = ?")
            .authoritiesByUsernameQuery(""
                    + "SELECT  person.username, "
                    + "authority.authority_name "
                    + "FROM    person "
                    + "JOIN    person_authority_mm ON person_authority_mm.person_id = person.person_id "
                    + "JOIN    authority ON authority.authority_id = person_authority_mm.authority_id "
                    + "WHERE   person.username = ?")
            .groupAuthoritiesByUsername(""
                    + "SELECT  faction.faction_id, "
                    + "         faction.faction_name, "
                    + "         authority.authority_name "
                    + "FROM    faction_person_mm "
                    + "JOIN    faction ON faction.faction_id = faction_person_mm.faction_id "
                    + "JOIN    faction_authority_mm ON faction_authority_mm.faction_id = faction.faction_id "
                    + "JOIN    authority ON authority.authority_id = faction_authority_mm.authority_id "
                    + "WHERE   faction_person_mm.person_id = (SELECT person_id FROM person WHERE username = ?)")
            .passwordEncoder(new BCryptPasswordEncoder());
    }
}
