package com.gfa.otocyonknowledgebase.controllers;

import com.gfa.otocyonknowledgebase.config.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


public interface ApiController {


  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping("/seeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Get seed",
      description = "In this endpoint, you can find a seed by its unique ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return seed by id"),
      @ApiResponse(responseCode = "404", description = "Seed not found for ID: (your id)"),})
  ResponseEntity<?> getById(@PathVariable Long id);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping("/")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.getAll)
  @Operation(summary = "Get all seeds ",
      description = "This endpoint provides a list of all seeds along with their tags and files.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return all seeds"),})
  ResponseEntity<?> getAll();

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @PostMapping("/seeds")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.dataPapyruses)
  @Operation(summary = "Post seed",
      description = " In this endpoint, you can create a new seed, a new file, or both.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return new seed"),
      @ApiResponse(responseCode = "400", description = "No body"),})
  ResponseEntity<?> saveSeed(@RequestParam("seed") Optional<String> seedContent,
                             @RequestParam("file") Optional<MultipartFile> file);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @PutMapping("/seeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Update seed content",
      description = "In this endpoint, you can update the content of an existing seed.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return new seed"),
      @ApiResponse(responseCode = "404", description = "Seed not found for ID: (your id)"),})
  ResponseEntity<?> updateSeedContent(@PathVariable Long id,
                                      @RequestParam String newContent);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping("/fileseeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.dataPapyruses)
  @Operation(summary = "Get file",
      description = "In this endpoint, you can retrieve a file using its ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return file"),
      @ApiResponse(responseCode = "404", description = "No body"),})
  ResponseEntity<?> getFileSeed(@PathVariable Long id);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @DeleteMapping("/fileseeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.dataPapyruses)
  @Operation(summary = "Delete file",
      description = "In this endpoint, you can delete a file using its ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return seed of file"),
      @ApiResponse(responseCode = "400", description = "Something's gone wrong!"),})
  ResponseEntity<?> deleteFileSeedByFileID(@PathVariable Long id);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @PostMapping("/fileseeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.dataPapyruses)
  @Operation(summary = "Add file to seed",
      description = "In this endpoint, you can add a file to an existing seed.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return seed of file"),
      @ApiResponse(responseCode = "400", description = "No body"),})
  ResponseEntity<?> addFileToSeed(@PathVariable Long id,
                                  @RequestParam("file") Optional<MultipartFile> file);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @DeleteMapping(value = "/seeds/{id}")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Delete seed",
      description = "In this endpoint, you can delete a seed by its ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return deleted seed"),
      @ApiResponse(responseCode = "404", description = "Seed not found for ID: (your id)"),})
  ResponseEntity<?> deleteSeed(@PathVariable Long id) throws IOException;

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping(value = "/query")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Find seeds by tags",
      description = "In this endpoint, you can find all seeds containing the selected tags.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return list seeds"),
      @ApiResponse(responseCode = "404", description = "No seed found for the given tags."),})
  ResponseEntity<?> findByTags(@RequestParam List<String> providedTags);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping(value = "/seeds/{id}/tags")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.tags)
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Get tags connected to seed",
      description = "In this endpoint, you can retrieve all tags connected to a specific seed.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return list tags"),
      @ApiResponse(responseCode = "404", description = "Seed not found for ID: (your id)"),})
  ResponseEntity<?> getTagsBySeedId(@PathVariable Long id);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping("/tags")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.tags)
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.getAll)
  @Operation(summary = "Get all tags",
      description = "In this endpoint, you can retrieve a list of all tags.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return list tags"),})
  ResponseEntity<?> getAllTags();

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @PostMapping(value = "/seeds/{id}/tags")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.tags)
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Add tag to seed",
      description = "In this endpoint, you can add a tag to a seed by its ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return seed"),
      @ApiResponse(responseCode = "404", description = "Seed not found for ID: (your id)"),})
  ResponseEntity<?> addTagToSeed(@PathVariable Long id, @RequestParam Optional<String> tagInput);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @PutMapping("/tagname")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.tags)
  @Operation(summary = "Update tag name",
      description = "In this endpoint, you can update the name of an existing tag.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return tag"),
      @ApiResponse(responseCode = "400", description = "No body"),})
  ResponseEntity<?> updateTagName(@RequestParam Optional<String> oldTagName,
                                  @RequestParam Optional<String> newTagName);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @DeleteMapping("/seeds/{id}/tags")
  @SecurityRequirement(name = "jwtToken")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.tags)
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.seeds)
  @Operation(summary = "Delete tag from seed",
      description = "In this endpoint, you can delete a tag from a seed by its ID.")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return seed"),
      @ApiResponse(responseCode = "400", description = "No body"),})
  ResponseEntity<?> deleteTagInSeed(@PathVariable Long id, @RequestParam String tagName);

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @GetMapping("/fileseeds")
  @io.swagger.v3.oas.annotations.tags.Tag(name = SwaggerTags.getAll)
  @Operation(summary = "Ester egg", description = "You found an easter egg!")
  ResponseEntity<?> saveFileSeed();

}