package ru.practicum.ewm.event;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.request.ParticipationRequest;
import ru.practicum.ewm.request.RequestStatus;

public final class EventSpecifications {

	private EventSpecifications() {
	}

	public static Specification<Event> publicSearch(
			String text,
			List<Long> categories,
			Boolean paid,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			boolean onlyAvailable) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));
			if (text != null && !text.isBlank()) {
				String pattern = "%" + text.toLowerCase() + "%";
				predicates.add(cb.or(
						cb.like(cb.lower(root.get("annotation")), pattern),
						cb.like(cb.lower(root.get("description")), pattern)));
			}
			if (categories != null && !categories.isEmpty()) {
				predicates.add(root.get("category").get("id").in(categories));
			}
			if (paid != null) {
				predicates.add(cb.equal(root.get("paid"), paid));
			}
			if (rangeStart != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
			}
			if (rangeEnd != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
			}
			if (onlyAvailable) {
				Subquery<Long> subquery = query.subquery(Long.class);
				Root<ParticipationRequest> requestRoot = subquery.from(ParticipationRequest.class);
				subquery.select(cb.count(requestRoot));
				subquery.where(
						cb.equal(requestRoot.get("event"), root),
						cb.equal(requestRoot.get("status"), RequestStatus.CONFIRMED));
				predicates.add(cb.or(
						cb.equal(root.get("participantLimit"), 0),
						cb.greaterThan(root.get("participantLimit").as(Long.class), subquery)));
			}
			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}

	public static Specification<Event> adminSearch(
			List<Long> users,
			List<EventState> states,
			List<Long> categories,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (users != null && !users.isEmpty()) {
				predicates.add(root.get("initiator").get("id").in(users));
			}
			if (states != null && !states.isEmpty()) {
				predicates.add(root.get("state").in(states));
			}
			if (categories != null && !categories.isEmpty()) {
				predicates.add(root.get("category").get("id").in(categories));
			}
			if (rangeStart != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
			}
			if (rangeEnd != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
			}
			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
