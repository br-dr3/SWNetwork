package com.github.brdr3.swsnetwork.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RebelDTO {
    private UUID id;
    private String name;
    private Date birthDate;
    private String gender;
    private RebelBaseDTO rebelBase;
}

