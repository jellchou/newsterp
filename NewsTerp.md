# Automated fact extraction #

News articles are downloaded and clustered, before basic relations are extracted using light NLP parsing and noun-phrase chunking (similar to www.cs.washington.edu/research/textrunner/). Similar relations are merged among clustered articles, exposing prevalent extracted facts among the various articles.

These facts are then used to locate sentences from the original articles describing the critical events.