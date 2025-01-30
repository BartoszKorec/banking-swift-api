package com.bartoszkorec.banking_swift_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "countries", schema = "public")
public class Country {
    @Id
    @Column(name = "iso2_code", nullable = false, length = 2)
    private String iso2Code;

    @Column(name = "country_name", nullable = false, length = Integer.MAX_VALUE)
    private String countryName;

}