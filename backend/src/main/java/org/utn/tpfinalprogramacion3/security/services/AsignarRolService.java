package org.utn.tpfinalprogramacion3.security.services;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.security.repositories.CredentialsRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.security.entities.RoleEntity;
import org.utn.tpfinalprogramacion3.security.repositories.RoleRepository;



@Service
@RequiredArgsConstructor
public class AsignarRolService {

    private final CredentialsRepository credencialRepository;
    private final RoleRepository roleRepository;

    public String asignarRol(String email, Rol rol) {
        CredencialEntity credencial = (CredencialEntity) credencialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credencial no encontrada"));

        RoleEntity roleEntity = roleRepository.findByRole(rol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        credencial.getRoles().add(roleEntity);
        credencialRepository.save(credencial);

        return "Rol asignado correctamente";
    }
}
