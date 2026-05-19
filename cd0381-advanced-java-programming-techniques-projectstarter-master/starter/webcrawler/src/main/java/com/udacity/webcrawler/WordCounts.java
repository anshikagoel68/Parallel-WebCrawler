package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class that sorts the map of word counts.
 *
 * Reimplemented using Stream API and lambdas as required in Step 5.
 */
final class WordCounts {

  /**
   * Given an unsorted map of word counts, returns a new map whose word counts are sorted according
   * to the provided WordCountComparator, and includes only the top
   * popularWordCount words and counts.
   *
   * @param wordCounts       the unsorted map of word counts.
   * @param popularWordCount the number of popular words to include in the result map.
   * @return a map containing the top popularWordCount words and counts in the correct order.
   */
  static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {

    return wordCounts.entrySet()
        .stream()
        .sorted(new WordCountComparator())
        .limit(Math.min(popularWordCount, wordCounts.size()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (existing, replacement) -> existing,
            LinkedHashMap::new
        ));
  }

  /**
   * Comparator that sorts word count pairs correctly:
   *
   * 1. First by word count (higher count first)
   * 2. Then by word length (longer word first)
   * 3. Then alphabetically (ascending)
   */
  private static final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {

    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {

      int countCompare = b.getValue().compareTo(a.getValue());
      if (countCompare != 0) {
        return countCompare;
      }

      int lengthCompare = Integer.compare(b.getKey().length(), a.getKey().length());
      if (lengthCompare != 0) {
        return lengthCompare;
      }

      return a.getKey().compareTo(b.getKey());
    }
  }

  private WordCounts() {
    // prevent instantiation
  }
}