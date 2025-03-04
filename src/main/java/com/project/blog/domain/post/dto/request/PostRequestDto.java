package com.project.blog.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 5, max = 50, message = "제목은 5자 이상 50자 이하로 입력해야 합니다.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 10, message = "내용은 최소 10글자 이상 입력해야 합니다.")
    private final String content;

    @NotBlank(message = "포스팅 공개 여부를 입력해주세요.")
    @Pattern(regexp = "공개|비공개|임시저장", message = "포스팅 공개 여부는 '공개','비공개','임시저장' 만 입력 가능합니다.")
    private final String postVisibility;

    @NotBlank(message = "포스팅 카테고리를 입력해주세요.")
    @Pattern(regexp = "일상|여행|음식|패션|건강|엔터테인먼트|영화|음악|게임|책|스포츠|문화|사회|비지니스", message = "포스팅 카테고리는 일상|여행|음식|패션|건강|엔터테인먼트|영화|음악|게임|책|스포츠|문화|사회|비지니스 만 입력 가능합니다.")
    private final String postCategory;

    public PostRequestDto(String title, String content, String postVisibility, String postCategory) {
        this.title = title;
        this.content = content;
        this.postVisibility = postVisibility;
        this.postCategory = postCategory;
    }
}
