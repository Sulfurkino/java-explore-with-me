package ru.practicum.ewm.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>,
		EventRepositoryCustom {

	boolean existsByCategoryId(Long categoryId);

	@EntityGraph(attributePaths = {"category", "initiator"})
	List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

	@Query("SELECT e FROM Event e JOIN FETCH e.category JOIN FETCH e.initiator WHERE e.id = :id")
	Optional<Event> findByIdWithRelations(Long id);

	@Query("SELECT e FROM Event e JOIN FETCH e.category JOIN FETCH e.initiator "
			+ "WHERE e.id = :id AND e.initiator.id = :userId")
	Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

	@Query("SELECT DISTINCT e FROM Event e JOIN FETCH e.category JOIN FETCH e.initiator WHERE e.id IN :ids")
	List<Event> findAllWithRelationsByIdIn(Collection<Long> ids);
}
