package org.openldes.server.compaction.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openldes.server.compaction.domain.repository.CompactionPageRepository;

@ExtendWith(MockitoExtension.class)
class PageDeletionTaskTest {
	@Mock
	private CompactionPageRepository pageRepository;
	@InjectMocks
	private PageDeletionTask pageDeletionTask;

	@Test
	void when_FragmentHasDeleteTimeEarlierThanCurrentTime_then_ItIsDeletedAndEventIsSent() {
		pageDeletionTask.execute(null, null);

		verify(pageRepository).deleteOutdatedFragments(any());
	}
}
