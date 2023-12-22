package com.helper.gurps;

import com.helper.gurps.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Optional<Campaign> getCampaignById(Long campaignId) {return campaignRepository.findById(campaignId);}

    public Campaign saveCampaign(Campaign campaign) {
        if(campaign.getPtsLimit() == -1)
            campaign.setPtsLimit(0);
        return campaignRepository.save(campaign);
    }


    public Optional<Campaign> updateCampaign(Long campaignId, Campaign updatedCampaign) throws Exception {
        Optional<Campaign> existingCampaign = campaignRepository.findById(campaignId);

        if(existingCampaign.isPresent())
        {
            Campaign campaign = existingCampaign.get();
            campaign.setName(updatedCampaign.getName());
            if(updatedCampaign.getPtsLimit() > -1)
                campaign.setPtsLimit(updatedCampaign.getPtsLimit());

            return Optional.of(campaignRepository.save(campaign));
        }
        else
        {
            throw new Exception("Campaign not found");
        }
    }

    public boolean deleteCampaign(Long campaignId) throws Exception {
        Optional<Campaign> existingCampaign = campaignRepository.findById(campaignId);
        if(existingCampaign.isPresent())
        {
            Campaign campaign = existingCampaign.get();

            campaign.getUser().getCampaigns().remove(campaign);

            campaignRepository.delete(campaign);
            return true;
        }
        else
        {
            return false;
        }

    }
}
