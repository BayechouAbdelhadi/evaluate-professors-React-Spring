package io.evaluation.evaluateProfessors.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.evaluation.evaluateProfessors.domain.ERole;
import io.evaluation.evaluateProfessors.domain.Role;



@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}