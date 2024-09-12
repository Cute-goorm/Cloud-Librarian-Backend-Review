package com.groom.cloudlibrarian.review.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    //userId 필드 추가 (?)
    private Integer userId;

    private int bookId;
    private String nickName;
    private LocalDateTime writeDate;
    private double starRating;
    private String review;


    //좋아요,싫어요 (리뷰생성시 기본값=0)
    private int likes = 0;
    private int dislikes = 0;

    //리뷰랑 좋아요,싫어요는 일대다관계
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //JSON직렬화관리를 위한 어노테이션
    private List<ReviewLike> reviewLikes = new ArrayList<>();  //여러 ReviewLike와의 관계

}
