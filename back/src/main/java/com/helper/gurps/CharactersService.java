package com.helper.gurps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharactersService {
    private final CharactersRepository charactersRepository;

    @Autowired
    public CharactersService(CharactersRepository charactersRepository)
    {
        this.charactersRepository = charactersRepository;
    }

    public List<Character> getAllCharacters()
    {
        return charactersRepository.findAll();
    }

    public List<Character> getAllCharactersInCampaign(Long campaignId, Long userId)
    {
        return charactersRepository.findAllByCampaignId(campaignId, userId);
    }

    public List<Character> getAllCharactersInCampaign(Long campaignId)
    {
        return charactersRepository.findAllByCampaignId(campaignId);
    }

    public Character getCharacterInCampaign(Long campaignId, Long characterId)
    {
        return (Character) charactersRepository.findCharacterInCampaign(campaignId, characterId);
    }

    public Character getCharacterInCampaign(Long campaignId, Long characterId, Long userId)
    {
        return (Character) charactersRepository.findCharacterInCampaign(campaignId, characterId, userId);
    }

    public Optional<Character> getCharacterByCharacterId(Long characterId) {return charactersRepository.findById(characterId);}

    public Character saveCharacter(Character character) {
//        if(character.getStrength() == -1)
//            character.setStrength(0);
//        if(character.getDex() == -1)
//            character.setDex(0);
//        if(character.getBasicMove()== -1)
//            character.setBasicMove(0);
//        if(character.getHealth() == -1)
//            character.setHealth(0);
//        if(character.getBasicSpeed() == -1)
//            character.setBasicSpeed(0);
//        if(character.getIntelligence() == -1)
//            character.setIntelligence(0);
//        if(character.getWill() == -1)
//            character.setWill(0);
//        if(character.getPerception() == -1)
//            character.setPerception(0);
//        if(character.getHitPoints() == -1)
//            character.setHitPoints(0);
        return charactersRepository.save(character);
    }


    public Character updateCharacter(Long characterId, Character updatedCharacter) throws Exception {
        Optional<Character> existingCharacter = charactersRepository.findById(characterId);

        if(existingCharacter.isPresent())
        {
            Character character = existingCharacter.get();
            character.setFirst_name(updatedCharacter.getFirst_name());
            character.setLast_name(updatedCharacter.getLast_name());
            if(updatedCharacter.getPhoto() != null)
                character.setPhoto(updatedCharacter.getPhoto());

            //if(updatedCharacter.getStrength() > -1)
                character.setStrength(updatedCharacter.getStrength());
            //if(updatedCharacter.getDex() > -1)
                character.setDex(updatedCharacter.getDex());
            //if(updatedCharacter.getIntelligence() > -1)
                character.setIntelligence(updatedCharacter.getIntelligence());
            //if(updatedCharacter.getHealth() > -1)
                character.setHealth(updatedCharacter.getHealth());
            //if(updatedCharacter.getHitPoints() > -1)
                character.setHitPoints(updatedCharacter.getHitPoints());
            //if(updatedCharacter.getWill() > -1)
                character.setWill(updatedCharacter.getWill());
            //if(updatedCharacter.getPerception() > -1)
                character.setPerception(updatedCharacter.getPerception());
            //if(updatedCharacter.getBasicSpeed() > -1)
                character.setBasicSpeed(updatedCharacter.getBasicSpeed());
            //if(updatedCharacter.getBasicMove() > -1)
                character.setBasicMove(updatedCharacter.getBasicMove());

            return charactersRepository.save(character);
        }
        else
        {
            throw new Exception("Character not found");
        }
    }

    public boolean deleteCharacter(Long characterId) throws Exception {
        Optional<Character> existingCharacter = charactersRepository.findById(characterId);
        if(existingCharacter.isPresent())
        {
            Character character = existingCharacter.get();

            character.getUser().getCharacters().remove(character);

            charactersRepository.delete(character);
            return true;
        }
        else
        {
            return false;
        }

    }
}
