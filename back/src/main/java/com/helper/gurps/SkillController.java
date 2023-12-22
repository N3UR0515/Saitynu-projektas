package com.helper.gurps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;
    private final CharactersService charactersService;

    @Autowired
    public SkillController(SkillService skillService, CharactersService charactersService)
    {
        this.skillService = skillService;
        this.charactersService = charactersService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }


    @GetMapping("/{skillId}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long skillId) {
        Optional<Skill> skill = skillService.getSkillBySkillId(skillId);
        return skill.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    public ResponseEntity<?> processBody(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.get("name") == null)
                return new ResponseEntity<>("Skill must contain name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("name").isNull())
                return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String name = jsonNode.get("name").asText();

            if (Objects.equals(name, "null") || Objects.equals(name, null) || name.isEmpty())
                return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("character") == null)
                return new ResponseEntity<>("Skill must contain character", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("character").isNull())
                return new ResponseEntity<>("Skill must contain character", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("character").get("id") == null)
                return new ResponseEntity<>("Character must contain Id", HttpStatus.UNPROCESSABLE_ENTITY);

            String characterId = jsonNode.get("character").get("id").asText();

            try {
                Long idCampaign = Long.parseLong(characterId);
                Optional<Character> character = charactersService.getCharacterByCharacterId(idCampaign);

                if (character.isEmpty()) {
                    return new ResponseEntity<>("Character with ID " + characterId + " does not exist", HttpStatus.NOT_FOUND);
                }

                if (jsonNode.get("difficulty") == null)
                    return new ResponseEntity<>("Skill must contain difficulty", HttpStatus.UNPROCESSABLE_ENTITY);

                if (jsonNode.get("difficulty").isNull())
                    return new ResponseEntity<>("Skill difficulty cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

                String difficultyStr = jsonNode.get("difficulty").asText();
                Difficulty difficulty = Difficulty.valueOf(difficultyStr);

                if (jsonNode.get("level") == null)
                    return new ResponseEntity<>("Skill must contain level", HttpStatus.UNPROCESSABLE_ENTITY);

                if (jsonNode.get("level").isNull())
                    return new ResponseEntity<>("Skill level cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

                int level = jsonNode.get("level").asInt();

                if (level < 1) {
                    return new ResponseEntity<>("Skill level must be higher than 1", HttpStatus.UNPROCESSABLE_ENTITY);
                }

                String description = jsonNode.has("description") ? jsonNode.get("description").asText() : null;

                Skill skill = new Skill(name, character.get());
                skill.setDifficulty(difficulty);
                skill.setLevel(level);
                skill.setDescription(description);

                return new ResponseEntity<Skill>(skill, HttpStatus.OK);

            } catch (NumberFormatException e) {
                return new ResponseEntity<>("Character Id must be a number", HttpStatus.UNPROCESSABLE_ENTITY);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid difficulty value", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/")
    public ResponseEntity<?> createSkill(@RequestBody String string) {
        try
        {
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Skill)
            {
                Skill createdSkill = skillService.saveSkill((Skill)test.getBody());
                return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
            }
            return test;
        }catch (NullPointerException e)
        {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }

        /*if(skill.getName() == null)
        {
            return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long characterId = skill.getCharacter().getId();
        Optional<Character> character = charactersService.getCharacterByCharacterId(characterId);

        if (character.isEmpty()) {
            return new ResponseEntity<>("Character with ID " + character + " does not exist", HttpStatus.NOT_FOUND);
        }
        Skill createdSkill = skillService.saveSkill(skill);
        return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);*/
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Long skillId, @RequestBody String string) throws Exception {
        try
        {
            Optional<Skill> toUpdate = skillService.getSkillBySkillId(skillId);
            if(toUpdate.isEmpty())
                return new ResponseEntity<>("Skill with ID " + skillId + " does not exist", HttpStatus.NOT_FOUND);
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Skill)
            {
                Optional<Skill> updatedSkill = skillService.updateSkill(skillId, (Skill)test.getBody());
                return updatedSkill.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return test;
        }catch (NullPointerException e)
        {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }

        /*if(skill.getName() == null)
        {
            return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long characterId = skill.getCharacter().getId();
        Optional<Character> character = charactersService.getCharacterByCharacterId(characterId);

        if (character.isEmpty()) {
            return new ResponseEntity<>("Character with ID " + character + " does not exist", HttpStatus.NOT_FOUND);
        }
        Optional<Skill> updatedSkill = skillService.updateSkill(skillId, skill);
        return updatedSkill.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));*/
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillId) throws Exception {
        boolean deleted = skillService.deleteSkill(skillId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

@RestController
@RequestMapping("/api/campaign")
class SkillController1 {
    private final SkillService skillService;
    private final CharactersService charactersService;
    private final CampaignService campaignService;

    @Autowired
    public SkillController1(SkillService skillService, CharactersService charactersService, CampaignService campaignService) {
        this.skillService = skillService;
        this.charactersService = charactersService;
        this.campaignService = campaignService;
    }

    @GetMapping("/{campaignId}/characters/{characterId}/skills")
    public ResponseEntity<?> getAllSkills(@PathVariable Long campaignId, @PathVariable Long characterId) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if(campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if(character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

        List<Skill> skills = skillService.getAllCharacterSkills(characterId);
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @GetMapping("/{campaignId}/characters/{characterId}/skills/{skillId}")
    public ResponseEntity<?> getSkillById(@PathVariable Long campaignId, @PathVariable Long characterId, @PathVariable Long skillId) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if(campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if(character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);
        Skill skill = skillService.getCharacterSkill(characterId, skillId);
        if(skill != null)
            return new ResponseEntity<>(skill, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> processBody(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.get("name") == null)
                return new ResponseEntity<>("Skill must contain name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("name").isNull())
                return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String name = jsonNode.get("name").asText();

            if (Objects.equals(name, "null") || Objects.equals(name, null) || name.isEmpty())
                return new ResponseEntity<>("Skill name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);


            try {
                if (jsonNode.get("difficulty") == null)
                    return new ResponseEntity<>("Skill must contain difficulty", HttpStatus.UNPROCESSABLE_ENTITY);

                if (jsonNode.get("difficulty").isNull())
                    return new ResponseEntity<>("Skill difficulty cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

                String difficultyStr = jsonNode.get("difficulty").asText();
                Difficulty difficulty = Difficulty.valueOf(difficultyStr);

                if (jsonNode.get("level") == null)
                    return new ResponseEntity<>("Skill must contain level", HttpStatus.UNPROCESSABLE_ENTITY);

                if (jsonNode.get("level").isNull())
                    return new ResponseEntity<>("Skill level cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

                int level = jsonNode.get("level").asInt();

                if (level < 1) {
                    return new ResponseEntity<>("Skill level must be higher than 1", HttpStatus.UNPROCESSABLE_ENTITY);
                }

                String description = jsonNode.has("description") ? jsonNode.get("description").asText() : null;

                Skill skill = new Skill(name, null);
                skill.setDifficulty(difficulty);
                skill.setLevel(level);
                skill.setDescription(description);

                return new ResponseEntity<Skill>(skill, HttpStatus.OK);

            } catch (NumberFormatException e) {
                return new ResponseEntity<>("Character Id must be a number", HttpStatus.UNPROCESSABLE_ENTITY);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid difficulty value", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{campaignId}/characters/{characterId}/skills")
    public ResponseEntity<?> createSkill(@RequestBody String string, @PathVariable Long campaignId, @PathVariable Long characterId) {
        try {
            Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
            if(campaign.isEmpty())
                return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
            Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
            if(character == null)
                return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

            ResponseEntity<?> test = processBody(string);
            if (test.getBody() instanceof Skill skill) {
                skill.setCharacter(character);
                Skill createdSkill = skillService.saveSkill(skill);
                return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
            }
            return test;
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{campaignId}/characters/{characterId}/skills/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Long skillId, @RequestBody String string, @PathVariable Long campaignId, @PathVariable Long characterId) throws Exception {
        try {
            Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
            if(campaign.isEmpty())
                return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
            Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
            if(character == null)
                return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);
            Optional<Skill> toUpdate = Optional.ofNullable(skillService.getCharacterSkill(characterId, skillId));
            if (toUpdate.isEmpty())
                return new ResponseEntity<>("Skill with ID " + skillId + " does not exist in the campaign", HttpStatus.NOT_FOUND);
            ResponseEntity<?> test = processBody(string);
            if (test.getBody() instanceof Skill skill) {
                skill.setCharacter(character);
                Optional<Skill> updatedSkill = skillService.updateSkill(skillId, skill);
                return updatedSkill.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return test;
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{campaignId}/characters/{characterId}/skills/{skillId}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long skillId, @PathVariable Long campaignId, @PathVariable Long characterId) throws Exception {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if(campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if(character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

        Optional<Skill> toDelete = Optional.ofNullable(skillService.getCharacterSkill(characterId, skillId));
        if(toDelete.isEmpty())
            return new ResponseEntity<>("Skill is not in the character", HttpStatus.NOT_FOUND);
        boolean deleted = skillService.deleteSkill(skillId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
