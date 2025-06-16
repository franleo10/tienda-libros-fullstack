package org.utn.tpfinalprogramacion3.security.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.security.entities.RoleEntity;
import org.utn.tpfinalprogramacion3.security.enums.Permits;
import org.utn.tpfinalprogramacion3.security.filter.JwtAuthenticationFilter;
import org.utn.tpfinalprogramacion3.security.filter.RestAuthenticationEntryPoint;
import org.utn.tpfinalprogramacion3.security.repositories.RoleRepository;

import java.util.Set;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/webhook").permitAll()

                        // Otras rutas seguras
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }
    @Bean
    public  AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage(null); // No redireccionar
        return handler;
    }

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findAll().isEmpty()) {

                // Rol USUARIO
                Set<Permits> permisosUsuario = Set.of(
                        Permits.VER_LIBROS,
                        Permits.VER_AUTORES,
                        Permits.VER_RESENIAS,
                        Permits.HACER_RESENIAS,
                        Permits.ELIMINAR_RESENIA_PROPIA,
                        Permits.VER_CARRITO,
                        Permits.AGREGAR_LIBRO_AL_CARRITO,
                        Permits.VER_SU_FACTURA,
                        Permits.VER_LINK_MERCADOPAGO,
                        Permits.VER_BIBLIOTECA,
                        Permits.AGREGAR_LIBRO_FAVORITOS,
                        Permits.VER_LIBRO_FAVORITO,
                        Permits.ELIMINAR_LIBRO_FAVORITO,
                        Permits.VER_GENEROS
                );

                RoleEntity usuario = RoleEntity.builder()
                        .role(Rol.USUARIO)
                        .permits(permisosUsuario)
                        .build();

                // Rol ADMINISTRADOR
                RoleEntity admin = RoleEntity.builder()
                        .role(Rol.ADMINISTRADOR)
                        .permits(Set.of(Permits.values())) // todos los permisos
                        .build();

                roleRepository.save(admin);
                roleRepository.save(usuario);
            }
        };
    }

}

