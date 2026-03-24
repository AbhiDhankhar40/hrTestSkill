package com.test.demo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ButtonDTO {

    private Long buttonId;
    private String buttonCode;
    private String buttonName;
}
