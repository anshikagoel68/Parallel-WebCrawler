package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

final class ParallelWebCrawler implements WebCrawler {

  private final Clock clock;
  private final PageParserFactory parserFactory;
  private final Duration timeout;
  private final int popularWordCount;
  private final int maxDepth;
  private final List<Pattern> ignoredUrls;
  private final ForkJoinPool pool;

  @Inject
  ParallelWebCrawler(
      Clock clock,
      PageParserFactory parserFactory,
      @Timeout Duration timeout,
      @PopularWordCount int popularWordCount,
      @MaxDepth int maxDepth,
      @IgnoredUrls List<Pattern> ignoredUrls,
      @TargetParallelism int threadCount) {

    this.clock = clock;
    this.parserFactory = parserFactory;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.maxDepth = maxDepth;
    this.ignoredUrls = ignoredUrls;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {

    Instant deadline = clock.instant().plus(timeout);

    Map<String, Integer> counts = new ConcurrentHashMap<>();
    Set<String> visitedUrls = new ConcurrentSkipListSet<>();

    List<CrawlTask> tasks = startingUrls.stream()
        .map(url -> new CrawlTask(url, deadline, maxDepth, counts, visitedUrls))
        .toList();

    for (CrawlTask task : tasks) {
      pool.invoke(task);
    }

    return new CrawlResult.Builder()
        .setWordCounts(WordCounts.sort(counts, popularWordCount))
        .setUrlsVisited(visitedUrls.size())
        .build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }

  private class CrawlTask extends RecursiveAction {

    private final String url;
    private final Instant deadline;
    private final int depth;
    private final Map<String, Integer> counts;
    private final Set<String> visitedUrls;

    CrawlTask(
        String url,
        Instant deadline,
        int depth,
        Map<String, Integer> counts,
        Set<String> visitedUrls) {

      this.url = url;
      this.deadline = deadline;
      this.depth = depth;
      this.counts = counts;
      this.visitedUrls = visitedUrls;
    }

    @Override
    protected void compute() {

      if (depth == 0) {
        return;
      }

      
      for (Pattern pattern : ignoredUrls) {
        if (pattern.matcher(url).matches()) {
          return;
        }
      }

      if (!visitedUrls.add(url)) {
        return;
      }

      PageParser.Result result = parserFactory.get(url).parse();

      result.getWordCounts().forEach(
          (word, count) -> counts.merge(word, count, Integer::sum)
      );

      if (clock.instant().isAfter(deadline)) {
        return;
      }

      List<CrawlTask> subtasks = result.getLinks().stream()
          .map(link -> new CrawlTask(link, deadline, depth - 1, counts, visitedUrls))
          .toList();

      invokeAll(subtasks);
    }
  }
}