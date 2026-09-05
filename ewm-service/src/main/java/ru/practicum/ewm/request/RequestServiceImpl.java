package ru.practicum.ewm.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

	private static final String CONDITIONS = "For the requested operation the conditions are not met.";

	private final RequestRepository requestRepository;
	private final UserService userService;
	private final EventService eventService;

	@Override
	@Transactional
	public ParticipationRequestDto addRequest(Long userId, Long eventId) {
		User user = userService.getById(userId);
		Event event = eventService.getEvent(eventId);
		if (event.getInitiator().getId().equals(userId)) {
			throw new ConflictException(CONDITIONS, "Initiator cannot request participation in own event");
		}
		if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
			throw new ConflictException("could not execute statement; SQL [n/a]; constraint [uq_request]; "
					+ "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
		}
		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException(CONDITIONS, "Cannot participate in an unpublished event");
		}
		long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
		if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
			throw new ConflictException(CONDITIONS, "The participant limit has been reached");
		}
		ParticipationRequest request = new ParticipationRequest();
		request.setCreated(LocalDateTime.now());
		request.setEvent(event);
		request.setRequester(user);
		boolean autoConfirm = !event.getRequestModeration() || event.getParticipantLimit() == 0;
		request.setStatus(autoConfirm ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
		return RequestMapper.toDto(requestRepository.save(request));
	}

	@Override
	public List<ParticipationRequestDto> getUserRequests(Long userId) {
		userService.getById(userId);
		return requestRepository.findAllByRequesterId(userId).stream()
				.map(RequestMapper::toDto)
				.toList();
	}

	@Override
	@Transactional
	public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
		userService.getById(userId);
		ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
				.orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
		request.setStatus(RequestStatus.CANCELED);
		return RequestMapper.toDto(requestRepository.save(request));
	}

	@Override
	public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
		userService.getById(userId);
		Event event = eventService.getEvent(eventId);
		if (!event.getInitiator().getId().equals(userId)) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		return requestRepository.findAllByEventId(eventId).stream()
				.map(RequestMapper::toDto)
				.toList();
	}

	@Override
	@Transactional
	public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
			EventRequestStatusUpdateRequest dto) {
		userService.getById(userId);
		Event event = eventService.getEvent(eventId);
		if (!event.getInitiator().getId().equals(userId)) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId, dto.getRequestIds());
		if (requests.size() != dto.getRequestIds().size()) {
			throw new NotFoundException("Request was not found");
		}
		if (dto.getStatus() == RequestStatus.CONFIRMED) {
			long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
			if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
				throw new ConflictException(CONDITIONS, "The participant limit has been reached");
			}
		}
		for (ParticipationRequest request : requests) {
			if (request.getStatus() != RequestStatus.PENDING) {
				throw new ConflictException(CONDITIONS, "Request must have status PENDING");
			}
		}
		List<ParticipationRequestDto> confirmedDtos = new ArrayList<>();
		List<ParticipationRequestDto> rejectedDtos = new ArrayList<>();
		if (dto.getStatus() == RequestStatus.REJECTED) {
			for (ParticipationRequest request : requests) {
				request.setStatus(RequestStatus.REJECTED);
				rejectedDtos.add(RequestMapper.toDto(request));
			}
			requestRepository.saveAll(requests);
			return new EventRequestStatusUpdateResult(confirmedDtos, rejectedDtos);
		}
		if (dto.getStatus() != RequestStatus.CONFIRMED) {
			throw new IllegalArgumentException("Request status must be CONFIRMED or REJECTED");
		}
		long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
		for (ParticipationRequest request : requests) {
			if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
				request.setStatus(RequestStatus.REJECTED);
				rejectedDtos.add(RequestMapper.toDto(request));
			} else {
				request.setStatus(RequestStatus.CONFIRMED);
				confirmed++;
				confirmedDtos.add(RequestMapper.toDto(request));
			}
		}
		requestRepository.saveAll(requests);
		if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
			List<ParticipationRequest> pending = requestRepository.findAllByEventIdAndStatus(eventId,
					RequestStatus.PENDING);
			for (ParticipationRequest request : pending) {
				request.setStatus(RequestStatus.REJECTED);
				rejectedDtos.add(RequestMapper.toDto(request));
			}
			requestRepository.saveAll(pending);
		}
		return new EventRequestStatusUpdateResult(confirmedDtos, rejectedDtos);
	}
}
