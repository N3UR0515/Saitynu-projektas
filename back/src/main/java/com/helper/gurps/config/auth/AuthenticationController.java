package com.helper.gurps.config.auth;

import com.helper.gurps.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import com.helper.gurps.config.JwtBlackList;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserRepository userRepository;

    @Autowired
    private final JwtBlackList jwtBlackList;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ){
        var test = userRepository.findByUsername(request.getUsername());
        if(test.isPresent())
            return ResponseEntity.unprocessableEntity().body("User already exists");
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@NonNull HttpServletRequest request) {
        AuthenticationResponse response = service.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Assuming the token starts with "Bearer "
        String jwt = token.substring(7);

        // Add the token to the blacklist
        jwtBlackList.blacklistToken(jwt);

        return ResponseEntity.ok("Logout successful");
    }

}
