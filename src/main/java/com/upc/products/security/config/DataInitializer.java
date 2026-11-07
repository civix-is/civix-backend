package com.upc.products.security.config;

import com.upc.products.security.entities.Role;
import com.upc.products.security.entities.User;
import com.upc.products.security.repositories.RoleRepository;
import com.upc.products.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Creamos los roles limpios
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleAdmin = roleRepository.save(roleAdmin);

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleUser = roleRepository.save(roleUser);

        // 1. Usuario Administrador
        User admin = new User();
        admin.setUsername("admin@municipalidad.gob.pe");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(roleAdmin);
        userRepository.save(admin);

        // 2. Usuario Ciudadano (Común)
        User ciudadano = new User();
        ciudadano.setUsername("ciudadano@gmail.com");
        ciudadano.setPassword(passwordEncoder.encode("admin123"));
        ciudadano.setRole(roleUser);
        userRepository.save(ciudadano);

        System.out.println(">>> ¡USUARIOS ADMIN Y CIUDADANO CARGADOS EXITOSAMENTE! <<<");
    }
}
