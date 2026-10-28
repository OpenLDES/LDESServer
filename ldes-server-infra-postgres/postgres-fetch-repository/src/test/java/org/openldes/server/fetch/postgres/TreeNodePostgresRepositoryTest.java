package org.openldes.server.fetch.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.fetch.postgres.projection.TreeNodeProjection;
import org.openldes.server.fetch.postgres.repository.FetchPageEntityRepository;
import org.openldes.server.fetch.postgres.repository.FetchPageMemberEntityRepository;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;

class TreeNodePostgresRepositoryTest {

    private static final String STORED = "/events/by-time?year=2026&month=03&day=21&hour=15&pageNumber=1";
    private static final String REORDERED = "/events/by-time?pageNumber=1&hour=15&day=21&month=03&year=2026";

    private FetchPageEntityRepository pages;
    private TreeNodePostgresRepository repository;

    @BeforeEach
    void setUp() {
        pages = mock(FetchPageEntityRepository.class);
        repository = new TreeNodePostgresRepository(pages, mock(FetchPageMemberEntityRepository.class));
    }

    @Test
    void exactPageLookupDoesNotRequireCanonicalLookup() {
        TreeNodeProjection page = mock(TreeNodeProjection.class);
        when(pages.findTreeNodeProjectionByPartialUrl(STORED)).thenReturn(Optional.of(page));

        assertThat(repository.findProjection(identifier(STORED))).contains(page);
        verify(pages).findTreeNodeProjectionByPartialUrl(STORED);
        verifyNoMoreInteractions(pages);
    }

    @Test
    void missingRootPageDoesNotRequireCanonicalLookup() {
        assertThat(repository.findProjection(identifier("/events/by-time"))).isEmpty();
        verify(pages).findTreeNodeProjectionByPartialUrl("/events/by-time");
        verifyNoMoreInteractions(pages);
    }

    @Test
    void reorderedParametersResolveToTheStoredUrl() {
        TreeNodeProjection page = mock(TreeNodeProjection.class);
        when(pages.findPartialUrlsWithEquivalentParameters(REORDERED)).thenReturn(List.of(STORED));
        when(pages.findTreeNodeProjectionByPartialUrl(STORED)).thenReturn(Optional.of(page));

        assertThat(repository.findProjection(identifier(REORDERED))).contains(page);
        verify(pages).findTreeNodeProjectionByPartialUrl(REORDERED);
        verify(pages).findPartialUrlsWithEquivalentParameters(REORDERED);
        verify(pages).findTreeNodeProjectionByPartialUrl(STORED);
    }

    @Test
    void unknownPageIsStillMissing() {
        when(pages.findPartialUrlsWithEquivalentParameters(REORDERED)).thenReturn(List.of());

        assertThat(repository.findProjection(identifier(REORDERED))).isEmpty();
    }

    @Test
    void ambiguousStoredPagesAreNotChosenArbitrarily() {
        when(pages.findPartialUrlsWithEquivalentParameters(REORDERED))
                .thenReturn(List.of(STORED,
                        "/events/by-time?month=03&year=2026&hour=15&day=21&pageNumber=1"));

        assertThatThrownBy(() -> repository.findProjection(identifier(REORDERED)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Multiple pages have equivalent fragment parameters");
    }

    private static LdesFragmentIdentifier identifier(String url) {
        return LdesFragmentIdentifier.fromFragmentId(url);
    }
}
