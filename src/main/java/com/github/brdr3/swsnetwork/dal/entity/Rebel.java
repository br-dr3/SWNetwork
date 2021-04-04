package com.github.brdr3.swsnetwork.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rebel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "rebel_id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true)
    private String name;

    private Date birthDate;

    @ColumnDefault("false")
    private boolean betrayal;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    private RebelBase rebelBase;

    @OneToMany(targetEntity = ItemPossession.class, cascade = CascadeType.ALL)
    private List<ItemPossession> inventory = new ArrayList<>(0);
}
