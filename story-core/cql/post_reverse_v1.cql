CREATE TABLE story_platform_test.post_reverse_v1
(
    service_type text,
    account_id   text,
    post_id      bigint,
    space_type   text,
    space_id     text,
    title        text,
    content      text,
    extra_json   text,
    PRIMARY KEY ((service_type, account_id), post_id, space_type, space_id)
)
WITH CLUSTERING ORDER BY
(
    post_id DESC,
    space_type DESC,
    space_id DESC
)
    AND additional_write_policy = '99p'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND cdc = false
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND memtable = 'default'
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND extensions = {}
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99p';
