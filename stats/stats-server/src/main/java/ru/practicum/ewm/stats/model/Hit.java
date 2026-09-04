package ru.practicum.ewm.stats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hits")
@Getter
@Setter
@NoArgsConstructor
public class Hit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String app;

	@Column(nullable = false)
	private String uri;

	@Column(nullable = false)
	private String ip;

	@Column(name = "created", nullable = false)
	private LocalDateTime timestamp;
}
