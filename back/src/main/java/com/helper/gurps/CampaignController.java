package com.helper.gurps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helper.gurps.config.JwtService;
import com.helper.gurps.user.User;
import com.helper.gurps.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("api/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;


    @GetMapping("/")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        return new ResponseEntity<>(campaigns, HttpStatus.OK);
    }

    private ResponseEntity<?> processBody(String jsonString)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if(jsonNode.get("name") == null)
                return new ResponseEntity<>("Campaign must have a name", HttpStatus.UNPROCESSABLE_ENTITY);

            if(jsonNode.get("name").isNull() || jsonNode.get("name").asText() == null || Objects.equals(jsonNode.get("name").asText(), "null") ||
                    Objects.equals(jsonNode.get("name").asText(), ""))
                return new ResponseEntity<>("Campaign must have a name", HttpStatus.UNPROCESSABLE_ENTITY);


            String name = jsonNode.get("name").asText();

            try{
                Campaign campaign = new Campaign();
                campaign.setName(name);
                if(jsonNode.get("ptsLimit") != null)
                {
                    int limit = Integer.parseInt(jsonNode.get("ptsLimit").asText());
                    if(limit < 0)
                        return new ResponseEntity<>("Campaign points must be a positive number", HttpStatus.UNPROCESSABLE_ENTITY);
                    campaign.setPtsLimit(limit);
                }

                return new ResponseEntity<Campaign>(campaign, HttpStatus.OK);

            }catch (Exception e)
            {
                return new ResponseEntity<>("Campaign points limit must be number", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        }catch (JsonParseException e) {
            return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Invalid JSON syntax", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createCampaign(@RequestBody String string, HttpServletRequest request) {
        ResponseEntity<?> response = processBody(string);
        if(response.getBody() instanceof Campaign)
        {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;
            if(authHeader != null && authHeader.startsWith("Bearer "))
            {
                jwt = authHeader.substring(7);
                username = jwtService.extractUsername(jwt);
                Optional<User> user = userRepository.findByUsername(username);
                ((Campaign) response.getBody()).setUser(user.get());
                Campaign createdCampaign = campaignService.saveCampaign((Campaign) response.getBody());
                return new ResponseEntity<>(createdCampaign, HttpStatus.CREATED);
            }
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return response;
        /*if(campaign.getName() == null)
        {
            return new ResponseEntity<>("Campaign name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Campaign createdCampaign = campaignService.saveCampaign(campaign);
        return new ResponseEntity<>(createdCampaign, HttpStatus.CREATED);*/
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<Campaign> getCampaignById(@PathVariable Long campaignId) {
        Optional<Campaign> campaign = campaignService.getCampaignById(campaignId);
        return campaign.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{campaignId}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long campaignId, @RequestBody String string) throws Exception {
        Optional<Campaign> toUpdate = campaignService.getCampaignById(campaignId);
        if(toUpdate.isEmpty())
            return new ResponseEntity<>("Campaign with ID " + campaignId + " does not exist", HttpStatus.NOT_FOUND);
        ResponseEntity<?> response = processBody(string);
        if(response.getBody() instanceof Campaign)
        {
            Optional<Campaign> updatedCampaign = campaignService.updateCampaign(campaignId, (Campaign) response.getBody());
            return updatedCampaign.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return response;
        /*if(campaign.getName() == null)
        {
            return new ResponseEntity<>("Campaign name cannot be null", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Optional<Campaign> updatedCampaign = campaignService.updateCampaign(campaignId, campaign);
        return updatedCampaign.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));*/
    }

    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId) throws Exception {
        boolean deleted = campaignService.deleteCampaign(campaignId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
