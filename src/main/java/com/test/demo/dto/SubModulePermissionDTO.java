package com.test.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubModulePermissionDTO {

    private Long subModuleId;
    private String subModuleName;
    private List<ButtonDTO> buttons;
}
