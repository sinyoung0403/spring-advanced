package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;

    // 리펙토링 3) commentId 가 없을 경우 오류 반환
    @Transactional
    public void deleteComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new InvalidRequestException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}
