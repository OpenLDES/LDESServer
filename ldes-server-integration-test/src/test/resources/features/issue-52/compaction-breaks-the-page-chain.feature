Feature: Compaction keeps every page of a view reachable from its root

  # Regression test for https://github.com/OpenLDES/LDESServer/issues/52
  # "Compaction groups non-adjacent pages and rewires the relations into a cycle".
  #
  # A version based retention policy thins out every page of a view: the pages end up below the
  # page size, but each of them still holds more than half of it, so no two neighbouring pages fit
  # into a single compacted page. The sorter used to drop a page that does not fit instead of
  # starting a new run with it, and it never closed a page that holds a single candidate, so it
  # ended up handing pages 1 and 3 to the writer as if they were adjacent. The writer then pointed
  # the compacted page at the successor of an arbitrary one of them, which left the pages in
  # between either orphaned or in a cycle: the members behind them could no longer be reached by a
  # client that follows the relations from the root page.

  @issue-52
  Scenario: Pages that a retention policy left below capacity are compacted without breaking the chain
    Given I create the eventstream "data/input/eventstreams/compaction/issue-52/paged-with-version-retention.ttl"
    # Four pages of four members: 0-3, 4-7, 8-11 and 12-15.
    When I ingest a version of the state objects "0-15" of template "data/input/members/issue-52/person-state.template.ttl" to the collection "chained-observations"
    And fragmentation of "chained-observations" has settled
    # A second version of one state object per page, so retention leaves every page with three of
    # the four members it can hold.
    And I ingest a version of the state objects "0,4,8,12" of template "data/input/members/issue-52/person-state.template.ttl" to the collection "chained-observations"
    And fragmentation of "chained-observations" has settled
    Then retention leaves the "paged" view of "chained-observations" with 16 members
    When the maintenance job has run
    Then every page of the "paged" view of "chained-observations" that holds members is reachable from its root page
    And the page chain of the "paged" view of "chained-observations" does not loop back on itself
    And traversing the "paged" view of "chained-observations" yields 16 distinct members
    And the background processes did not fail
