package com.pasciitools.pithy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PithyUserDTO {
    private String username;
    private String password;
    private List<String> roles;
    private boolean enabled;
}
