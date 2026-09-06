package ru.practicum.ewm;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest implements Pageable {

	private final int offset;
	private final int size;
	private final Sort sort;

	public OffsetPageRequest(int offset, int size) {
		this(offset, size, Sort.unsorted());
	}

	public OffsetPageRequest(int offset, int size, Sort sort) {
		if (offset < 0) {
			throw new IllegalArgumentException("offset must not be negative");
		}
		if (size < 1) {
			throw new IllegalArgumentException("size must be positive");
		}
		this.offset = offset;
		this.size = size;
		this.sort = sort;
	}

	@Override
	public int getPageNumber() {
		return offset / size;
	}

	@Override
	public int getPageSize() {
		return size;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		return new OffsetPageRequest(offset + size, size, sort);
	}

	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? new OffsetPageRequest(Math.max(0, offset - size), size, sort) : first();
	}

	@Override
	public Pageable first() {
		return new OffsetPageRequest(0, size, sort);
	}

	@Override
	public Pageable withPage(int pageNumber) {
		return new OffsetPageRequest(pageNumber * size, size, sort);
	}

	@Override
	public boolean hasPrevious() {
		return offset > 0;
	}
}
