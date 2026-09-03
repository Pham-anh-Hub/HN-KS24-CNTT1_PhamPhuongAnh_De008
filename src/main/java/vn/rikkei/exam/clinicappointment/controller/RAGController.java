package vn.rikkei.exam.clinicappointment.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.rikkei.exam.clinicappointment.dto.request.ChatRequest;
import vn.rikkei.exam.clinicappointment.dto.request.DocumentRequest;
import vn.rikkei.exam.clinicappointment.service.rag.RAGService;

@RestController
@RequestMapping("/api/v1/ai/rag")
@RequiredArgsConstructor
public class RAGController {

    private final RAGService ragService;

    @PostMapping("/load")
    public String ingestDocument(@RequestParam DocumentRequest request){
        return ragService.ingestAndSaveDocument(request);
    }

    @GetMapping("/search")
    public String searchData(@RequestBody String keyword){
        return ragService.searchDocument(keyword);
    }


}
