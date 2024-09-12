package com.groom.cloudlibrarian.review.service;

import com.groom.cloudlibrarian.review.dto.ReviewDTO;
import com.groom.cloudlibrarian.review.entity.Review;
import com.groom.cloudlibrarian.review.entity.ReviewLike;
import com.groom.cloudlibrarian.review.repository.ReviewLikeRepository;
import com.groom.cloudlibrarian.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewLikeRepository reviewLikeRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
    }


    //////////////////////////////작성
    public Review createReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setBookId(reviewDTO.getBookId());
        review.setNickName(reviewDTO.getNickName());
        review.setWriteDate(reviewDTO.getWriteDate());
        review.setStarRating(reviewDTO.getStarRating());
        review.setReview(reviewDTO.getReview());

        review.setUserId(reviewDTO.getUserId());

        return reviewRepository.save(review);
    }


    //////////////////////////////조회
    //////////////////////////////1. 책에 대한 전체 리뷰 목록 조회
    public List<ReviewDTO> getReviewsByBookId(int bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        //리뷰가 없으면 예외 던지기
        if (reviews.isEmpty()) {
            throw new RuntimeException("책 \"" + bookId + "\"에 대한 리뷰가 없습니다.");
        }
        return reviews.stream().map(review -> new ReviewDTO(
                review.getReviewId(),
                review.getUserId(),
                review.getBookId(),
                review.getNickName(),
                review.getWriteDate(),
                review.getStarRating(),
                review.getReview(),
                review.getLikes(),
                review.getDislikes())).collect(Collectors.toList());
    }


    //////////////////////////////2. 책에 대한 특정 리뷰 조회
    public ReviewDTO getReviewsByReviewId(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        return optionalReview.map(review -> new ReviewDTO(
                review.getReviewId(),
                review.getBookId(),
                review.getUserId(),
                review.getNickName(),
                review.getWriteDate(),
                review.getStarRating(),
                review.getReview(),
                review.getLikes(),
                review.getDislikes()
        )).orElseThrow(()-> new RuntimeException("리뷰를 찾을 수 없습니다."));    //리뷰가 없으면.
    }


    //////////////////////////////수정,삭제시 필요한 토큰 검증.
    //토큰 검증 로직(유효한 토큰인지 확인하는 메서드)
    public boolean validateToken(String token) {
        return true;    //일단 true로 놓고 test
    }

    //토큰관련...모르겠음.
    //토큰에서 userId 추출 메서드
//    public Integer extractUserIdFromToken(String token) {
//        if (token == null || token.isEmpty()) {
//            return null;
//        }
//        //토큰에서 userId 추출(아래 디코딩 로직 필요)
//        return decodeUserIdFromToken(token);
//    }
//
//    private Integer decodeUserIdFromToken(String token) {
//        //실제 토큰에서 userId를 추출하는 로직.
//        return 1; // 테스트용 userId
//    }


    //////////////////////////////수정
    public Review updateReview(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new RuntimeException("리뷰ID " + reviewId + "를 찾을 수 없습니다."));

        //DTO의 값으로 엔티티의 값 업데이트
//        if (reviewDTO.getBookId() != 0) {
//            review.setBookId(reviewDTO.getBookId());
//        }
        if (reviewDTO.getNickName() != null) {
            review.setNickName(reviewDTO.getNickName());
        }
        if (reviewDTO.getWriteDate() != null) {
            review.setWriteDate(reviewDTO.getWriteDate());
        }
        if (reviewDTO.getStarRating() != 0) {
            review.setStarRating(reviewDTO.getStarRating());
        }
        if (reviewDTO.getReview() != null) {
            review.setReview(reviewDTO.getReview());
        }
        return reviewRepository.save(review);
    }


    //////////////////////////////삭제
    public void deleteReview(Long reviewId, int bookId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new RuntimeException("리뷰ID " + reviewId + "를 찾을 수 없습니다."));

        reviewRepository.delete(review);
    }


    //////////////////////////////좋아요
    public Review toggleLike(Long reviewId, Integer userId, Integer bookId) {
        //1.reviewId를 사용해 리뷰를 가져옴.
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new RuntimeException("리뷰ID " + reviewId + "를 찾을 수 없습니다."));

        //2.좋아요 상태 확인.
        Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByUserIdAndReview_ReviewId(userId, reviewId);

        if (reviewLikeOptional.isPresent()) {
            //좋아요,싫어요 누른 경우
            ReviewLike reviewLike = reviewLikeOptional.get();
            if (reviewLike.isLiked()) {
                //이미 좋아요를 눌렀다면, 좋아요 취소
                review.setLikes(review.getLikes() - 1); //리뷰의 좋아요 수 감소
                reviewLikeRepository.delete((reviewLike));  //리뷰의 좋아요 기록 삭제
            } else {
                //싫어요를 좋아요로 변경
                review.setDislikes(review.getDislikes() - 1); //리뷰의 싫어요 수 감소
                review.setLikes(review.getLikes() + 1); //리뷰의 좋아요 수 증가
                reviewLike.setLiked(true);  //좋아요 상태로 변경
                reviewLikeRepository.save(reviewLike);  //변경사항저장
            }
        } else {
            //처음 좋아요를 누른 경우
            ReviewLike newReviewLike = new ReviewLike();
            newReviewLike.setReview(review);    //reviewId설정
            newReviewLike.setUserId(userId);    //userId설정
            newReviewLike.setLiked(true);   //좋아요 상태로 변경
            review.setLikes(review.getLikes() + 1);  //좋아요 수 증가
            reviewLikeRepository.save(newReviewLike);   //새로운 좋아요기록 저장
        }

        return reviewRepository.save(review);   //변경된 리뷰 저장후 반환
    }

    //////////////////////////////싫어요
    public Review toggleDislike(Long reviewId, Integer userId, Integer bookId) {
        //1.reviewId를 사용해 리뷰를 가져옴.
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new RuntimeException("리뷰ID " + reviewId + "를 찾을 수 없습니다."));

        //2.좋아요 상태 확인.
        Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByUserIdAndReview_ReviewId(userId, reviewId);

        if (reviewLikeOptional.isPresent()) {
            //좋아요,싫어요 누른 경우
            ReviewLike reviewLike = reviewLikeOptional.get();
            if (!reviewLike.isLiked()) {
                //이미 싫어요를 눌렀다면, 싫어요 취소
                review.setDislikes(review.getDislikes() - 1); //리뷰의 좋아요 수 감소
                reviewLikeRepository.delete((reviewLike));  //리뷰의 싫어요 기록 삭제
            } else {
                //좋아요를 싫어요로 변경
                review.setLikes(review.getLikes() - 1); //리뷰의 좋아요 수 감소
                review.setDislikes(review.getDislikes() + 1); //리뷰의 싫어요 수 증가
                reviewLike.setLiked(false);  //싫어요 상태로 변경
                reviewLikeRepository.save(reviewLike);  //변경사항저장
            }
        } else {
            //처음 좋아요를 누른 경우
            ReviewLike newReviewLike = new ReviewLike();
            newReviewLike.setReview(review);    //reviewId설정
            newReviewLike.setUserId(userId);    //userId설정
            newReviewLike.setLiked(false);   //싫어요 상태로 변경
            review.setDislikes(review.getDislikes() + 1);  //싫어요 수 증가
            reviewLikeRepository.save(newReviewLike);   //새로운 싫어요기록 저장
        }

        return reviewRepository.save(review);   //변경된 리뷰 저장후 반환
    }

}
