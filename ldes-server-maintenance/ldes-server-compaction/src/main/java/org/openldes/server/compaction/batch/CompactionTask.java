package org.openldes.server.compaction.batch;

import java.util.List;
import org.openldes.server.compaction.application.services.CompactionCandidateSorter;
import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.openldes.server.compaction.domain.repository.CompactionPageRepository;
import org.openldes.server.domain.model.ViewName;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class CompactionTask implements Tasklet {
	private final CompactionPageRepository pageRepository;
	private final org.openldes.server.compaction.application.services.CompactionCandidateSorter compactionCandidateSorter;
	private final CompactionWriter compactionWriter;

	public CompactionTask(CompactionPageRepository pageRepository, CompactionCandidateSorter compactionCandidateSorter, CompactionWriter compactionWriter) {
		this.pageRepository = pageRepository;
		this.compactionCandidateSorter = compactionCandidateSorter;
		this.compactionWriter = compactionWriter;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		final ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		final ViewName viewName = ViewName.fromString(executionContext.getString("viewName"));
		final int capacityPerPage = executionContext.getInt("capacityPerPage");

		final List<CompactionCandidate> compactionCandidates = pageRepository.getPossibleCompactionCandidates(viewName, capacityPerPage);


		compactionCandidateSorter
				.getSortedCompactionCandidates(compactionCandidates, capacityPerPage)
				.forEach(compactionWriter::write);

		return RepeatStatus.FINISHED;
	}
}
