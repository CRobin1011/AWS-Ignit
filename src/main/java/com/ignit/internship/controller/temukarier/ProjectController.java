package com.ignit.internship.controller.temukarier;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ignit.internship.dto.DefaultResponse;
import com.ignit.internship.dto.ResponseReturn;
import com.ignit.internship.dto.temukarier.ProjectRequest;
import com.ignit.internship.dto.temukarier.ProjectResponse;
import com.ignit.internship.exception.IdNotFoundException;
import com.ignit.internship.model.auth.User;
import com.ignit.internship.service.temukarier.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/temukarier/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(description = "Create Project")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<DefaultResponse<ProjectResponse>> createProject(
        @RequestPart MultipartFile file,
        @RequestPart ProjectRequest request,
        @Parameter(hidden = true)
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseReturn.ok(new ProjectResponse(projectService.createProject(file, request, user.getId())));
    }

    @Operation(description = "Get Project by id")
    @GetMapping("/{id}")
    public ResponseEntity<DefaultResponse<ProjectResponse>> getProject(@PathVariable Long id) throws IdNotFoundException {
        return ResponseReturn.ok(new ProjectResponse(projectService.getProjectById(id)));
    }

    @Operation(description = "Update Project by id")
    @PatchMapping("/{id}")
    public ResponseEntity<DefaultResponse<ProjectResponse>> updateProject(
        @RequestBody ProjectRequest request,
        @PathVariable Long id,
        @Parameter(hidden = true)
        @CurrentSecurityContext SecurityContext context
    ) throws IdNotFoundException, IOException {
        User user = (User) context.getAuthentication().getPrincipal();
        return ResponseReturn.ok(new ProjectResponse(projectService.updateProject(request, id, user.getId())));
    }

    @Operation(description = "Get Project by page and tag")
    @GetMapping
    public ResponseEntity<DefaultResponse<List<ProjectResponse>>> getProjectByTagsAndPage(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (tags == null) {
            return ResponseReturn.ok(
                projectService.getProjectByPage(pageable).stream().map(p -> new ProjectResponse(p)).toList()
            );
        }
        else {
            return ResponseReturn.ok(
                projectService.getProjectByPageAndTag(pageable, tags).stream().map(p -> new ProjectResponse(p)).toList()
            );
        }
    }

    @Operation(description = "Join Project by id, token sent to user that creates the project")
    @GetMapping("/{id}/join")
    public ResponseEntity<DefaultResponse<Object>> joinProject(
        @PathVariable Long id,
        @Parameter(hidden = true)
        @CurrentSecurityContext SecurityContext context
    ) throws IdNotFoundException {
        User user = (User) context.getAuthentication().getPrincipal();
        projectService.joinProject(user.getId(), id);
        return ResponseReturn.ok(null);
    }

    @Operation(description = "Approve Join Project by applicant id, only user that creates the project can approve")
    @GetMapping("/{projectId}/approve/{applicantId}")
    public ResponseEntity<DefaultResponse<Object>> approveJoinProject(
        @PathVariable Long projectId,
        @PathVariable Long applicantId,
        @Parameter(hidden = true)
        @CurrentSecurityContext SecurityContext context
    ) throws Exception {
        User user = (User) context.getAuthentication().getPrincipal();
        projectService.approveJoinProject(user.getId(), projectId, applicantId);
        return ResponseReturn.ok(null);
    }

    @Operation(description = "Delete Project by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultResponse<Object>> deleteProject(Long id) throws IdNotFoundException {
        projectService.deleteProject(id);
        return ResponseReturn.ok(null);
    }
}
