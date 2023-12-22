package com.helper.gurps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraitService {

    private final TraitRepository traitRepository;

    @Autowired
    public TraitService(TraitRepository traitRepository) {
        this.traitRepository = traitRepository;
    }

    public List<Trait> getAllTraits() {
        return traitRepository.findAll();
    }

    public Optional<Trait> getTraitByTraitId(Long traitId) {return traitRepository.findById(traitId);}

    public Trait getCharacterTrait(Long characterId, Long traitId)
    {
        return traitRepository.findCharacterTrait(characterId, traitId);
    }
    public List<Trait> getAllCharacterTraits(Long characterId)
    {
        return traitRepository.findAllByCharacterId(characterId);
    }

    public Trait saveTrait(Trait trait) {
        return traitRepository.save(trait);
    }

    public Optional<Trait> updateTrait(Long traitId, Trait updatedTrait) throws Exception {
        Optional<Trait> existingTrait = traitRepository.findById(traitId);

        if(existingTrait.isPresent())
        {
            Trait trait = existingTrait.get();
            trait.setName(updatedTrait.getName());
            trait.setCharacter(updatedTrait.getCharacter());
            trait.setPrice(updatedTrait.getPrice());
            if(updatedTrait.getDescription() != null)
                trait.setDescription(updatedTrait.getDescription());

            return Optional.of(traitRepository.save(trait));
        }
        else
        {
            throw new Exception("Trait not found");
        }
    }

    public boolean deleteTrait(Long traitId) throws Exception {
        Optional<Trait> existingTrait = traitRepository.findById(traitId);
        if(existingTrait.isPresent())
        {
            Trait trait = existingTrait.get();

            traitRepository.delete(trait);
            return true;
        }
        else
        {
            return false;
        }

    }
}
