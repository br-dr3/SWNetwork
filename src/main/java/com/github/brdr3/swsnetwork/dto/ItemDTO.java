package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Setter
@Getter
public class ItemDTO {
    public UUID id;
    public String name;
    public int quantity;
}
