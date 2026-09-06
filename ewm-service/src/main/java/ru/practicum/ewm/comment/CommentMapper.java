package ru.practicum.ewm.comment;

import ru.practicum.ewm.user.UserMapper;

public final class CommentMapper {

	private CommentMapper() {
	}

	public static CommentDto toDto(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setEventId(comment.getEvent().getId());
		dto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
		dto.setCreated(comment.getCreated());
		dto.setUpdated(comment.getUpdated());
		return dto;
	}
}
