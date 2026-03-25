package com.test.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.Hibernate;

@Entity
@Table( name = "permission_master")
@JsonIgnoreProperties(value = {"buttons", "subModules"}, allowSetters = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionMaster {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(nullable = false, unique = true)
    private String permissionCode;  // e.g. ADMIN_USER_FULL_ACCESS

    private String permissionName;

       @Builder.Default
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "permission_button_mapping",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "button_id")
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<ButtonMaster> buttons;

       // ✅ ADD THIS RELATION
      @ManyToMany
    @JoinTable(
            name = "permission_submodule_mapping",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_module_id")
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<SubModuleMaster> subModules;

    @JsonProperty("buttonIds")
    public Set<Long> getButtonIds() {
        if (!Hibernate.isInitialized(buttons) || buttons == null || buttons.isEmpty()) {
            return Collections.emptySet();
        }
        return buttons.stream()
                .map(ButtonMaster::getButtonId)
                .collect(Collectors.toSet());
    }

    @JsonProperty("subModuleIds")
    public Set<Long> getSubModuleIds() {
        if (!Hibernate.isInitialized(subModules) || subModules == null || subModules.isEmpty()) {
            return Collections.emptySet();
        }
        return subModules.stream()
                .map(SubModuleMaster::getSubModuleId)
                .collect(Collectors.toSet());
    }
}
