We report. You decipher.

NewsTerp will be a basic news aggregation and fact extraction service.

News articles are downloaded and clustered, before basic relations are extracted using light NLP parsing and noun-phrase chunking similar to [TextRunner](TextRunner.md). Similar relations are merged among clustered articles, exposing prevalent extracted facts among the various articles.

These facts are then used to locate sentences from the original articles describing the critical events.