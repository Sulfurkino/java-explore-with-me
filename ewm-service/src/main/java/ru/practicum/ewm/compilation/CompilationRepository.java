package ru.practicum.ewm.compilation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

	List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

	@Query("SELECT DISTINCT c FROM Compilation c LEFT JOIN FETCH c.events e "
			+ "LEFT JOIN FETCH e.category LEFT JOIN FETCH e.initiator WHERE c.id = :id")
	Optional<Compilation> findByIdWithEvents(Long id);

	@Query("SELECT DISTINCT c FROM Compilation c LEFT JOIN FETCH c.events e "
			+ "LEFT JOIN FETCH e.category LEFT JOIN FETCH e.initiator WHERE c.id IN :ids")
	List<Compilation> findAllWithEventsByIdIn(Collection<Long> ids);
}
