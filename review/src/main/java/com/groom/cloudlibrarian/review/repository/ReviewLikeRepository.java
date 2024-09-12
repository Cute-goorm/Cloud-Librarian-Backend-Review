package com.groom.cloudlibrarian.review.repository;

import com.groom.cloudlibrarian.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Integer> {

    Optional<ReviewLike> findByUserIdAndReview_ReviewId(Integer userId, Long reviewId);

}
