package com.bartoszkorec.banking_swift_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "locations", schema = "public", indexes = {
        @Index(name = "idx_locations_countries", columnList = "iso2_code")
})
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private Integer id;

    @Column(name = "address_line", nullable = false, length = Integer.MAX_VALUE)
    private String addressLine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "iso2_code", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "location")
    private Set<Branch> branches = new LinkedHashSet<>();

    @OneToMany(mappedBy = "location")
    private Set<Headquarter> headquarters = new LinkedHashSet<>();

}