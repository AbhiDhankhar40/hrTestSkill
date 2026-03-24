package com.test.demo.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "button_master")
@Getter
@Setter
public class ButtonMaster {

    @Id   // ✅ Correct Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "button_id")
    private Long buttonId;

    @Column(nullable = false)
    private String buttonName;

    @Column(nullable = false, unique = true)
    private String buttonCode;   // e.g. USER_CREATE

    private Boolean isActive = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_module_id", nullable = false)
    private SubModuleMaster subModule;

    @ManyToMany(mappedBy = "buttons")
    @JsonIgnore
    private Set<PermissionMaster> permissions;

    @JsonProperty("subModuleId")
    public Long getSubModuleId() {
        return subModule != null ? subModule.getSubModuleId() : null;
    }
}
