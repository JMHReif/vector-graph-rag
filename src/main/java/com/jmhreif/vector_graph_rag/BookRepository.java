package com.jmhreif.vector_graph_rag;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface BookRepository extends Neo4jRepository<Book, String> {
    @Query("MATCH (b:Book)<-[rel:WRITTEN_FOR]-(r:Review) " +
            "WHERE r.id IN $reviewIds " +
            "OPTIONAL MATCH (a:Author)-[rel2:AUTHORED]->(b)" +
            "RETURN b, collect(rel), collect(r), collect(rel2), collect(a);")
    List<Book> findBooks(List<String> reviewIds);
}
