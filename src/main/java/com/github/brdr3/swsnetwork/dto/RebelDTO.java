package com.github.brdr3.swsnetwork.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RebelDTO {
    private UUID id;
    private String name;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date birthDate;

    private String gender;
    private boolean betrayal;
    private RebelBaseDTO rebelBase;
    private List<ItemPossessionDTO> inventory;
}


