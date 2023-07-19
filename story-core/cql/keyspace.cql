// single
create keyspace story_platform_local with replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

// cluster
create keyspace story_platform_local with replication = { 'class': 'NetworkTopologyStrategy', 'dc1': 3 };
