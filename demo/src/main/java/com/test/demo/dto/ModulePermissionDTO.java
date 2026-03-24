package com.test.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModulePermissionDTO {

    private Long moduleId;
    private String moduleName;
    private List<SubModulePermissionDTO> subModules;
}
