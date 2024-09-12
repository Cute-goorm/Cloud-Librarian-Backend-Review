package com.groom.cloudlibrarian.review.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //likeId

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)   //외래키
    @JsonBackReference  //JSON직렬화관리를 위한 어노테이션
    private Review review;  //like와 review는 다대일관계

    private Integer userId;
    private boolean liked;  //ture:종아요, false:싫어요

}
