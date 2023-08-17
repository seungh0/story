// single
create keyspace story_platform_local with replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

// cluster
create keyspace story_platform_local with replication = { 'class': 'NetworkTopologyStrategy', 'dc1': 3 };


create table authentication_v1
(
    authentication_key text primary key,
    description        text,
    status             text,
    workspace_id       text
)   WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table workspace_v1
(
    workspace_id text primary key,
    created_at   timestamp,
    name         text,
    plan         text,
    status       text,
    updated_at   timestamp
)   WITH bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
     AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     AND crc_check_chance = 1.0
     AND default_time_to_live = 0
     AND gc_grace_seconds = 864000
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND speculative_retry = '99p';


create table workspace_authentication_v1
(
    workspace_id       text,
    authentication_key text,
    created_at         timestamp,
    description        text,
    status             text,
    updated_at         timestamp,
    primary key (workspace_id, authentication_key)
)
    with clustering order by (authentication_key desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table component_v1
(
    workspace_id text,
    resource_id  text,
    component_id text,
    created_at   timestamp,
    description  text,
    status       text,
    updated_at   timestamp,
    primary key ((workspace_id, resource_id), component_id)
)
    with clustering order by (component_id desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table event_history_v1
(
    workspace_id   text,
    slot_id        bigint,
    event_id       bigint,
    resource_id    text,
    component_id   text,
    event_action   text,
    failure_reason text,
    payload_json   text,
    publish_status text,
    primary key ((workspace_id, slot_id), event_id, resource_id, component_id, event_action)
)
    with clustering order by (event_id desc, resource_id desc, component_id desc, event_action desc)
     and bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     AND crc_check_chance = 1.0
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200 // 3d
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND speculative_retry = '99p';


create table event_key_id_mapping_v1
(
    workspace_id text,
    event_key    text,
    event_id     bigint,
    primary key ((workspace_id, event_key), event_id)
)
    with clustering order by (event_id desc)
     and bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND compression = {'enabled':'false'}
     AND crc_check_chance = 1.0
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200 // 3d
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND speculative_retry = '99p';


create table post_v1
(
    workspace_id text,
    component_id text,
    space_id     text,
    slot_id      bigint,
    post_id      bigint,
    account_id   text,
    content      text,
    created_at   timestamp,
    extra_json   text,
    title        text,
    updated_at   timestamp,
    primary key ((workspace_id, component_id, space_id, slot_id), post_id)
)
    with clustering order by (post_id desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table post_reverse_v1
(
    workspace_id     text,
    component_id     text,
    distribution_key text,
    account_id       text,
    post_id          bigint,
    space_id         text,
    content          text,
    extra_json       text,
    slot_id          bigint,
    title            text,
    primary key ((workspace_id, component_id, distribution_key), account_id, post_id, space_id)
)
    with clustering order by (account_id desc, post_id desc, space_id desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table subscriber_v1
(
    workspace_id  text,
    component_id  text,
    target_id     text,
    slot_id       bigint,
    subscriber_id text,
    alarm         boolean,
    primary key ((workspace_id, component_id, target_id, slot_id), subscriber_id)
)
    with bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table subscription_v1
(
    workspace_id     text,
    component_id     text,
    distribution_key text,
    subscriber_id    text,
    target_id        text,
    alarm            boolean,
    slot_id          bigint,
    status           text,
    primary key ((workspace_id, component_id, distribution_key), subscriber_id, target_id)
)
    with clustering order by (subscriber_id desc, target_id asc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table feed_mapping_configuration_v1
(
    workspace_id              text,
    feed_component_id         text,
    source_resource_id        text,
    source_component_id       text,
    subscription_component_id text,
    created_at                timestamp,
    description               text,
    status                    text,
    updated_at                timestamp,
    primary key (workspace_id, feed_component_id, source_resource_id, source_component_id, subscription_component_id)
)
    with clustering order by (feed_component_id desc, source_resource_id desc, source_component_id desc, subscription_component_id desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table feed_reverse_mapping_configuration_v1
(
    workspace_id              text,
    source_resource_id        text,
    source_component_id       text,
    subscription_component_id text,
    feed_component_id         text,
    primary key ((workspace_id, source_resource_id, source_component_id), subscription_component_id, feed_component_id)
)
    with clustering order by (subscription_component_id desc, feed_component_id desc)
    and bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99p';


create table feed_v1
(
    workspace_id        text,
    feed_component_id   text,
    distribution_key    text,
    subscriber_id       text,
    event_id            bigint,
    payload_json        text,
    source_component_id text,
    source_resource_id  text,
    primary key ((workspace_id, feed_component_id, distribution_key), subscriber_id, event_id)
)
    with clustering order by (subscriber_id desc, event_id desc)
     and bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     AND crc_check_chance = 1.0
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200 // 3d
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND speculative_retry = '99p';

create table feed_subscriber_v1
(
    workspace_id      text,
    feed_component_id text,
    event_key         text,
    slot_id           bigint,
    subscriber_id     text,
    event_id          bigint,
    primary key ((workspace_id, feed_component_id, event_key, slot_id), subscriber_id)
)
    with clustering order by (subscriber_id desc)
     and bloom_filter_fp_chance = 0.01
     AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
     AND crc_check_chance = 1.0
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200 // 3d
     AND max_index_interval = 2048
     AND memtable_flush_period_in_ms = 0
     AND min_index_interval = 128
     AND speculative_retry = '99p';

