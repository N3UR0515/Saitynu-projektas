package com.helper.gurps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharactersRepository extends JpaRepository<Character, Long> {
    @Query("SELECT c FROM Character c WHERE (c.hidden = false OR c.user.id = :userId) AND c.campaign.id = :campaignId")
    List<Character> findAllByCampaignId(@Param("campaignId") Long campaignId, @Param("userId") Long userId);

    @Query("SELECT c FROM Character c WHERE c.campaign.id = :campaignId")
    List<Character> findAllByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT c FROM Character c WHERE c.campaign.id = :campaignId AND c.id = :characterId")
    Character findCharacterInCampaign(@Param("campaignId") Long campaignId, @Param("characterId") Long characterId);

    @Query("SELECT c FROM Character c WHERE (c.hidden = false OR c.user.id = :userId) AND c.campaign.id = :campaignId AND c.id = :characterId")
    Character findCharacterInCampaign(@Param("campaignId") Long campaignId, @Param("characterId") Long characterId, @Param("userId") Long userId);



}
