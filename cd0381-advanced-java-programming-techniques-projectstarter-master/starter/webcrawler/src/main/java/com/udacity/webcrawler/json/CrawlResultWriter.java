package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Utility class to write a CrawlResult to a file or writer.
 */
public final class CrawlResultWriter {

  private final CrawlResult result;

  /**
   * Constructs a writer for the given crawl result.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Writes the result as JSON to a file.
   * Appends if the file already exists.
   */
  public void write(Path path) {
    Objects.requireNonNull(path);

    try (Writer writer = Files.newBufferedWriter(
        path,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND)) {

      write(writer);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Writes the result as JSON using a Writer.
   */
  public void write(Writer writer) {
    Objects.requireNonNull(writer);

    ObjectMapper mapper = new ObjectMapper();

    // Prevent Jackson from closing the writer
    mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

    try {
      mapper.writeValue(writer, result);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}