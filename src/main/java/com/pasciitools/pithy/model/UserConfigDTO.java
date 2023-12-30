package com.pasciitools.pithy.model;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserConfigDTO {
    private List<UserDetails> userList;

    public UserConfigDTO(List<UserDetails> appConfigs) {
        userList = appConfigs;
    }

    public UserConfigDTO () {}

    public void addUser(UserDetails userDetails) {
        if (userList == null)
            userList = new ArrayList<>();

        userList.add(userDetails);
    }


}
