package com.pasciitools.pithy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class AppConfig {

    @Id
    private String configKey;

    @Column(length = 4096)
    private String configValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppConfig appConfig = (AppConfig) o;
        return getConfigKey() != null && Objects.equals(getConfigKey(), appConfig.getConfigKey());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
