package com.helper.gurps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TraitRepository extends JpaRepository<Trait, Long> {

    @Query("SELECT s FROM Trait s WHERE s.character.id = :characterId")
    List<Trait> findAllByCharacterId(@Param("characterId") Long characterId);

    @Query("SELECT s FROM Trait s WHERE s.character.id = :characterId AND s.id = :traitId")
    Trait findCharacterTrait(@Param("characterId") Long characterId, @Param("traitId") Long traitId);
}
