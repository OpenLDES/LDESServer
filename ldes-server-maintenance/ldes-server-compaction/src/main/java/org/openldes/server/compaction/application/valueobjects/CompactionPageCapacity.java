package org.openldes.server.compaction.application.valueobjects;

public class CompactionPageCapacity {
	private final int maxCapacity;
	private int currentCapacity;

	public CompactionPageCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public void reset() {
		currentCapacity = 0;
	}

	/**
	 * Whether a candidate of the given size still fits into the page that is being filled.
	 */
	public boolean hasRoomFor(int size) {
		return currentCapacity + size <= maxCapacity;
	}

	public void increase(int delta) {
		currentCapacity += delta;
	}
}
