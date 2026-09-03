package vn.rikkei.exam.clinicappointment.service.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.rikkei.exam.clinicappointment.dto.request.DocumentRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {
    private final VectorStore vectorStore;

    public String ingestAndSaveDocument(@RequestBody DocumentRequest documentRequest){
        Resource resource = documentRequest.getFile().getResource();
        TikaDocumentReader documentReader = new TikaDocumentReader(resource);
        List<Document> rawDocument = documentReader.get();
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder().build();
        List<Document> documents = tokenTextSplitter.split(rawDocument);

        vectorStore.add(documents);
        return "Tải lên dữ liệu thành công";
    }

    public String searchDocument(String keyword) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(keyword)
                        .topK(3)
                        .build()
        );
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }
}
