package com.test.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubModuleDTO {

    private Long subModuleId;
    private String subModuleName;
    private String subModuleRoute;
    private List<PermissionDTO> permissions;
}
