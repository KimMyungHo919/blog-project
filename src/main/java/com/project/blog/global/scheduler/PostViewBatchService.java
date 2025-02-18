package com.project.blog.global.scheduler;

import com.project.blog.domain.postview.entity.PostView;
import com.project.blog.domain.postview.repository.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostViewBatchService {

    private final PostViewRepository postViewRepository;

    // 배치사이즈
    private static final int BATCH_SIZE = 1000;

    // 자정마다 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldPostViews() {
        boolean hasMoreData = true;

        while (hasMoreData) {
            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            List<PostView> oldPosts = postViewRepository.findBeforeDate(LocalDateTime.now(), pageable);

            if (oldPosts.isEmpty()) {
                hasMoreData = false;
            } else {
                postViewRepository.deleteAllInBatch(oldPosts);
            }

            if (hasMoreData) {
                try {
                    Thread.sleep(500);  // 0.5초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
