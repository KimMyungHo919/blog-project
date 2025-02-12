package com.project.blog.global.scheduler;


import com.project.blog.domain.s3.entity.S3Image;
import com.project.blog.domain.s3.repository.S3ImageRepository;
import com.project.blog.domain.s3.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3ImageDeleteScheduler {

    private final S3ImageRepository s3ImageRepository;
    private final S3ImageService s3ImageService;

    @Scheduled(cron = "0 0 0 * * ?") // 자정마다 실행
    public void performScheduledTask() {
        // 이미지 list 로 다 가져오기
        List<S3Image> byPostIdIsNull = s3ImageRepository.findByPostIdIsNull();

        // 반복문 돌리면서 삭제
        for (S3Image image : byPostIdIsNull) {
            try {
                log.info("S3Image 삭제 성공 : {}", image.getImgUrl());
                s3ImageService.deleteImageFromS3(image.getId());
            } catch (Exception e) {
                log.warn("S3Image 삭제 실패 : {}", image.getImgUrl());
            }
        }
    }


}
