package com.test.demo.dto;

import java.time.LocalDateTime;
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
public class AccessControlResponseDTO {

    private Long userId;
    private String username;
    private String name;
    private LocalDateTime createdAt;
    private List<GroupDTO> groups;
    private List<ModuleDTO> modules;
}
