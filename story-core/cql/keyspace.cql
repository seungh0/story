// single
create keyspace story_platform_local with replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };
create keyspace reaper with replication = { 'class': 'NetworkTopologyStrategy', 'dc1': 1 };

// cluster
create keyspace story_platform_local with replication = { 'class': 'NetworkTopologyStrategy', 'dc1': 3 };


create table workspace_v1
(
    workspace_id text primary key,
    status       text,
    name         text,
    plan         text,
    created_at   timestamp,
    updated_at   timestamp,
);

create table workspace_archive_v1
(
    workspace_id text primary key,
    archive_time timestamp,
    created_at   timestamp,
    name         text,
    plan         text,
    updated_at   timestamp
);

create table api_key_v1
(
    api_key text primary key,
    workspace_id       text,
    status             text,
    description        text,
    created_at timestamp,
    updated_at timestamp,
);


create table workspace_api_key_v1
(
    workspace_id       text,
    api_key text,
    status             text,
    description        text,
    created_at         timestamp,
    updated_at         timestamp,
    primary key (workspace_id, api_key)
);


create table component_v1
(
    workspace_id text,
    resource_id  text,
    component_id text,
    status       text,
    description  text,
    created_at   timestamp,
    updated_at   timestamp,
    primary key ((workspace_id, resource_id), component_id)
);


create table event_history_v1
(
    workspace_id   text,
    slot_id        bigint,
    event_id       bigint,
    resource_id    text,
    component_id   text,
    event_action   text,
    publish_status text,
    payload_json   text,
    failure_reason text,
    primary key ((workspace_id, slot_id), event_id, resource_id, component_id, event_action)
)
    with clustering order by (event_id desc, resource_id desc, component_id desc, event_action desc)
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200; // 3d

create table post_v1
(
    workspace_id text,
    component_id text,
    space_id     text,
    slot_id      bigint,
    post_id      bigint,
    USER_ID   text,
    title        text,
    content      text,
    extra map<text,text>,
    metadata map<text,text>,
    created_at   timestamp,
    updated_at   timestamp,
    primary key ((workspace_id, component_id, space_id, slot_id), post_id)
)
    with clustering order by (post_id desc);


create table post_reverse_v1
(
    workspace_id     text,
    component_id     text,
    distribution_key text,
    USER_ID       text,
    post_id          bigint,
    space_id         text,
    title            text,
    content          text,
    extra map<text,text>,
    slot_id          bigint,
    primary key ((workspace_id, component_id, distribution_key), USER_ID, post_id, space_id)
)
    with clustering order by (USER_ID desc, post_id desc, space_id desc);


create table post_section_v1
(
    workspace_id text,
    component_id text,
    space_id     text,
    parent_id    text,
    slot_id      bigint,
    post_no      bigint,
    priority     bigint,
    data         text,
    section_type text,
    primary key ((workspace_id, component_id, space_id, parent_id, slot_id), post_no, priority)
)
    with clustering order by (post_no desc, priority asc);


create table subscriber_v1
(
    workspace_id  text,
    component_id  text,
    target_id     text,
    slot_id       bigint,
    subscriber_id text,
    alarmEnabled  boolean,
    primary key ((workspace_id, component_id, target_id, slot_id), subscriber_id)
);


create table subscription_v1
(
    workspace_id     text,
    component_id     text,
    distribution_key text,
    subscriber_id    text,
    target_id        text,
    status           text,
    alarmEnabled     boolean,
    slot_id          bigint,
    primary key ((workspace_id, component_id, distribution_key), subscriber_id, target_id)
);

create table subscriber_count_v1
(
    workspace_id text,
    component_id text,
    target_id    text,
    count        counter,
    primary key ((workspace_id, component_id, target_id))
);


create table feed_mapping_v1
(
    workspace_id              text,
    feed_component_id         text,
    source_resource_id        text,
    source_component_id       text,
    subscription_component_id text,
    status                    text,
    description               text,
    retention                 text,
    created_at                timestamp,
    updated_at                timestamp,
    primary key (workspace_id, feed_component_id, source_resource_id, source_component_id, subscription_component_id)
);


create table feed_mapping_reverse_v1
(
    workspace_id              text,
    source_resource_id        text,
    source_component_id       text,
    subscription_component_id text,
    feed_component_id         text,
    retention                 text,
    primary key ((workspace_id, source_resource_id, source_component_id), subscription_component_id, feed_component_id)
);


create table feed_v1
(
    workspace_id        text,
    feed_component_id   text,
    distribution_key    text,
    subscriber_id       text,
    feed_id            bigint,
    event_key           text,
    source_resource_id  text,
    source_component_id text,
    payload_json        text,
    primary key ((workspace_id, feed_component_id, distribution_key), subscriber_id, feed_id)
)
    with clustering order by (subscriber_id desc, feed_id desc)
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200; // 3d

create table feed_subscriber_v1
(
    workspace_id      text,
    feed_component_id text,
    event_key         text,
    slot_id           bigint,
    subscriber_id     text,
    feed_id          bigint,
    primary key ((workspace_id, feed_component_id, event_key, slot_id), subscriber_id)
)
    with clustering order by (subscriber_id desc)
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND default_time_to_live = 2592000 // 30d
     AND gc_grace_seconds = 259200;// 3d


create table reaction_v1
(
    workspace_id     text,
    component_id     text,
    space_id         text,
    distribution_key text,
    user_id          text,
    emotion_ids      set<text>,
    primary key ((workspace_id, component_id, space_id, distribution_key), user_id)
)
            with clustering order by (user_id desc);


create table reaction_reverse_v1
(
    workspace_id     text,
    component_id     text,
    user_id          text,
    distribution_key text,
    space_id         text,
    emotion_ids      set<text>,
    primary key ((workspace_id, component_id, user_id, distribution_key), space_id)
)
            with clustering order by (space_id desc);


create table reaction_count_v1
(
    workspace_id text,
    component_id text,
    space_id     text,
    emotion_id   text,
    count        counter,
    primary key ((workspace_id, component_id, space_id), emotion_id)
)
            with clustering order by (emotion_id desc);


create table emotion_v1
(
    workspace_id text,
    resource_id  text,
    component_id text,
    emotion_id   text,
    created_at   timestamp,
    image        text,
    priority     bigint,
    updated_at   timestamp,
    primary key ((workspace_id, resource_id, component_id), emotion_id)
);


create table file_v1
(
    workspace_id text,
    file_type    text,
    slot_id      bigint,
    file_id      bigint,
    file_size    bigint,
    height       int,
    path         text,
    width        int,
    primary key ((workspace_id, file_type, slot_id), file_id)
) with clustering order by (file_id desc)
     AND compaction = {'class':'TimeWindowCompactionStrategy', 'unchecked_tombstone_compaction':'true'}
     AND default_time_to_live = 259200 // 3d
     AND gc_grace_seconds = 259200;// 3d

