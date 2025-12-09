package com.postulytn.company.controller;

import com.postulytn.company.dto.RecruiterDTO;
import com.postulytn.company.service.IRecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruiters")
@RequiredArgsConstructor
public class RecruiterController {
    
    private final IRecruiterService recruiterService;
    
    @GetMapping("/{id}")
    public ResponseEntity<RecruiterDTO> getRecruiterById(@PathVariable Long id) {
        return ResponseEntity.ok(recruiterService.getRecruiterById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<RecruiterDTO>> getAllRecruiters() {
        return ResponseEntity.ok(recruiterService.getAllRecruiters());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecruiterDTO> updateRecruiter(
            @PathVariable Long id,
            @Valid @RequestBody RecruiterDTO dto) {
        return ResponseEntity.ok(recruiterService.updateRecruiter(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable Long id) {
        recruiterService.deleteRecruiter(id);
        return ResponseEntity.noContent().build();
    }
}
