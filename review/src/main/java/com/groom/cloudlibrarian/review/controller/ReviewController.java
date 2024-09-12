package com.groom.cloudlibrarian.review.controller;

import com.groom.cloudlibrarian.review.dto.ReviewDTO;
import com.groom.cloudlibrarian.review.entity.Review;
import com.groom.cloudlibrarian.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    //////////////////////////////작성
    @PostMapping("/{book_id}/reviews")
    public ResponseEntity<Review> createReview(
            @PathVariable("book_id") int bookId,
            @RequestBody ReviewDTO reviewDTO,
//            @RequestHeader("Authorization") String token) //Authorization 헤더로 전달된 유저 토큰, 테스트이후에 아래랑 바꿀것.
            @RequestHeader(value = "Authorization", required = false) String token
            ){

        //토큰 검증.
        boolean isAuthorized = reviewService.validateToken(token);
        //권한이 없는 경우
        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 토큰에서 userId 추출
//        Integer userId = reviewService.extractUserIdFromToken(token);
        Integer userId = 1; //테스트용(나중에 위에랑 바꿀것)

        reviewDTO.setUserId(userId);
        reviewDTO.setBookId(bookId);
        Review createReview = reviewService.createReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createReview);
    }


    //////////////////////////////조회
    //////////////////////////////1. 책에 대한 전체 리뷰 목록 조회
    @GetMapping("/{book_id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable("book_id") int bookId) {

        List<ReviewDTO> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }


    //////////////////////////////2. 책에 대한 특정 리뷰 조회
    @GetMapping("/{book_id}/reviews/{review_id}")
    public ResponseEntity<ReviewDTO> getReviewById(
            @PathVariable("book_id") int bookId,
            @PathVariable("review_id") Long reviewId)
    {
        ReviewDTO review = reviewService.getReviewsByReviewId(reviewId);
        return ResponseEntity.ok(review);
    }


    //////////////////////////////수정
    @PatchMapping("/{book_id}/reviews/{review_id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable("book_id") int bookId,
            @PathVariable("review_id") Long reviewId,
            @RequestBody ReviewDTO reviewDTO,   //업데이트할 데이터가 담긴 DTO
//            @RequestHeader("Authorization") String token) //Authorization 헤더로 전달된 유저 토큰, 테스트이후에 아래랑 바꿀것.
            @RequestHeader(value = "Authorization", required = false) String token)
    {
        //토큰 검증.
        boolean isAuthorized = reviewService.validateToken(token);
        //권한이 없는 경우
        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 토큰에서 userId 추출
//        Integer userId = reviewService.extractUserIdFromToken(token);
        Integer userId = 1; //테스트용(나중에 위에랑 바꿀것)

        //업데이트 로직 실행
        reviewDTO.setUserId(userId);
        reviewDTO.setBookId(bookId);
        Review updatedReview = reviewService.updateReview(reviewId, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }


    //////////////////////////////삭제
    @DeleteMapping("/{book_id}/reviews/{review_id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("book_id") int bookId,
            @PathVariable("review_id") Long reviewId,
            @RequestBody Map<String, Object> requestBody,
//            @RequestHeader("Authorization") String token) //Authorization 헤더로 전달된 유저 토큰, 테스트이후에 아래랑 바꿀 것.
            @RequestHeader(value = "Authorization", required = false) String token)
    {

        //토큰 검증.
        boolean isAuthorized = reviewService.validateToken(token);
        //권한이 없는 경우
        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 토큰에서 userId 추출
//        Integer userId = reviewService.extractUserIdFromToken(token);

        try {
            //리뷰 삭제 로직 실행
            reviewService.deleteReview(reviewId, bookId);
            return ResponseEntity.noContent().build(); //204 No Content 상태 코드만 반환
        } catch (RuntimeException e) {
            //오류 발생 시 400 Bad Request 상태 코드만 반환
            return ResponseEntity.badRequest().build();
        }
    }


    //////////////////////////////좋아요
    @PatchMapping("/{book_id}/reviews/{review_id}/like")
    public ResponseEntity<Review> toggleLikeReview(
            @PathVariable("book_id") int bookId,
            @PathVariable("review_id") Long reviewId,
//            @RequestHeader(value = "Authorization") String token,
            @RequestBody Map<String, Object> requestBody) {

        // 토큰 검증
//        boolean isAuthorized = reviewService.validateToken(token);
//        if (!isAuthorized) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        // 토큰에서 userId 추출
//        Integer userId = reviewService.extractUserIdFromToken(token);
        //Request Body에서 Integer로 userId 받기(테스트용, 나중에 토큰사용시 위에랑 바꿀 것)
        Integer userId = (Integer) requestBody.get("userId");

        //"좋아요" 로직 실행
        Review updatedReview = reviewService.toggleLike(reviewId, userId, bookId);
        return ResponseEntity.ok(updatedReview);
    }


    //////////////////////////////싫어요
    @PatchMapping("/{book_id}/reviews/{review_id}/dislike")
    public ResponseEntity<Review> toggleDislikeReview(
            @PathVariable("book_id") int bookId,
            @PathVariable("review_id") Long reviewId,
//            @RequestHeader(value = "Authorization") String token,
            @RequestBody Map<String, Object> requestBody) {

        // 토큰 검증
//        boolean isAuthorized = reviewService.validateToken(token);
//        if (!isAuthorized) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        // 토큰에서 userId 추출
//        Integer userId = reviewService.extractUserIdFromToken(token);
        //Request Body에서 Integer로 userId 받기(테스트용, 나중에 토큰사용시 위에랑 바꿀 것)
        Integer userId = (Integer) requestBody.get("userId");

        //"싫어요" 로직 실행
        Review updatedReview = reviewService.toggleDislike(reviewId, userId, bookId);
        return ResponseEntity.ok(updatedReview);
    }

}
