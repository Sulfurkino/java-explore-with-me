package ru.practicum.ewm.comment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicCommentController {

	private final CommentService commentService;

	@GetMapping("/events/{eventId}/comments")
	public List<CommentDto> getByEvent(
			@PathVariable Long eventId,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return commentService.getByEvent(eventId, from, size);
	}

	@GetMapping("/comments/{commentId}")
	public CommentDto getById(@PathVariable Long commentId) {
		return commentService.getPublished(commentId);
	}
}
