Feature: LDES Server Retention

  @time-based
  Scenario Outline: Server provides timebased retention
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the first fragment of the "paged" view in collection <collection> contains <ingestedMemberCount> members
    # Since all added members' timestamp values equal to their ingestion date, they should be removed after 15 seconds
    Then the first fragment of the "paged" view in collection <collection> contains 0 members
    And the background processes did not fail
    And the batch tables has been cleaned
    Examples:
      | eventStreamDescriptionFile                                            | template                                           | collection            | ingestedMemberCount |
      | "data/input/eventstreams/retention/mobility-hindrances_timebased.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 30                  |
      | "data/input/eventstreams/retention/observations/timebased.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 60                  |

  @version-based
  Scenario Outline: Server provides version retention
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the first fragment of the "paged" view in collection <collection> contains <expectedMemberCount> members
    And the background processes did not fail
    And the batch tables has been cleaned
    Examples:
      | eventStreamDescriptionFile                                               | template                                           | collection            | expectedMemberCount |
      | "data/input/eventstreams/retention/mobility-hindrances_versionbased.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 10                  |
      | "data/input/eventstreams/retention/observations/versionbased.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 20                  |

  # https://github.com/OpenLDES/LDESServer/issues/43
  @version-based @nested-versioned-resources
  Scenario: Server does not apply version retention on nested versioned resources
    Given I create the eventstream "data/input/eventstreams/retention/products_nested_versioned.ttl"
    When I ingest 30 members of template "data/input/members/product-nested-versioned.template.ttl" to the collection "products"
    Then every member of the collection "products" has a version_of that is the versionOf of its own subject
    And every member of the collection "products" has a timestamp after "2000-01-01T00:00:00"
    And the members of the collection "products" have 30 distinct version_of values
    And the first fragment of the "paged" view in collection "products" contains 30 members
    When the maintenance job has run
    Then the first fragment of the "paged" view in collection "products" contains 30 members
    And the background processes did not fail
    And the batch tables has been cleaned

  @combined @version-based-and-time-based
  Scenario Outline: Server combines multiple retention policies
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the first fragment of the "paged" view in collection <collection> contains <expectedMemberCount> members
    And the background processes did not fail
    And the batch tables has been cleaned
    Examples:
      | eventStreamDescriptionFile                                           | template                                           | collection            | expectedMemberCount |
      | "data/input/eventstreams/retention/mobility-hindrances_combined.ttl" | "data/input/members/mob-hind.template.ttl"         | "mobility-hindrances" | 5                   |
      | "data/input/eventstreams/retention/observations/combined.ttl"        | "data/input/members/two-observations.template.ttl" | "observations"        | 10                  |

  @deletion @version-based
  Scenario Outline: Server retention version-based to event source
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the LDES <collection> contains <expectedMemberCount> members
    And the background processes did not fail
    And the batch tables has been cleaned

    Examples:
      | eventStreamDescriptionFile                                                               | template                                   | collection            | expectedMemberCount |
      | "data/input/eventstreams/deletion/mobility-hindrances_versionbased_with_eventsource.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | 10                  |
      | "data/input/eventstreams/deletion/mobility-hindrances_versionbased_with_eventsource.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | 30                  |

  @deletion @time-based
  Scenario Outline: Server retention time-based to event source
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the LDES <collection> contains <expectedMemberCount> members
    And the background processes did not fail
    And the batch tables has been cleaned

    Examples:
      | eventStreamDescriptionFile                                                            | template                                   | collection            | expectedMemberCount |
      | "data/input/eventstreams/deletion/mobility-hindrances_timebased_with_eventsource.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | 0                   |

  @deletion @version-based-and-time-based
  Scenario Outline: Server retention combined-based to event source
    Given I create the eventstream <eventStreamDescriptionFile>
    When I ingest 30 members of template <template> to the collection <collection>
    Then the LDES <collection> contains <expectedMemberCount> members
    And the background processes did not fail
    And the batch tables has been cleaned

    Examples:
      | eventStreamDescriptionFile                                                           | template                                   | collection            | expectedMemberCount |
      | "data/input/eventstreams/deletion/mobility-hindrances_combined_with_eventsource.ttl" | "data/input/members/mob-hind.template.ttl" | "mobility-hindrances" | 5                   |
