package com.helper.gurps.config;

import com.helper.gurps.Campaign;
import com.helper.gurps.Character;
import com.helper.gurps.CharactersRepository;
import com.helper.gurps.user.User;
import com.helper.gurps.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class CharacterSecurity implements AuthorizationManager<RequestAuthorizationContext>
{
    private final UserDetailsService userDetailsService;
    private final CharactersRepository characterRepository;
    private final UserRepository userRepository;
    @Override
    public AuthorizationDecision check(Supplier authenticationSupplier, RequestAuthorizationContext ctx) {
        Long characterId = Long.parseLong(ctx.getVariables().get("characterId"));
        final String authHeader = ctx.getRequest().getHeader("Authorization");
        final String jwt;
        String username = "";
        if(authHeader != null && authHeader.startsWith("Bearer "))
        {
            jwt = authHeader.substring(7);
            JwtService jwtService = new JwtService();
            username = jwtService.extractUsername(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String role = userDetails.getAuthorities().toArray()[0].toString();
            if(Objects.equals(role, "ADMIN"))
            {
                return new AuthorizationDecision(returnTrue());
            }
            if(Objects.equals(role, "GAMEMASTER"))
            {
                System.out.println("VEIK");
                System.out.println(gameBelongsTo(characterId, username));
                return new AuthorizationDecision(gameBelongsTo(characterId, username));
            }
        }

        Authentication authentication = (Authentication) authenticationSupplier.get();
        return new AuthorizationDecision(hasUserId(authentication, characterId, username));
    }
    public boolean hasUserId(Authentication authentication, Long characterId, String username) {
        Optional<Character> character = characterRepository.findById(characterId);
        return character.filter(character1 -> Objects.equals(character1.getUser().getUsername(), username)).isPresent();
    }

    public boolean gameBelongsTo(Long characterId, String username)
    {
        Optional<Character> character = characterRepository.findById(characterId);
        if(character.isPresent())
        {
            Campaign campaign = character.get().getCampaign();
            Optional<User> user = userRepository.findByUsername(username);
            if(user.isPresent())
            {
                System.out.println(user.get().getCampaigns().get(0).getName());
                System.out.println(campaign.getName());

                for(Campaign cam : user.get().getCampaigns())
                {
                    if(Objects.equals(cam.getId(), campaign.getId()))
                        return true;
                }
            }
        }

        return false;
    }

    public boolean returnTrue()
    {
        return true;
    }
}
