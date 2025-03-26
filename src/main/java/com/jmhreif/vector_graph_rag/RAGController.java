package com.jmhreif.vector_graph_rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RAGController {
    private final ChatClient chatClient;
    private final PineconeVectorStore pineconeVectorStore;
    private final Neo4jVectorStore neo4jVectorStore;
    private final BookRepository bookRepository;

    String prompt = """
            You are a book expert providing recommendations from high-quality book information in the CONTEXT section.
            Please summarize the books provided in the context section.
            
            CONTEXT:
            {context}
            
            PHRASE:
            {searchPhrase}
            """;

    public RAGController(ChatClient.Builder builder, PineconeVectorStore pineconeVectorStore, Neo4jVectorStore neo4jVectorStore, BookRepository bookRepository) {
        this.chatClient = builder.build();
        this.pineconeVectorStore = pineconeVectorStore;
        this.neo4jVectorStore = neo4jVectorStore;
        this.bookRepository = bookRepository;
    }

    //Vector RAG - embeddings are on Review text, so missing some context
    @GetMapping("/vectorRAG")
    public String vSimilarityResponse(@RequestParam String searchPhrase) {
        List<Document> results = pineconeVectorStore.doSimilaritySearch(SearchRequest.builder().query(searchPhrase).topK(5).build());
//        System.out.println("--- Results ---");
//        System.out.println(results);

        var template = new PromptTemplate(prompt,
                Map.of("context", results.stream().map(Document::toString).collect(Collectors.joining("\n")),
                        "searchPhrase", searchPhrase));
        System.out.println("----- VectorRAG PROMPT -----");
        System.out.println(template.render());

        return chatClient.prompt(template.create()).call().content();
    }

    //Graph RAG - vector search + retrieval query for related context
    @GetMapping("/graphRAG")
    public String gSimilarityResponse(@RequestParam String searchPhrase) {
        List<Document> results = neo4jVectorStore.doSimilaritySearch(SearchRequest.builder().query(searchPhrase).topK(5).build());
//        System.out.println("--- Results ---");
//        System.out.println(results);

        List<Book> bookList = bookRepository.findBooks(results.stream().map(Document::getId).toList());
//        System.out.println("--- Book list ---");
//        System.out.println(bookList);

        var template = new PromptTemplate(prompt,
                Map.of("context", bookList.stream().map(Book::toString).collect(Collectors.joining("\n")),
                        "searchPhrase", searchPhrase));
        System.out.println("----- GraphRAG PROMPT -----");
        System.out.println(template.render());

        return chatClient.prompt(template.create()).call().content();
    }
}
