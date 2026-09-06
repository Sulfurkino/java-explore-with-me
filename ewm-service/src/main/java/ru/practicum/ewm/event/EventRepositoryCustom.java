package ru.practicum.ewm.event;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface EventRepositoryCustom {

	List<Long> findIds(Specification<Event> spec);
}
