package org.utn.tpfinalprogramacion3.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;


import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<CredencialEntity, Long> {
    Optional<UserDetails> findByEmail(String email);
}
