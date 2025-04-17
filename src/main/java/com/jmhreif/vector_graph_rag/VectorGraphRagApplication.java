package com.jmhreif.vector_graph_rag;

import org.neo4j.driver.Driver;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VectorGraphRagApplication {

	public static void main(String[] args) {
		SpringApplication.run(VectorGraphRagApplication.class, args);
	}

	@Bean
	public PineconeVectorStore pineconeVectorStore(EmbeddingModel embeddingModel) {
		return PineconeVectorStore.builder(embeddingModel)
				.apiKey(System.getenv("SPRING_AI_VECTORSTORE_PINECONE_API_KEY"))
				.indexName(System.getenv("SPRING_AI_VECTORSTORE_PINECONE_INDEX_NAME"))
				.build();
	}

	@Bean
	Neo4jVectorStore neo4jVectorStore(Driver driver, EmbeddingModel embeddingModel) {
		return Neo4jVectorStore.builder(driver, embeddingModel)
				.indexName(System.getenv("SPRING_AI_VECTORSTORE_NEO4J_INDEX_NAME"))
				.label(System.getenv("SPRING_AI_VECTORSTORE_NEO4J_LABEL"))
				.initializeSchema(true)
				.build();
	}
}
