package com.github.brdr3.swsnetwork.dal.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_rebel_id", "reported_rebel_id"}))
public class BetrayalReport {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "betrayer_report_id", updatable = false, nullable = false)
    private UUID id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "reporter_rebel_id")
    private Rebel reporter;

    @ManyToOne
    @JoinColumn(name = "reported_rebel_id")
    private Rebel reported;

}
