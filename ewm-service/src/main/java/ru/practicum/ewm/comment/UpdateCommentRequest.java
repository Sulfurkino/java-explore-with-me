package ru.practicum.ewm.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentRequest {

	@NotBlank
	@Size(min = 1, max = 2000)
	private String text;
}
