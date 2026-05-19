package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A utility class that loads crawler configuration from a JSON file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Creates a ConfigurationLoader that reads from the given file path.
   *
   * @param path the path to the configuration JSON file
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from the file.
   *
   * @return parsed CrawlerConfiguration object
   */
  public CrawlerConfiguration load() {
    try (Reader reader = Files.newBufferedReader(path)) {
      return read(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Reads JSON from a Reader and converts it into a CrawlerConfiguration.
   *
   * @param reader input reader containing JSON configuration
   * @return parsed CrawlerConfiguration
   */
  public static CrawlerConfiguration read(Reader reader) {
    Objects.requireNonNull(reader);

    ObjectMapper mapper = new ObjectMapper();

    // Prevent Jackson from closing the reader automatically
    mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

    try {
      return mapper.readValue(reader, CrawlerConfiguration.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}