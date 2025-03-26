package com.jmhreif.vector_graph_rag;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

public record Book(@Id String book_id,
                   String title,
                   String isbn,
                   String isbn13,
                   Double average_rating,
                   @Relationship(value = "AUTHORED", direction = Relationship.Direction.INCOMING) List<Author> authors,
                   @Relationship(value = "WRITTEN_FOR", direction = Relationship.Direction.INCOMING) List<Review> reviewList) {
}
