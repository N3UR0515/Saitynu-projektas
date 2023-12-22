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
@RequestMapping("/trait")
public class TraitController {

    private final TraitService traitService;
    private final CharactersService charactersService;

    @Autowired
    public TraitController(TraitService traitService, CharactersService charactersService) {
        this.traitService = traitService;
        this.charactersService = charactersService;
    }


    @GetMapping("/")
    public ResponseEntity<List<Trait>> getAllTraits() {
        List<Trait> traits = traitService.getAllTraits();
        return new ResponseEntity<>(traits, HttpStatus.OK);
    }

    @GetMapping("/{traitId}")
    public ResponseEntity<Trait> getTraitById(@PathVariable Long traitId) {
        Optional<Trait> trait = traitService.getTraitByTraitId(traitId);
        return trait.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> processBody(String jsonString)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if(jsonNode.get("name") == null)
                return new ResponseEntity<>("Trait must contain name", HttpStatus.UNPROCESSABLE_ENTITY);

            if(jsonNode.get("name").isNull())
                return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String name = jsonNode.get("name").asText();

            if(Objects.equals(name, "null") || Objects.equals(name, null) || name.isEmpty())
                return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            if(jsonNode.get("character") == null)
                return new ResponseEntity<>("Trait must contain character", HttpStatus.UNPROCESSABLE_ENTITY);

            if(jsonNode.get("character").isNull())
                return new ResponseEntity<>("Trait must contain character", HttpStatus.UNPROCESSABLE_ENTITY);

            if(jsonNode.get("character").get("id") == null)
                return new ResponseEntity<>("Character must contain Id", HttpStatus.UNPROCESSABLE_ENTITY);

            String characterId = jsonNode.get("character").get("id").asText();

            if(jsonNode.get("price") == null || jsonNode.get("price").isNull())
                return new ResponseEntity<>("Trait must contain price", HttpStatus.UNPROCESSABLE_ENTITY);

            try{
                Long idCampaign = Long.parseLong(characterId);
                Optional<Character> character = charactersService.getCharacterByCharacterId(idCampaign);

                if (character.isEmpty()) {
                    return new ResponseEntity<>("Character with ID " + characterId + " does not exist", HttpStatus.NOT_FOUND);
                }

                try{
                    int price = Integer.parseInt(jsonNode.get("price").asText());
                    Trait trait = new Trait(name, character.get(), price);
                    if(jsonNode.get("description") != null)
                        trait.setDescription(jsonNode.get("description").asText());
                    return new ResponseEntity<Trait>(trait, HttpStatus.OK);
                }catch (Exception e)
                {
                    return new ResponseEntity<>("Price must be numeric", HttpStatus.UNPROCESSABLE_ENTITY);
                }



            }catch (Exception e)
            {
                return new ResponseEntity<>("Character Id must be number", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/")
    public ResponseEntity<?> createTrait(@RequestBody String string) {
        try
        {
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Trait)
            {
                Trait createdTrait = traitService.saveTrait((Trait)test.getBody());
                return new ResponseEntity<>(createdTrait, HttpStatus.CREATED);
            }
            return test;
        }catch (NullPointerException e)
        {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
        /*if(trait.getName() == null)
        {
            return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long characterId = trait.getCharacter().getId();
        Optional<Character> character = charactersService.getCharacterByCharacterId(characterId);

        if (character.isEmpty()) {
            return new ResponseEntity<>("Character with ID " + character + " does not exist", HttpStatus.NOT_FOUND);
        }
        Trait createdTrait = traitService.saveTrait(trait);
        return new ResponseEntity<>(createdTrait, HttpStatus.CREATED);*/
    }

    @PutMapping("/{traitId}")
    public ResponseEntity<?> updateTrait(@PathVariable Long traitId, @RequestBody String string) throws Exception {
        try
        {
            Optional<Trait> toUpdate = traitService.getTraitByTraitId(traitId);
            if(toUpdate.isEmpty())
                return new ResponseEntity<>("Trait with ID " + traitId + " does not exist", HttpStatus.NOT_FOUND);
            ResponseEntity<?> test = processBody(string);
            if(test.getBody() instanceof Trait)
            {
                Optional<Trait> updatedTrait = traitService.updateTrait(traitId, (Trait)test.getBody());
                return updatedTrait.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return test;
        }catch (NullPointerException e)
        {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
        /*if(trait.getName() == null)
        {
            return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long characterId = trait.getCharacter().getId();
        Optional<Character> character = charactersService.getCharacterByCharacterId(characterId);

        if (character.isEmpty()) {
            return new ResponseEntity<>("Character with ID " + character + " does not exist", HttpStatus.NOT_FOUND);
        }
        Optional<Trait> updatedTrait = traitService.updateTrait(traitId, trait);
        return updatedTrait.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));*/
    }

    @DeleteMapping("/{traitId}")
    public ResponseEntity<Void> deleteTrait(@PathVariable Long traitId) throws Exception {
        boolean deleted = traitService.deleteTrait(traitId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

@RestController
@RequestMapping("/api/campaign")
class TraitController1 {
    private final TraitService traitService;
    private final CharactersService charactersService;
    private final CampaignService campaignService;

    @Autowired
    public TraitController1(TraitService traitService, CharactersService charactersService, CampaignService campaignService) {
        this.traitService = traitService;
        this.charactersService = charactersService;
        this.campaignService = campaignService;
    }

    @GetMapping("/{campaignId}/characters/{characterId}/traits")
    public ResponseEntity<?> getAllTraits(@PathVariable Long campaignId, @PathVariable Long characterId) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if (character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

        List<Trait> traits = traitService.getAllCharacterTraits(characterId);
        return new ResponseEntity<>(traits, HttpStatus.OK);
    }

    @GetMapping("/{campaignId}/characters/{characterId}/traits/{traitId}")
    public ResponseEntity<?> getTraitById(@PathVariable Long campaignId, @PathVariable Long characterId, @PathVariable Long traitId) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if (character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);
        Trait trait = traitService.getCharacterTrait(characterId, traitId);
        if (trait != null)
            return new ResponseEntity<>(trait, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> processBody(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.get("name") == null)
                return new ResponseEntity<>("Trait must contain name", HttpStatus.UNPROCESSABLE_ENTITY);

            if (jsonNode.get("name").isNull())
                return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);

            String name = jsonNode.get("name").asText();

            if (Objects.equals(name, "null") || Objects.equals(name, null) || name.isEmpty())
                return new ResponseEntity<>("Trait name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);


            if (jsonNode.get("price") == null || jsonNode.get("price").isNull())
                return new ResponseEntity<>("Trait must contain price", HttpStatus.UNPROCESSABLE_ENTITY);

            try {

                try {
                    int price = Integer.parseInt(jsonNode.get("price").asText());
                    Trait trait = new Trait(name, null, price);
                    if (jsonNode.get("description") != null)
                        trait.setDescription(jsonNode.get("description").asText());
                    return new ResponseEntity<Trait>(trait, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Price must be numeric", HttpStatus.UNPROCESSABLE_ENTITY);
                }


            } catch (Exception e) {
                return new ResponseEntity<>("Character Id must be number", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{campaignId}/characters/{characterId}/traits")
    public ResponseEntity<?> createTrait(@RequestBody String string, @PathVariable Long campaignId, @PathVariable Long characterId) {
        try {
            Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
            if (campaign.isEmpty())
                return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
            Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
            if (character == null)
                return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

            ResponseEntity<?> test = processBody(string);
            if (test.getBody() instanceof Trait trait) {
                trait.setCharacter(character);
                Trait createdTrait = traitService.saveTrait(trait);
                return new ResponseEntity<>(createdTrait, HttpStatus.CREATED);
            }
            return test;
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{campaignId}/characters/{characterId}/traits/{traitId}")
    public ResponseEntity<?> updateTrait(@PathVariable Long traitId, @RequestBody String string, @PathVariable Long campaignId, @PathVariable Long characterId) throws Exception {
        try {
            Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
            if (campaign.isEmpty())
                return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
            Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
            if (character == null)
                return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);
            Optional<Trait> toUpdate = Optional.ofNullable(traitService.getCharacterTrait(characterId, traitId));
            if (toUpdate.isEmpty())
                return new ResponseEntity<>("Trait with ID " + traitId + " does not exist in the campaign", HttpStatus.NOT_FOUND);
            ResponseEntity<?> test = processBody(string);
            if (test.getBody() instanceof Trait trait) {
                trait.setCharacter(character);
                Optional<Trait> updatedTrait = traitService.updateTrait(traitId, trait);
                return updatedTrait.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            return test;
        } catch (NullPointerException e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{campaignId}/characters/{characterId}/traits/{traitId}")
    public ResponseEntity<?> deleteTrait(@PathVariable Long traitId, @PathVariable Long campaignId, @PathVariable Long characterId) throws Exception {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        if (campaign.isEmpty())
            return new ResponseEntity<>("Campaign wasn't found", HttpStatus.NOT_FOUND);
        Character character = charactersService.getCharacterInCampaign(campaignId, characterId);
        if (character == null)
            return new ResponseEntity<>("Character is not in the campaign or doesn't exist", HttpStatus.NOT_FOUND);

        Optional<Trait> toDelete = Optional.ofNullable(traitService.getCharacterTrait(characterId, traitId));
        if (toDelete.isEmpty())
            return new ResponseEntity<>("Trait is not in the character", HttpStatus.NOT_FOUND);
        boolean deleted = traitService.deleteTrait(traitId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
