package ru.practicum.ewm.comment;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

	private final CommentService commentService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommentDto add(@PathVariable Long userId, @RequestBody @Valid NewCommentDto dto) {
		return commentService.add(userId, dto);
	}

	@GetMapping
	public List<CommentDto> getByAuthor(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return commentService.getByAuthor(userId, from, size);
	}

	@PatchMapping("/{commentId}")
	public CommentDto update(
			@PathVariable Long userId,
			@PathVariable Long commentId,
			@RequestBody @Valid UpdateCommentRequest dto) {
		return commentService.update(userId, commentId, dto);
	}

	@DeleteMapping("/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long userId, @PathVariable Long commentId) {
		commentService.deleteByAuthor(userId, commentId);
	}
}
