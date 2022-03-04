package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "item_img")
@Entity
public class ItemImg extends BaseEntity {

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 상품 엔티티 정보가 필요할 경우에 데이터를 조회하도록 지연로딩 설정
    @JoinColumn(name = "item_id")
    private Item item;

    private String imgName; // 이미지 파일명

    private String oriImgName; // 원본 이미지 파일명

    private String imgUrl; // 이미지 조회 경로

    private String repImgYn; // 대표 이미지 여부

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
