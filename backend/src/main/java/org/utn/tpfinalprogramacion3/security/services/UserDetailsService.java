package org.utn.tpfinalprogramacion3.security.services;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.security.repositories.CredentialsRepository;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final CredentialsRepository credentialsRepository;

    public UserDetailsService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialsRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el email: " + username));
    }

}