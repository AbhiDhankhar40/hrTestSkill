package com.test.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sub_module_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubModuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_module_id")
    private Long subModuleId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleMaster module;

    @Column(name = "sub_module_name", nullable = false, length = 100)
    private String subModuleName;

    @Column(name = "sub_module_code", nullable = false, unique = true, length = 50)
    private String subModuleCode;

    @Column(name = "sub_module_route", nullable = false, unique = true, length = 500)
    private String subModuleRoute;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("moduleId")
    public Long getModuleId() {
        return module != null ? module.getModuleId() : null;
    }

    @JsonProperty("moduleName")
    public String getModuleName() {
        return module != null ? module.getModuleName() : null;
    }
}
