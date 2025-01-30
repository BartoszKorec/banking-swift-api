package com.bartoszkorec.banking_swift_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "headquarters", schema = "public", indexes = {
        @Index(name = "idx_headquarters_locations", columnList = "location_id")
})
public class Headquarter {
    @Id
    @Column(name = "swift_code", nullable = false, length = 11)
    private String swiftCode;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "location_id", nullable = false)
    private com.bartoszkorec.banking_swift_service.entity.Location location;

    @OneToMany(mappedBy = "hqSwiftCode")
    private Set<Branch> branches = new HashSet<>();

}