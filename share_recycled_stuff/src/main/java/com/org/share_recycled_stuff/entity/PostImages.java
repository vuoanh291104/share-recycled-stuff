package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_images", indexes = {
        @Index(name = "idx_post_image_post", columnList = "post_id"),
        @Index(name = "idx_post_image_order", columnList = "display_order")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"post"})
@ToString(exclude = {"post"})
public class PostImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
