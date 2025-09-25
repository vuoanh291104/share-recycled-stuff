package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImages, Long> {
}
