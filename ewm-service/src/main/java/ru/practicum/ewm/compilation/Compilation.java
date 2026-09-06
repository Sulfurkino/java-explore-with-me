package ru.practicum.ewm.compilation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.Event;

@Getter
@Setter
@Entity
@Table(name = "compilations")
public class Compilation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Boolean pinned;

	@Column(nullable = false, unique = true)
	private String title;

	@ManyToMany
	@JoinTable(name = "compilation_events",
			joinColumns = @JoinColumn(name = "compilation_id"),
			inverseJoinColumns = @JoinColumn(name = "event_id"))
	private Set<Event> events = new HashSet<>();
}
