package org.openldes.server.compaction.batch;

import java.time.LocalDateTime;
import org.openldes.server.compaction.domain.repository.CompactionPageRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class PageDeletionTask implements Tasklet {
	private final CompactionPageRepository pageRepository;

	public PageDeletionTask(CompactionPageRepository pageRepository) {
		this.pageRepository = pageRepository;
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		pageRepository.deleteOutdatedFragments(LocalDateTime.now());
		return RepeatStatus.FINISHED;
	}
}
