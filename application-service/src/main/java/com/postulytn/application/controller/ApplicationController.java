package com.postulytn.application.controller;

import com.postulytn.application.dto.ApplicationCreateDTO;
import com.postulytn.application.dto.ApplicationDTO;
import com.postulytn.application.dto.StatusUpdateDTO;
import com.postulytn.application.service.IApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final IApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationDTO> createApplication(@Valid @RequestBody ApplicationCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(dto));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO dto) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
