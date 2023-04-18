package com.team.puddy.domain.pet.repository;

import com.team.puddy.domain.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
