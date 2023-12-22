package com.helper.gurps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("SELECT s FROM Skill s WHERE s.character.id = :characterId")
    List<Skill> findAllByCharacterId(@Param("characterId") Long characterId);

    @Query("SELECT s FROM Skill s WHERE s.character.id = :characterId AND s.id = :skillId")
    Skill findCharacterSkill(@Param("characterId") Long characterId, @Param("skillId") Long skillId);
}
