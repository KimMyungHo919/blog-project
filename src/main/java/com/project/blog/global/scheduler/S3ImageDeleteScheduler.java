package com.project.blog.global.scheduler;


import com.project.blog.domain.image.entity.Image;
import com.project.blog.domain.image.repository.ImageRepository;
import com.project.blog.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3ImageDeleteScheduler {

    private final ImageRepository imageRepository;
    private final ImageService imageService;

    @Scheduled(cron = "0 0 0 * * ?") // 자정마다 실행
    public void performScheduledTask() {
        // 이미지 list 로 다 가져오기
        List<Image> byImageTypeIsNull = imageRepository.findByImageTypeIsNull();

        // 반복문 돌리면서 삭제
        for (Image image : byImageTypeIsNull) {
            try {
                log.info("S3Image 삭제 성공 : {}", image.getImgUrl());
                imageService.deleteImageFromS3(image.getImgUrl());
            } catch (Exception e) {
                log.warn("S3Image 삭제 실패 : {}", image.getImgUrl());
            }
        }
    }


}
