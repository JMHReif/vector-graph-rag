package com.jmhreif.vector_graph_rag;

import org.springframework.data.neo4j.core.schema.Id;

public record Review(@Id String id,
                     String text,
                     String book_id,
                     Integer rating) {
}
