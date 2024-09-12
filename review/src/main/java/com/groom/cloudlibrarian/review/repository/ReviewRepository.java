package com.groom.cloudlibrarian.review.repository;

import com.groom.cloudlibrarian.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(int bookId);
    Optional<Review> findByReviewId(Long reviewId);

}
