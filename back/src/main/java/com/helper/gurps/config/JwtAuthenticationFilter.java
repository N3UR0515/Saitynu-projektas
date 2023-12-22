package com.helper.gurps.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Autowired
    private final JwtBlackList jwtBlackList;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("autentifikacija");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && !jwtBlackList.isTokenBlacklisted(jwt)) {
                System.out.println(username);
                if (jwtService.isTokenValid(jwt, userDetails) || jwtService.isRefreshToken(jwt, userDetails)) {
                    System.out.println("access");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                if (jwtService.isRefreshToken(jwt, userDetails)) {
                    String newAccessToken = jwtService.generateToken(userDetails);

                    //response.setHeader("Authorization", newAccessToken);
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}
