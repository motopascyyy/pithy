package com.pasciitools.pithy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode
public class RoleDTO implements Comparable<RoleDTO>{
    private String roleName;
    private boolean enabled;

    @Override
    public int compareTo(@NotNull RoleDTO comparable) {
        if (this.equals(comparable))
            return 0;
        else if (this.getRoleName().equals(comparable.getRoleName()))
            return this.isEnabled() ? 1 : 0;
        else
            return this.getRoleName().compareTo(comparable.getRoleName());
    }
}
