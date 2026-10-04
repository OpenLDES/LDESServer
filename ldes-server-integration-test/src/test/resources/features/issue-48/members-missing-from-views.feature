Feature: Every view of a collection receives every ingested member

  # Reproduction of https://github.com/OpenLDES/LDESServer/issues/48
  # "Ingested members are not found in views".
  #
  # A member is only offered to the bucketisation step of a view for as long as
  # `members.is_fragmented` is false (see the `processable_members` database view). That flag
  # however is kept per collection, not per view, and the Paginator flips it to true as soon as
  # the *first* view has paginated the member. Any other view of the same collection that had not
  # read the member yet will therefore never see it again, which silently drops members from that
  # view forever.

  @issue-48 @concurrent-ingestion
  Scenario: Members ingested while fragmentation is already running end up in every view
    Given I create the eventstream "data/input/eventstreams/fragmentation/issue-48/multi-view.ttl"
    When I concurrently ingest 600 members of template "data/input/members/mob-hind.template.ttl" to the collection "concurrently-ingested-hindrances" using 8 threads
    And the LDES "concurrently-ingested-hindrances" contains 600 members
    And fragmentation of "concurrently-ingested-hindrances" has settled
    Then every view of "concurrently-ingested-hindrances" contains 600 members
    And traversing the "paged" view of "concurrently-ingested-hindrances" yields 600 distinct members

  @issue-48 @late-added-view
  Scenario: A view added to an already fragmented collection still receives every member
    Given I create the eventstream "data/input/eventstreams/fragmentation/issue-48/single-view.ttl"
    When I ingest 20 members of template "data/input/members/mob-hind.template.ttl" to the collection "late-view-hindrances"
    And the LDES "late-view-hindrances" contains 20 members
    And fragmentation of "late-view-hindrances" has settled
    Then I create the view "data/input/eventstreams/fragmentation/issue-48/late-added-view.paged.ttl"
    And fragmentation of "late-view-hindrances" has settled
    And every view of "late-view-hindrances" contains 20 members
