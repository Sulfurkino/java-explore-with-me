package ru.practicum.ewm.comment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.EventState;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event WHERE c.id = :id")
	Optional<Comment> findByIdWithRelations(Long id);

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event "
			+ "WHERE c.id = :id AND c.event.state = :state")
	Optional<Comment> findByIdAndEventStateWithRelations(Long id, EventState state);

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event "
			+ "WHERE c.event.id = :eventId ORDER BY c.created DESC")
	List<Comment> findAllByEventIdWithRelations(Long eventId, Pageable pageable);

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event "
			+ "WHERE c.event.id = :eventId AND c.event.state = :state ORDER BY c.created DESC")
	List<Comment> findAllByEventIdAndEventStateWithRelations(Long eventId, EventState state, Pageable pageable);

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event "
			+ "WHERE c.author.id = :authorId ORDER BY c.created DESC")
	List<Comment> findAllByAuthorIdWithRelations(Long authorId, Pageable pageable);

	@Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.event ORDER BY c.created DESC")
	List<Comment> findAllWithRelations(Pageable pageable);
}
