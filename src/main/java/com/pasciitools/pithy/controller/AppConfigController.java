package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.data.AppConfigRepo;
import com.pasciitools.pithy.data.FixedPageRepository;
import com.pasciitools.pithy.model.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Controller
public class AppConfigController implements PithyController{

    private final AppConfigRepo appConfigRepo;
    private final JdbcUserDetailsManager userDetailsManager;
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;
    private FixedPageRepository fixedPageRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppConfigController.class);

    @PostMapping("/config/settings/save")
    public String saveConfigs(@ModelAttribute AppConfigDTO appConfigDTO, Model model) {
        appConfigRepo.saveAll(appConfigDTO.getAppConfigList());
        model.addAttribute("appConfigs", appConfigRepo.findAll());
        return "redirect:/config";
    }

    @PostMapping("/config/user/save")
    public String saveUsers(@ModelAttribute PithyUserDTO user) {
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        UserDetails existingUser = null;
        try {
            existingUser = userDetailsManager.loadUserByUsername(user.getUsername());
        } catch (UsernameNotFoundException ex) {
            //do nothing handled elsewhere
        }
        for (String roleString : user.getRoles()){
            roles.add(PithyRoles.valueOf(roleString).getRole());
        }
        var password = user.getPassword();
        if ((password == null || password.isBlank()) && existingUser != null)
            password = existingUser.getPassword();
        else
            password = passwordEncoder.encode(password);
        User userImpl = new User(user.getUsername(), password, user.isEnabled(), true, true, true, roles);
        if (existingUser == null)
            userDetailsManager.createUser(userImpl);
        else
            userDetailsManager.updateUser(userImpl);
        return "redirect:/config";
    }

    @GetMapping("/config")
    public String showEditAppConfigForm(ModelMap model) {
        var appConfigs = new ArrayList<AppConfig>();
        appConfigRepo.findAll().iterator().forEachRemaining(appConfigs::add);
        model.put("appConfigDTO", new AppConfigDTO(appConfigs));

        UserConfigDTO userList = new UserConfigDTO();
        var query = "SELECT username FROM users";

        List<UserDetails> userDetailsList = jdbcTemplate.query(query, (rs, rowNum) -> {
            return userDetailsManager.loadUserByUsername(rs.getString("username"));
        });

        userList.setUserList(userDetailsList);
        model.put("userConfigDTO", userList);
        model.putAll(loadHeaderLinks(fixedPageRepository));

        return "config";
    }

    @GetMapping("/config/edit/{username}")
    public String getUserEditScreen (@PathVariable String username, ModelMap model) {
        UserDetails user = userDetailsManager.loadUserByUsername(username);
        if (user != null) {

            var userDTO = new PithyUserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setEnabled(user.isEnabled());
            var rolesAsString = user.getAuthorities().stream().map(r -> r.getAuthority().substring("ROLE_".length())).toList();
            userDTO.setRoles(rolesAsString);
            model.put("user", userDTO);
            model.put("allRoles", createRoleList(user));
            model.putAll(loadHeaderLinks(fixedPageRepository));
            return "user-edit";
        } else {
            logger.error("User {} not found.", username);
            return "error";
        }
    }

    @GetMapping("/config/add-user")
    public String getUserAddScreen (ModelMap model) {
        var user = new PithyUserDTO();
        model.put("user", user);
        model.put("allRoles", createRoleList(null));
        model.putAll(loadHeaderLinks(fixedPageRepository));
        return "user-edit";
    }

    private List<RoleDTO> createRoleList(UserDetails user) {
        var roleList = new ArrayList<RoleDTO>();
        for (PithyRoles pithyRole : PithyRoles.values()){
            var role = new RoleDTO();
            role.setEnabled(user != null && user.getAuthorities().contains(pithyRole.getRole()));
            role.setRoleName(pithyRole.name());
            roleList.add(role);
        }
        Collections.sort(roleList);
        return roleList;
    }

}
