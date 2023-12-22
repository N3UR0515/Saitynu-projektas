package com.helper.gurps;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helper.gurps.config.JwtService;
import com.helper.gurps.user.User;
import com.helper.gurps.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/character")
@RequiredArgsConstructor
public class CharactersController {
    private final CharactersService charactersService;
    private final CampaignService campaignService;

    private final JwtService jwtService;


    @GetMapping("/")
    public ResponseEntity<List<Character>> getAllCharacters() {
        List<Character> characters = charactersService.getAllCharacters();
        System.out.println("Hello");
        return new ResponseEntity<>(characters, HttpStatus.OK);
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<Character> getCharacterById(@PathVariable Long characterId) {
        Optional<Character> character = charactersService.getCharacterByCharacterId(characterId);
        return character.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> processBody(String jsonString)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.get("first_name") == null)
                return new ResponseEntity<>("Character must have a first name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("first_name").isNull())
                return new ResponseEntity<>("Character's first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String firstName = jsonNode.get("first_name").asText();

            if (Objects.equals(firstName, "null") || Objects.equals(firstName, null) || firstName.isEmpty())
                return new ResponseEntity<>("Character's first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("last_name") == null)
                return new ResponseEntity<>("Character must have a last name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("last_name").isNull())
                return new ResponseEntity<>("Character's last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String lastName = jsonNode.get("last_name").asText();

            if (Objects.equals(lastName, "null") || Objects.equals(lastName, null) || lastName.isEmpty())
                return new ResponseEntity<>("Character's last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("campaign") == null)
                return new ResponseEntity<>("Character must have a campaign", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("campaign").isNull())
                return new ResponseEntity<>("Character's campaign cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("campaign").get("id") == null)
                return new ResponseEntity<>("Campaign must contain Id", HttpStatus.UNPROCESSABLE_ENTITY);

            String campaignId = jsonNode.get("campaign").get("id").asText();

            try {
                Long idCampaign = Long.parseLong(campaignId);
                Optional<Campaign> campaign = campaignService.getCampaignById(idCampaign);

                if (campaign.isEmpty()) {
                    return new ResponseEntity<>("Campaign with ID " + campaignId + " does not exist", HttpStatus.NOT_FOUND);
                }

                Character character = new Character(firstName, lastName, campaign.get());

                if (jsonNode.has("photo")) {
                    if (jsonNode.get("photo").isTextual()) {
                        character.setPhoto(jsonNode.get("photo").asText());
                    } else {
                        return new ResponseEntity<>("Invalid data type for 'photo'", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }

                if (jsonNode.has("strength")) {
                    if (jsonNode.get("strength").isInt()) {
                        character.setStrength(jsonNode.get("strength").intValue());
                    } else {
                        return new ResponseEntity<>("Invalid data type for 'strength'", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }

                try{
                if (jsonNode.has("dex")) {
                    if (jsonNode.get("dex").isInt()) {
                        character.setDex(jsonNode.get("dex").intValue());
                    } else {
                        return new ResponseEntity<>("Invalid data type for 'dex'", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }

                    if (jsonNode.has("intelligence")) {
                        if (jsonNode.get("intelligence").isInt()) {
                            character.setIntelligence(jsonNode.get("intelligence").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'intelligence'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("health")) {
                        if (jsonNode.get("health").isInt()) {
                            character.setHealth(jsonNode.get("health").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'health'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("hitPoints")) {
                        if (jsonNode.get("hitPoints").isInt()) {
                            character.setHitPoints(jsonNode.get("hitPoints").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'hitPoints'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("will")) {
                        if (jsonNode.get("will").isInt()) {
                            character.setWill(jsonNode.get("will").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'will'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("perception")) {
                        if (jsonNode.get("perception").isInt()) {
                            character.setPerception(jsonNode.get("perception").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'perception'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("basicSpeed")) {
                        if (jsonNode.get("basicSpeed").isDouble()) {
                            character.setBasicSpeed(jsonNode.get("basicSpeed").floatValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'basicSpeed'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("basicMove")) {
                        if (jsonNode.get("basicMove").isInt()) {
                            character.setBasicMove(jsonNode.get("basicMove").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'basicMove'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    return new ResponseEntity<Character>(character, HttpStatus.OK);
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>("Campaign Id must be a number", HttpStatus.UNPROCESSABLE_ENTITY);
                }

            } catch (JsonParseException e) {
                return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
            }

        }catch (JsonParseException e) {
            return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/")
    public ResponseEntity<?> createCharacter(@RequestBody String string) {
        try {
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Character)
            {
                Character createdCharacter = charactersService.saveCharacter((Character) test.getBody());
                return new ResponseEntity<>(createdCharacter, HttpStatus.CREATED);
            }
            return test;
        }catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }


        /*if(character.getFirst_name() == null)
        {
            return new ResponseEntity<>("Character first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(character.getLast_name() == null)
        {
            return new ResponseEntity<>("Character last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(character.getCampaign() == null || character.getCampaign().getId() == null)
        {
            return new ResponseEntity<>("Campaign cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long campaignId = character.getCampaign().getId();

        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);

        if (campaign.isEmpty()) {
            return new ResponseEntity<>("Campaign with ID " + campaignId + " does not exist", HttpStatus.NOT_FOUND);
        }
        Character createdCharacter = charactersService.saveCharacter(character)*/
        //Character createdCharacter = new Character();
        //return new ResponseEntity<>(createdCharacter, HttpStatus.CREATED);
    }




    @PutMapping("/{characterId}")
    public ResponseEntity<?> updateCharacter(@PathVariable Long characterId, @RequestBody String string) throws Exception {
        Optional<Character> toUpdate = charactersService.getCharacterByCharacterId(characterId);
        if(toUpdate.isEmpty())
            return new ResponseEntity<>("Character with ID " + characterId + " does not exist", HttpStatus.NOT_FOUND);
        try {
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Character)
            {
                Character updatedCharacter = charactersService.updateCharacter(characterId, (Character) test.getBody());
                if (updatedCharacter != null) {
                    return new ResponseEntity<>(updatedCharacter, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
            return test;
        }
        catch (NullPointerException e)
        {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }

        /*if(character.getFirst_name() == null)
        {
            return new ResponseEntity<>("Character first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(character.getLast_name() == null)
        {
            return new ResponseEntity<>("Character last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(character.getCampaign() == null || character.getCampaign().getId() == null)
        {
            return new ResponseEntity<>("Campaign cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long campaignId = character.getCampaign().getId();
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);

        if (campaign.isEmpty()) {
            return new ResponseEntity<>("Campaign with ID " + campaignId + " does not exist", HttpStatus.NOT_FOUND);
        }
        Character updatedCharacter = charactersService.updateCharacter(characterId, character);
        if (updatedCharacter != null) {
            return new ResponseEntity<>(updatedCharacter, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) throws Exception {
        if (charactersService.deleteCharacter(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
class CharactersController1 {

    private final CharactersService charactersService;
    private final CampaignService campaignService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/api/campaign/{campaignId}/characters")
    public ResponseEntity<?> getAllCharacters(@PathVariable Long campaignId, HttpServletRequest request) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign with this id doesn't exist", HttpStatus.NOT_FOUND);
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);

            final String username = jwtService.extractUsername(jwt);

            Optional<User> user = userRepository.findByUsername(username);
            List<Character> characters;
            if (user.get().getCampaigns().contains(campaign.get()))
                characters = charactersService.getAllCharactersInCampaign(campaignId);
            else
                characters = charactersService.getAllCharactersInCampaign(campaignId, Long.valueOf(user.get().getId()));

            return new ResponseEntity<>(characters, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/api/campaign/{campaignId}/characters/{characterId}")
    public ResponseEntity<?> getCharacterById(@PathVariable Long campaignId, @PathVariable Long characterId, HttpServletRequest request) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign with this id doesn't exist", HttpStatus.NOT_FOUND);
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                Character character;
                if (user.get().getCampaigns().contains(campaign))
                    character = charactersService.getCharacterInCampaign(campaignId, characterId);
                else
                    character = charactersService.getCharacterInCampaign(campaignId, characterId, Long.valueOf(user.get().getId()));
                if (character != null)
                    return new ResponseEntity<>(character, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Character is not in the campaign", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> processBody(String jsonString, Long campaignId) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.get("first_name") == null)
                return new ResponseEntity<>("Character must have a first name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("first_name").isNull())
                return new ResponseEntity<>("Character's first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String firstName = jsonNode.get("first_name").asText();

            if (Objects.equals(firstName, "null") || Objects.equals(firstName, null) || firstName.isEmpty())
                return new ResponseEntity<>("Character's first name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("last_name") == null)
                return new ResponseEntity<>("Character must have a last name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("last_name").isNull())
                return new ResponseEntity<>("Character's last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String lastName = jsonNode.get("last_name").asText();

            if (Objects.equals(lastName, "null") || Objects.equals(lastName, null) || lastName.isEmpty())
                return new ResponseEntity<>("Character's last name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);


            try {
                Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);

                if (campaign.isEmpty()) {
                    return new ResponseEntity<>("Campaign with ID " + campaignId + " does not exist", HttpStatus.NOT_FOUND);
                }

                Character character = new Character(firstName, lastName, campaign.get());

                if (jsonNode.has("photo")) {
                    if (jsonNode.get("photo").isTextual()) {
                        character.setPhoto(jsonNode.get("photo").asText());
                    } else {
                        return new ResponseEntity<>("Invalid data type for 'photo'", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }

                if (jsonNode.has("strength")) {
                    if (jsonNode.get("strength").isInt()) {
                        character.setStrength(jsonNode.get("strength").intValue());
                    } else {
                        return new ResponseEntity<>("Invalid data type for 'strength'", HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }

                try {
                    if (jsonNode.has("dex")) {
                        if (jsonNode.get("dex").isInt()) {
                            character.setDex(jsonNode.get("dex").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'dex'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("intelligence")) {
                        if (jsonNode.get("intelligence").isInt()) {
                            character.setIntelligence(jsonNode.get("intelligence").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'intelligence'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("health")) {
                        if (jsonNode.get("health").isInt()) {
                            character.setHealth(jsonNode.get("health").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'health'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("hitPoints")) {
                        if (jsonNode.get("hitPoints").isInt()) {
                            character.setHitPoints(jsonNode.get("hitPoints").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'hitPoints'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("will")) {
                        if (jsonNode.get("will").isInt()) {
                            character.setWill(jsonNode.get("will").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'will'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("perception")) {
                        if (jsonNode.get("perception").isInt()) {
                            character.setPerception(jsonNode.get("perception").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'perception'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("basicSpeed")) {
                        if (jsonNode.get("basicSpeed").isDouble() || jsonNode.get("basicSpeed").isInt()) {
                            character.setBasicSpeed(jsonNode.get("basicSpeed").floatValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'basicSpeed'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("basicMove")) {
                        if (jsonNode.get("basicMove").isInt()) {
                            character.setBasicMove(jsonNode.get("basicMove").intValue());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'basicMove'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    if (jsonNode.has("hidden")) {
                        if (jsonNode.get("hidden").isBoolean()) {
                            character.setHidden(jsonNode.get("hidden").asBoolean());
                        } else {
                            return new ResponseEntity<>("Invalid data type for 'hidden'", HttpStatus.UNPROCESSABLE_ENTITY);
                        }
                    }

                    return new ResponseEntity<Character>(character, HttpStatus.OK);
                } catch (NumberFormatException e) {
                    return new ResponseEntity<>("Campaign Id must be a number", HttpStatus.UNPROCESSABLE_ENTITY);
                }

            } catch (JsonParseException e) {
                return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
            }

        } catch (JsonParseException e) {
            return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/campaign/{campaignId}/characters")
    public ResponseEntity<?> addCharacter(@RequestBody String string, @PathVariable Long campaignId, HttpServletRequest request) {
        try {
            ResponseEntity<?> response = processBody(string, campaignId);
            if (response.getBody() instanceof Character) {
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String username;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                    username = jwtService.extractUsername(jwt);
                    Optional<User> user = userRepository.findByUsername(username);
                    ((Character) response.getBody()).setUser(user.get());
                    Character createdCharacter = charactersService.saveCharacter((Character) response.getBody());
                    return new ResponseEntity<>(createdCharacter, HttpStatus.CREATED);
                }
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return response;
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/campaign/{campaignId}/characters/{characterId}")
    public ResponseEntity<?> renewCharacter(@PathVariable Long characterId, @PathVariable Long campaignId, @RequestBody String string, HttpServletRequest request) throws Exception {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign with this id doesn't exist", HttpStatus.NOT_FOUND);
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            Optional<User> user = userRepository.findByUsername(username);

            Character toUpdate;
            if (user.isPresent()) {
                if (user.get().getCampaigns().contains(campaign.get())) {
                    toUpdate = charactersService.getCharacterInCampaign(campaignId, characterId);
                } else
                    toUpdate = charactersService.getCharacterInCampaign(campaignId, characterId, Long.valueOf(user.get().getId()));
                if (toUpdate != null && Objects.equals(toUpdate.getCampaign().getId(), campaignId)) {
                    ResponseEntity<?> response = processBody(string, campaignId);
                    if (response.getBody() instanceof Character) {
                        Character updatedCharacter = charactersService.updateCharacter(characterId, (Character) response.getBody());
                        if (updatedCharacter != null) {
                            return new ResponseEntity<>(updatedCharacter, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>("Character doesn't belong in this campaign", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/campaign/{campaignId}/characters/{characterId}")
    public ResponseEntity<?> deleteCharacter(@PathVariable Long campaignId, @PathVariable Long characterId, HttpServletRequest request) throws Exception {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign with this id doesn't exist", HttpStatus.NOT_FOUND);
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);
            Optional<User> user = userRepository.findByUsername(username);

            if (user.isPresent()) {
                Character toDelete;
                if (user.get().getCampaigns().contains(campaign.get())) {
                    toDelete = charactersService.getCharacterInCampaign(campaignId, characterId);
                } else
                    toDelete = charactersService.getCharacterInCampaign(campaignId, characterId, Long.valueOf(user.get().getId()));

                if (toDelete != null && Objects.equals(toDelete.getCampaign().getId(), campaignId)) {
                    charactersService.deleteCharacter(characterId);
                    return new ResponseEntity<>(toDelete, HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>("Character doesn't belong in this campaign or doesn't exist", HttpStatus.NOT_FOUND);
                }
            }
        }
        return new ResponseEntity<>("Character doesn't belong in this campaign", HttpStatus.NOT_FOUND);
    }

}
