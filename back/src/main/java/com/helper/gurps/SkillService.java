package com.helper.gurps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository)
    {
        this.skillRepository = skillRepository;
    }

    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public Optional<Skill> getSkillBySkillId(Long skillId) {return skillRepository.findById(skillId);}

    public List<Skill> getAllSkills()
    {
        return skillRepository.findAll();
    }

    public Skill getCharacterSkill(Long characterId, Long skillId)
    {
        return skillRepository.findCharacterSkill(characterId, skillId);
    }
    public List<Skill> getAllCharacterSkills(Long characterId)
    {
        return skillRepository.findAllByCharacterId(characterId);
    }


    public Optional<Skill> updateSkill(Long skillId, Skill updatedSkill) throws Exception {
        Optional<Skill> existingSkill = skillRepository.findById(skillId);

        if(existingSkill.isPresent())
        {
            Skill skill = existingSkill.get();
            skill.setName(updatedSkill.getName());
            skill.setCharacter(updatedSkill.getCharacter());
            skill.setDifficulty(updatedSkill.getDifficulty());
            skill.setLevel(updatedSkill.getLevel());

            if(updatedSkill.getDescription() != null)
                skill.setDescription(updatedSkill.getDescription());
            return Optional.of(skillRepository.save(skill));
        }
        else
        {
            throw new Exception("Skill not found");
        }
    }

    public boolean deleteSkill(Long skillId) throws Exception {
        Optional<Skill> existingSkill = skillRepository.findById(skillId);
        if(existingSkill.isPresent())
        {
            Skill skill = existingSkill.get();

            skillRepository.delete(skill);
            return true;
        }
        else
        {
            return false;
        }

    }
}
