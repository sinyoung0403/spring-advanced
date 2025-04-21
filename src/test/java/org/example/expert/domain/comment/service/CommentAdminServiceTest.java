package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    @DisplayName("어드민의 댓글 삭제가 정상적으로 삭제된다")
    void deleteComment_successfully() {
        // given
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        doNothing().when(commentRepository).deleteById(commentId);

        // when
        commentAdminService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
        verify(commentRepository).existsById(commentId);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    @DisplayName("댓글 식별자가 존재하지 않아 댓글 삭제가 실패된다")
    void deleteComment_failsWhenCommentDoesNotExist() {
        // given
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        // when + then
        assertThatThrownBy(() -> commentAdminService.deleteComment(commentId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Comment not found");
        verify(commentRepository, never()).deleteById(anyLong());
    }
}