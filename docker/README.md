```shell
docker exec story-kafka-local kafka-console-consumer --bootstrap-server localhost:9092 --topic post_v1 --group test-consumer --from-beginning
```

```shell
docker exec story-kafka-local kafka-consumer-groups --bootstrap-server localhost:9092 --group a427716d-2c7e-402a-b330-559d1ce4bf2a --topic story.api-key-event-v1 --reset-offsets --to-earliest --execute
```

```shell
docker exec story-kafka-local kafka-consumer-groups --bootstrap-server localhost:9092 --group api-key-cache-evict-consumer-a427716d-2c7e-402a-b330-559d1ce4bf2a --topic story.api-key-event-v1 --reset-offsets --to-earliest --execute
```

```shell
docker exec story-kafka-local kafka-consumer-groups --bootstrap-server localhost:9092 --group api-key-cache-evict-consumer-a427716d-2c7e-402a-b330-559d1ce4bf2a --describe
```
