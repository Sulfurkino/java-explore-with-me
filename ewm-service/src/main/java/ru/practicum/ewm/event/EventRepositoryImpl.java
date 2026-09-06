package ru.practicum.ewm.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class EventRepositoryImpl implements EventRepositoryCustom {

	private final EntityManager entityManager;

	public EventRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Long> findIds(Specification<Event> spec) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Event> root = query.from(Event.class);
		query.select(root.get("id"));
		if (spec != null) {
			Predicate predicate = spec.toPredicate(root, query, cb);
			if (predicate != null) {
				query.where(predicate);
			}
		}
		return entityManager.createQuery(query).getResultList();
	}
}
