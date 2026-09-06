package ru.practicum.ewm.comment;

import java.util.List;

public interface CommentService {

	CommentDto add(Long userId, NewCommentDto dto);

	CommentDto update(Long userId, Long commentId, UpdateCommentRequest dto);

	void deleteByAuthor(Long userId, Long commentId);

	List<CommentDto> getByAuthor(Long userId, int from, int size);

	List<CommentDto> getByEvent(Long eventId, int from, int size);

	CommentDto getPublished(Long commentId);

	void deleteByAdmin(Long commentId);

	List<CommentDto> getAdminComments(Long eventId, int from, int size);
}
