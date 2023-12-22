package com.helper.gurps.config;

import com.helper.gurps.Campaign;
import com.helper.gurps.CampaignRepository;
import com.helper.gurps.Character;
import com.helper.gurps.CharactersRepository;
import com.helper.gurps.user.Role;
import com.helper.gurps.user.User;
import com.helper.gurps.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final UserRepository userRepository;

    CampaignSecurity campaignSecurity = new CampaignSecurity();
    private final JwtService jwtService;

    private final CampaignRepository campaignRepository;
    private final CharacterSecurity characterSecurity;
    private final CharactersRepository characterRepository;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/register")
                .permitAll()
                .requestMatchers("/api/v1/auth/authenticate")
                .permitAll()
                .requestMatchers("/api/v1/auth/demo-controller")
                .authenticated()
                .requestMatchers("/api/v1/auth/refresh")
                .authenticated()
                .requestMatchers("/api/v1/auth/logout")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/campaign/")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}")
                .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/campaign/")
                .hasAnyAuthority("ADMIN", "GAMEMASTER")
                .requestMatchers(HttpMethod.PUT,"/api/campaign/{campaignId}").access(campaignSecurity)
                .requestMatchers(HttpMethod.DELETE,"/api/campaign/{campaignId}").access(campaignSecurity)
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters/{characterId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/campaign/{campaignId}/characters").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/campaign/{campaignId}/characters/{characterId}").access(characterSecurity)
                .requestMatchers(HttpMethod.DELETE, "/api/campaign/{campaignId}/characters/{characterId}").access(characterSecurity)
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters/{characterId}/skills").access(characterSecurity)
                .requestMatchers(HttpMethod.POST, "/api/campaign/{campaignId}/characters/{characterId}/skills").access(characterSecurity)
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters/{characterId}/skills/{skillId}").access(characterSecurity)
                .requestMatchers(HttpMethod.PUT, "/api/campaign/{campaignId}/characters/{characterId}/skills/{skillId}").access(characterSecurity)
                .requestMatchers(HttpMethod.DELETE, "/api/campaign/{campaignId}/characters/{characterId}/skills/{skillId}").access(characterSecurity)
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters/{characterId}/traits").access(characterSecurity)
                .requestMatchers(HttpMethod.POST, "/api/campaign/{campaignId}/characters/{characterId}/traits").access(characterSecurity)
                .requestMatchers(HttpMethod.GET, "/api/campaign/{campaignId}/characters/{characterId}/traits/{traitId}").access(characterSecurity)
                .requestMatchers(HttpMethod.PUT, "/api/campaign/{campaignId}/characters/{characterId}/traits/{traitId}").access(characterSecurity)
                .requestMatchers(HttpMethod.DELETE, "/api/campaign/{campaignId}/characters/{characterId}/traits/{traitId}").access(characterSecurity)
                .requestMatchers(HttpMethod.GET, "/api/user/").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/user/{userId}").hasAuthority("ADMIN")
                //.requestMatchers(HttpMethod.PUT, "/api/campaign/") //.access("hasAuthority('ADMIN') or (hasAuthority('GAMEMASTER') and #campaign.userId == authentication.principal.id)")
                //.antMatchers(HttpMethod.DELETE, "/api/campaign/**").access("hasAuthority('ADMIN') or (hasAuthority('GAMEMASTER') and #campaign.userId == authentication.principal.id)")
                .anyRequest()
                .hasAuthority("ADMIN")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Component
    public class CampaignSecurity implements AuthorizationManager<RequestAuthorizationContext>
    {
        @Override
        public AuthorizationDecision check(Supplier authenticationSupplier, RequestAuthorizationContext ctx) {
            Long campaignId = Long.parseLong(ctx.getVariables().get("campaignId"));
            final String authHeader = ctx.getRequest().getHeader("Authorization");
            final String jwt;
            String username = "";
            if(authHeader != null && authHeader.startsWith("Bearer "))
            {
                jwt = authHeader.substring(7);
                username = jwtService.extractUsername(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String role = userDetails.getAuthorities().toArray()[0].toString();
                if(Objects.equals(role, "ADMIN"))
                {
                    return new AuthorizationDecision(returnTrue());
                }
                if(Objects.equals(role, "PLAYER"))
                {
                    return new AuthorizationDecision(returnFalse());
                }
            }

            Authentication authentication = (Authentication) authenticationSupplier.get();
            return new AuthorizationDecision(hasUserId(authentication, campaignId, username));
        }
        public boolean hasUserId(Authentication authentication, Long campaignId, String username) {
            Optional<Campaign> camp = campaignRepository.findById(campaignId);
            return camp.filter(campaign -> Objects.equals(campaign.getUser().getUsername(), username)).isPresent();
        }

        public boolean returnTrue()
        {
            return true;
        }

        public boolean returnFalse() {return false;}
    }

}

