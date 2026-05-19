# Parallel Web Crawler

This project implements a parallel web crawler in Java using modern concurrency techniques.
It crawls real web pages, counts word frequencies, avoids revisiting URLs, respects a configurable timeout, and supports profiling using dynamic proxies.

The project demonstrates:

* Parallel programming using ForkJoinPool
* Thread-safe data structures
* Dependency Injection
* JSON configuration parsing
* Functional programming with Stream API
* Dynamic Proxy profiling

---

## Features

* Parallel crawling using ForkJoinPool
* Sequential crawling support
* URL deduplication (no URL visited twice)
* Configurable crawl depth
* Configurable timeout
* Ignored URL patterns
* Top N popular word counting
* JSON configuration input
* JSON result output
* Dynamic proxy profiling with @Profiled
* Proper exception handling and return value preservation

---

## Technologies Used

* Java 8+
* Maven
* ForkJoin Framework
* javax.inject (Dependency Injection)
* ConcurrentHashMap
* ConcurrentSkipListSet
* Stream API
* Dynamic Proxy API

---

## Project Structure

```
webcrawler/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/udacity/webcrawler/
 │   │   ├── config/
 │   │   └── resources/
 │   └── test/
 ├── pom.xml
 └── README.md
```

---

## Build Instructions

Make sure Maven is installed.

### Clean Project

```
mvn clean
```

### Run Unit Tests

```
mvn test
```

All tests must pass before packaging.

### Package the Project

```
mvn package
```

This generates:

```
target/udacity-webcrawler-1.0.jar
```

---

## Running the Crawler

### Sequential Mode

```
java -classpath target/udacity-webcrawler-1.0.jar com.udacity.webcrawler.main.WebCrawlerMain src/main/java/com/udacity/webcrawler/main/config/sample_config_sequential.json
```

### Parallel Mode

```
java -classpath target/udacity-webcrawler-1.0.jar com.udacity.webcrawler.main.WebCrawlerMain src/main/config/sample_config.json
```

---

## Configuration File Format

The crawler reads configuration from a JSON file.

Example:

```json
{
  "startingUrls": ["https://example.com"],
  "maxDepth": 3,
  "timeoutSeconds": 5,
  "popularWordCount": 5,
  "ignoredUrls": [],
  "threadCount": 4
}
```

### Configuration Fields

* startingUrls — List of URLs to begin crawling
* maxDepth — Maximum crawl depth
* timeoutSeconds — Maximum crawl time
* popularWordCount — Number of top words to return
* ignoredUrls — Regex patterns to ignore URLs
* threadCount — Parallel thread count

---

## Timeout Behavior

* The crawler stops scheduling new tasks after timeout
* Already running tasks are allowed to finish
* Partial results are returned if timeout occurs

Test suggestion:

Set:

* maxDepth = 10
* timeoutSeconds = 1

Crawler should stop after approximately 1–2 seconds.

---

## Parallelism & Synchronization

* Uses ForkJoinPool for parallel execution
* Thread count limited by available processors
* Uses ConcurrentHashMap for word counts
* Uses ConcurrentSkipListSet for visited URLs
* Each URL is counted only once
* Multiple threads run concurrently

---

## Word Counting

* Words are merged using:

  ```
  counts.merge(word, count, Integer::sum)
  ```
* Sorting implemented using Stream API
* Only top N words returned

---

## Profiling

* Implemented using Java Dynamic Proxy
* Only methods annotated with @Profiled are recorded
* Original return values are preserved
* Exceptions are rethrown without wrapping
* equals() method from Object class is not intercepted

---

## JSON Output Format

Example output:

```json
{
  "wordCounts": {
    "library": 35,
    "borrow": 33,
    "books": 28
  },
  "urlsVisited": 12
}
```

If no output file is specified, results are printed to standard output.

---

## Design Patterns Used

* Dependency Injection
* Builder Pattern (CrawlResult.Builder)
* Factory Pattern (PageParserFactory)
* Dynamic Proxy Pattern
* Functional Programming (Streams API)

---

## Requirements

* Java 8 or higher
* Maven 3.x

