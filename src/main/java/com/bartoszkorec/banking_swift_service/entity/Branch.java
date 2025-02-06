package com.bartoszkorec.banking_swift_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "branches", schema = "public", indexes = {
        @Index(name = "idx_branches_headquarters", columnList = "hq_swift_code"),
        @Index(name = "idx_branches_locations", columnList = "location_id")
})
public class Branch {
    @Id
    @Column(name = "swift_code", nullable = false, length = 11)
    private String swiftCode;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "hq_swift_code", nullable = false)
    private Headquarters headquarters;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade =  {CascadeType.PERSIST, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

}