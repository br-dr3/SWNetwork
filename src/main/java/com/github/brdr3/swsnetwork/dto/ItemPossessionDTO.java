package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Setter
@Getter
@EqualsAndHashCode
public class ItemPossessionDTO {
    public UUID id;
    public String item;
    public int quantity;
}
