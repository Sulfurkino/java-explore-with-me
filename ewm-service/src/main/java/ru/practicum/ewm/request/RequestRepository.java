package ru.practicum.ewm.request;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

	long countByEventIdAndStatus(Long eventId, RequestStatus status);

	boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

	@Query("SELECT r FROM ParticipationRequest r JOIN FETCH r.event JOIN FETCH r.requester WHERE r.event.id = :eventId")
	List<ParticipationRequest> findAllByEventId(Long eventId);

	@Query("SELECT r FROM ParticipationRequest r JOIN FETCH r.event JOIN FETCH r.requester WHERE r.requester.id = :requesterId")
	List<ParticipationRequest> findAllByRequesterId(Long requesterId);

	@Query("SELECT r FROM ParticipationRequest r JOIN FETCH r.event JOIN FETCH r.requester "
			+ "WHERE r.event.id = :eventId AND r.id IN :ids")
	List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, List<Long> ids);

	List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

	@Query("SELECT r FROM ParticipationRequest r JOIN FETCH r.event JOIN FETCH r.requester "
			+ "WHERE r.id = :id AND r.requester.id = :requesterId")
	Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);

	@Query("SELECT r.event.id, COUNT(r) FROM ParticipationRequest r "
			+ "WHERE r.status = :status AND r.event.id IN :eventIds GROUP BY r.event.id")
	List<Object[]> countByEventIdInAndStatus(Collection<Long> eventIds, RequestStatus status);
}
