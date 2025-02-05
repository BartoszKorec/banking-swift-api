package com.bartoszkorec.banking_swift_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries", schema = "public")
public class Country {
    @Id
    @Column(name = "iso2_code", nullable = false, length = 2)
    private String iso2Code;

    @Column(name = "country_name", nullable = false, length = Integer.MAX_VALUE)
    private String countryName;

    @OneToMany(mappedBy = "country")
    private Set<Location> locations = new HashSet<>();

}