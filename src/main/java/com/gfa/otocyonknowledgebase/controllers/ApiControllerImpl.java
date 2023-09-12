package com.gfa.otocyonknowledgebase.controllers;

import com.gfa.otocyonknowledgebase.dto.SeedDto;
import com.gfa.otocyonknowledgebase.dto.TagDto;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.BadRequestException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.HandleMaxUpLoadSizeExceededException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedNotFoundByTagsException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedNotFoundException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.TeapotException;
import com.gfa.otocyonknowledgebase.models.DataPapyrus;
import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import com.gfa.otocyonknowledgebase.security.models.Role;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.services.UserService;
import com.gfa.otocyonknowledgebase.services.DataPapyrusService;
import com.gfa.otocyonknowledgebase.services.SeedService;
import com.gfa.otocyonknowledgebase.services.TagService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ApiControllerImpl implements ApiController {
  private final SeedService seedService;
  private final TagService tagService;
  private final DataPapyrusService dataPapyrusService;
  private final UserService userService;
  private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

  @Override
  @GetMapping("/seeds/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    logger.info("Received request for seed with ID: {}", id);

    if (seedService.isPresent(id)) {
      logger.info("Seed with ID: {} found.", id);
      return ResponseEntity.ok().body(new SeedDto(seedService.getSeedByIdForLoggedUser(id)));
    }
    logger.warn("Seed with ID: {} not found.", id);
    throw new BadRequestException();
  }

  @Override
  @GetMapping("/")
  public ResponseEntity<?> getAll() {
    logger.info("Received request for all seeds.");
    List<SeedDto> seedsDtos = seedService.getAllSeedsForLoggedUser()
        .stream()
        .map(seed -> new SeedDto(seed))
        .toList();
    logger.info("Found list of seeds, converted to SeedDtos.");
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(seedsDtos);
  }


  @Override
  @PostMapping("/seeds")
  public ResponseEntity<?> saveSeed(@RequestParam("seed") Optional<String> seedContent,
                                    @RequestParam("file") Optional<MultipartFile> file) {

    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();


    if (file.isPresent() && dataPapyrusService.getFileSize(file) <= 25 * 1024 * 1024) {
      try {
        Seed originSeed =
            seedContent.isPresent() ? new Seed(seedContent.get()) : new Seed("JustForFile");
        DataPapyrus dp = dataPapyrusService.saveFile(file.get());
        originSeed.setUser(user);
        logger.info("Seed tied to logged-in user.");
        Seed originSeedRet = seedService.save(originSeed);
        logger.info("Seed saved.");
        dp.setSeed(originSeedRet);
        dataPapyrusService.save(dp);
        logger.info("File saved.");
        return ResponseEntity.status(201)
            .body(new SeedDto(seedService.getById(originSeedRet.getSeedId())));
      } catch (IOException e) {
        throw new HandleMaxUpLoadSizeExceededException();
      }
    }
    if (seedContent.isPresent()) {
      Seed originSeed = new Seed(seedContent.get());
      originSeed.setUser(user);
      logger.info("Seed tied to logged-in user.");
      Seed originSeedRet = seedService.save(originSeed);
      logger.info("Seed saved.");
      return ResponseEntity.ok().body(new SeedDto(seedService.getById(originSeedRet.getSeedId())));
    }
    throw new BadRequestException();
  }


  @Override
  @PutMapping("/seeds/{id}")
  public ResponseEntity<?> updateSeedContent(@PathVariable Long id,
                                             @RequestParam String newContent) {

    if (seedService.isPresent(id)) {
      Seed updatedSeed = seedService.getSeedByIdForLoggedUser(id);
      updatedSeed.setContent(newContent);
      logger.info("Updated seed content for ID {}: New content: {}", id, newContent);
      return ResponseEntity.status(HttpStatus.OK)
          .body(new SeedDto(seedService.save(updatedSeed)));
    }
    logger.warn("Seed with ID {} not found.", id);
    throw new SeedNotFoundException(id);
  }

  @Override
  @GetMapping("/fileseeds/{id}")
  public ResponseEntity<?> getFileSeed(@PathVariable Long id) {
    try {
      byte[] fileData = dataPapyrusService.getFileById(id);
      String contentType = dataPapyrusService.getContentType(id);
      Long seedIDofThisDP = dataPapyrusService.findSeedIdByDataPapyrus(id);
      if (Objects.equals(seedIDofThisDP,
          seedService.getSeedByIdForLoggedUser(seedIDofThisDP).getSeedId())) {
        logger.info("This user has seed with this file. Here is the file.");
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.valueOf(contentType))
            .body(fileData);
      }
    } catch (IOException e) {
      e.printStackTrace();
      logger.warn("Either there is no file, or the file is there,"
          + "but the user is not the owner of the file.");
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.notFound().build();
  }


  @Override
  @DeleteMapping("/fileseeds/{id}")
  public ResponseEntity<?> deleteFileSeedByFileID(@PathVariable Long id) {
    try {
      Long seedIdWithDP = dataPapyrusService.findSeedIdByDataPapyrus(id);
      Seed seed = seedService.getById(seedIdWithDP);
      if (Objects.equals(seedIdWithDP,
          seedService.getSeedByIdForLoggedUser(seedIdWithDP).getSeedId())) {
        dataPapyrusService.deleteFile(id);
      }
      if (seedService.doesntContainDataPapyrus(seedIdWithDP)
          && seedService.isJustForFile(seedIdWithDP)) {
        seedService.delete(seed.getSeedId());
        for (Tag tag : seed.getTags()) {
          tagService.deleteTagIfNotConnected(tag.getTagName());
        }
      }
      logger.info("File seed with ID {} deleted successfully.", id);
      return ResponseEntity.status(HttpStatus.OK).body(new SeedDto(seed));
    } catch (IOException exception) {
      exception.printStackTrace();
      logger.error("Exception occurred while deleting file seed with ID {}: {}", id,
          exception.getMessage());
      return ResponseEntity.badRequest().body("Something's gone wrong!");
    }
  }

  @Override
  @PostMapping("/fileseeds/{id}")
  public ResponseEntity<?> addFileToSeed(@PathVariable Long id,
                                         @RequestParam("file") Optional<MultipartFile> file) {
    try {
      DataPapyrus dp = dataPapyrusService.saveFile(file.get());
      if (Objects.equals(seedService.getSeedByIdForLoggedUser(id).getSeedId(), id)) {
        Seed originSeed = seedService.getById(id);
        dp.setSeed(originSeed);
        dataPapyrusService.save(dp);
        logger.info("File added to seed with ID {} successfully.", id);
        return ResponseEntity.status(201).body(new SeedDto(seedService.getById(id)));
      } else {
        throw new BadRequestException();
      }
    } catch (IOException ex) {
      logger.error("Exception occurred while adding file to seed with ID {}: {}", id,
          ex.getMessage());
      throw new BadRequestException();
    }
  }

  @Override
  @DeleteMapping(value = "/seeds/{id}")
  public ResponseEntity<?> deleteSeed(@PathVariable Long id) throws IOException {
    if (seedService.getSeedByIdForLoggedUser(id) == null) {
      logger.warn("Seed with ID {} not found.", id);
      throw new SeedNotFoundException(id);
    } else {
      Seed seedInput = seedService.getById(id);
      for (DataPapyrus dataPapyrus : seedInput.getDataPapyruses()) {
        dataPapyrusService.deleteFile(dataPapyrus.getFileId());
        seedService.save(seedInput);
      }
      Seed seed = seedService.delete(id);
      for (Tag tag : seed.getTags()) {
        tagService.deleteTagIfNotConnected(tag.getTagName());
      }
      logger.info("Seed with ID {} deleted successfully.", id);
      return ResponseEntity.ok().body(new SeedDto(seed));
    }
  }

  @Override
  @GetMapping(value = "/query")
  public ResponseEntity<?> findByTags(@RequestParam List<String> providedTags) {
    List<Seed> seeds = seedService.getAllSeedsForLoggedUser();
    List<SeedDto> outputSeeds = seedService.getAllSeedsContainingTheseTags(providedTags, seeds);
    if (!outputSeeds.isEmpty()) {

      logger.info("Seeds found by tags: {}", providedTags);
      return ResponseEntity.status(200).body(outputSeeds);
    } else {
      logger.warn("No seeds found for tags: {}", providedTags);
      throw new SeedNotFoundByTagsException();
    }
  }

  @Override
  @GetMapping(value = "/seeds/{id}/tags")
  public ResponseEntity<?> getTagsBySeedId(@PathVariable Long id) {
    if (seedService.isPresent(id)) {
      List<TagDto> tagNames = seedService.getAllTagNamesOfThisSeed(
          seedService.getSeedByIdForLoggedUser(id).getSeedId());
      logger.info("Tags found for seed with ID {}: {}", id, tagNames);
      return ResponseEntity.ok().body(tagNames);
    } else {
      logger.warn("Seed with ID {} not found.", id);
      throw new SeedNotFoundException(id);
    }
  }

  @Override
  @GetMapping("/tags")
  public ResponseEntity<?> getAllTags() {
    logger.info("Received request for all tags.");

    List<Seed> seedsOfLoggedUser = seedService.getAllSeedsForLoggedUser();
    List<Tag> allTags = new ArrayList<>();
    for (Seed seed : seedsOfLoggedUser) {
      List<Tag> tagsOfThisSeed = seed.getTags();
      for (Tag tag : tagsOfThisSeed) {
        if (!allTags.contains(tag)) {
          allTags.add(tag);
        }
      }
    }
    List<String> tagNames = allTags.stream().map(Tag::getTagName).collect(Collectors.toList());
    logger.info("Tags found: {}", tagNames);

    List<TagDto> tagsDtos = new ArrayList<>();
    for (Tag tag : allTags) {
      tagsDtos.add(new TagDto(tag));
    }
    return ResponseEntity.ok().body(tagsDtos);
  }

  @Override
  @PostMapping(value = "/seeds/{id}/tags")
  public ResponseEntity<?> addTagToSeed(@PathVariable Long id,
                                        @RequestParam Optional<String> tagInput) {
    logger.info("Received request to add tag {} to seed with ID {}.", tagInput, id);

    if (Objects.equals(seedService.getSeedByIdForLoggedUser(id).getSeedId(), id)
        && tagInput.isPresent()) {
      Tag tag = new Tag(tagInput.get());
      Seed seed = seedService.getById(id);
      Tag newTag = tagService.createNewTag(tag);
      seedService.addTagToSeed(seed, newTag);

      logger.info("Tag {} added to seed with ID {} successfully.", tag, id);
      return ResponseEntity.ok().body(new SeedDto(seed));
    }
    if (!(Objects.equals(seedService.getSeedByIdForLoggedUser(id).getSeedId(), id))) {
      logger.warn("Seed with ID {} not found.", id);
      throw new SeedNotFoundException(id);
    }
    logger.warn("Tag is not provided in the request body.");
    throw new BadRequestException();
  }

  @Override
  @PutMapping("/tagname")
  public ResponseEntity<?> updateTagName(@RequestParam Optional<String> oldTagName,
                                         @RequestParam Optional<String> newTagName) {

    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();

    if (oldTagName.isPresent() && newTagName.isPresent()) {
      oldTagName = Optional.of(user.getUserId() + "|" + oldTagName.get());
      newTagName = Optional.of(user.getUserId() + "|" + newTagName.get());

      String oldTag = oldTagName.get();
      String newTag = newTagName.get();

      if (tagService.isPresentTagByName(oldTag)) {
        tagService.updateTagName(oldTag, newTag);

        logger.info("Tag name updated successfully. Old Tag Name: '{}', New Tag Name: '{}'",
            oldTag,
            newTag);
        return ResponseEntity.ok().body(new TagDto(tagService.findByTagName(newTag)));
      } else {
        logger.warn("Tag name update failed.Tag with name '{}' not found.", oldTag);
        throw new BadRequestException();
      }
    } else {
      logger.warn("Both oldTagName and newTagName are required in the request.");
      throw new BadRequestException();
    }
  }

  @Override
  @DeleteMapping("/seeds/{id}/tags")
  public ResponseEntity<?> deleteTagInSeed(@PathVariable Long id, @RequestParam String tagName) {
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();
    tagName = user.getUserId() + "|" + tagName;
    if (Objects.equals(seedService.getSeedByIdForLoggedUser(id).getSeedId(), id)
        && tagService.findByTagName(tagName) != null) {
      Seed seed = seedService.removeTagFromSeed(id, tagService.findByTagName(tagName));
      tagService.deleteTagIfNotConnected(tagName);

      logger.info("Tag {} deleted from seed with ID {} successfully.", tagName, id);
      return ResponseEntity.ok().body(new SeedDto(seed));
    } else {
      if (!(Objects.equals(seedService.getSeedByIdForLoggedUser(id).getSeedId(), id))) {
        logger.warn("Seed with ID {} not found.", id);
        throw new SeedNotFoundException(id);
      } else {
        logger.warn("Tag with name {} not found.", tagName);
        throw new BadRequestException();
      }
    }
  }

  @Override
  @GetMapping("/fileseeds")
  public ResponseEntity<?> saveFileSeed() {
    logger.info("Received request for file seed.");
    throw new TeapotException();
  }
}