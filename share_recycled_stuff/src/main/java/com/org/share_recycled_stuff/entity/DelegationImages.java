package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"delegationRequest"})
@ToString(exclude = {"delegationRequest"})
public class DelegationImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consignment_id", nullable = false)
    private DelegationRequests delegationRequest;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
