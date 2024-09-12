package com.groom.cloudlibrarian.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private Long reviewId;

    //userId 필드 추가 (?)
    private Integer userId;

    private int bookId;
    private String nickName;
    private LocalDateTime writeDate;
    private double starRating;
    private String review;

    private int likes = 0;
    private int dislikes = 0;

    //기본생성자
//    public ReviewDTO() {}

    //생성자
    public ReviewDTO(Long reviewId, Integer userId, int bookId, String nickName, LocalDateTime writeDate, double starRating, String review, int likes, int dislikes) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.bookId = bookId;
        this.nickName = nickName;
        this.writeDate = writeDate;
        this.starRating = starRating;
        this.review = review;
        this.likes = likes;
        this.dislikes = dislikes;
    }
}
