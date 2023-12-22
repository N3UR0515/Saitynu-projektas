package com.helper.gurps.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helper.gurps.Campaign;
import lombok.AllArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(Math.toIntExact(userId));
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) throws Exception {
        boolean deleted = false;
        Optional<User> user = userRepository.findById(Math.toIntExact(userId));
        if(user.isPresent())
        {
            userRepository.delete(user.get());
            deleted = true;
        }

        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> changeRole(@PathVariable Long userId, @RequestBody String role)
    {
        Optional<User> user = userRepository.findById(Math.toIntExact(userId));
        if(user.isPresent())
        {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(role);
                if(jsonNode.get("role") == null)
                    return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
                try {
                    Role role1 = Role.valueOf(jsonNode.get("role").asText());
                    user.get().setRole(role1);
                    userRepository.save(user.get());
                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
                }catch (Exception e) {
                    return new ResponseEntity<>("This ROLE doens't exist", HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
            catch (Exception e) {
                return new ResponseEntity<>("Invalid JSON syntax: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
