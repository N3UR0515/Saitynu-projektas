package com.helper.gurps.config.auth;

import com.helper.gurps.user.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Getter
    private String username;
    private String email;
    private String password;

    private Role role;

    public CharSequence getPassword() {
        return password;
    }

}
