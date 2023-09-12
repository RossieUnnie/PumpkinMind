package com.gfa.otocyonknowledgebase.services;

import com.gfa.otocyonknowledgebase.dto.SeedDto;
import com.gfa.otocyonknowledgebase.dto.TagDto;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedDoesntBelongToThisUserException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedNotFoundException;
import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import com.gfa.otocyonknowledgebase.repositories.SeedRepository;
import com.gfa.otocyonknowledgebase.security.models.Role;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.services.UserService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class SeedServiceImpl implements SeedService {
  private final SeedRepository seedRepository;

  private final UserService userService;
  static final Logger logger = LogManager.getLogger(SeedServiceImpl.class);


  @Override
  public List<Seed> getAllSeedsForLoggedUser() {
    logger.info("getAllSeedsForLoggedUser() called");
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();
    return seedRepository.findAllByUser(user);
  }

  @Override
  public Seed getSeedByIdForLoggedUser(Long id) {
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();

    Optional<Seed> seed = seedRepository.findById(id);
    if (seed.isEmpty()) {
      throw new SeedNotFoundException(id);
    }
    if (Objects.equals(seed.get().getUser().getUserId(), user.getUserId())) {
      return seed.get();
    } else {
      throw new SeedDoesntBelongToThisUserException(id);
    }
  }

  @Override
  public Seed delete(Long id) {
    Seed seed = seedRepository.findById(id).get();
    seedRepository.delete(seedRepository.findById(id).get());
    return seed;
  }

  @Override
  public Seed save(Seed seed) {
    return seedRepository.save(seed);
  }

  @Override
  public boolean isPresent(Long id) {
    return seedRepository.existsById(id);
  }

  @Override
  public Seed getById(Long id) {
    return seedRepository.findById(id).get();
  }

  @Override
  public void addTagToSeed(Seed seed, Tag tagIput) {
    boolean isPresent = false;
    List<Tag> seedTags = seed.getTags();
    for (Tag tag : seedTags) {
      if (Objects.equals(tag.getTagName(), tagIput.getTagName())) {
        isPresent = true;
        break;
      }
    }
    if (!isPresent) {
      seedTags.add(tagIput);
      seed.setTags(seedTags);
      seedRepository.save(seed);
    }
  }

  @Override
  public List<TagDto> getAllTagNamesOfThisSeed(Long seedId) {
    List<TagDto> tagNamesFromThisSeed = new ArrayList<>();
    Seed seed = seedRepository.findById(seedId).get();
    List<Tag> tagsFromThisSeed = seed.getTags();
    for (Tag tag : tagsFromThisSeed) {
      tagNamesFromThisSeed.add(new TagDto(tag));
    }
    return tagNamesFromThisSeed;
  }

  @Override
  public List<SeedDto> getAllSeedsContainingTheseTags(List<String> providedTags, List<Seed> seeds) {
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();
    List<String> providedTagsWithUserId = new ArrayList<>();
    List<SeedDto> output = new ArrayList<>();
    for (String name : providedTags) {
      providedTagsWithUserId.add(user.getUserId() + "|" + name);
    }

    for (Seed seed : seeds) {
      Set<String> seedTags = seed.getTags().stream()
          .map(Tag::getTagName)
          .collect(Collectors.toSet());
      if (seedTags.containsAll(providedTagsWithUserId)) {
        output.add(new SeedDto(seed));
      }
    }
    return output;
  }

  @Override
  public boolean isJustForFile(Long id) {
    return Objects.equals(getById(id).getContent(), "JustForFile");
  }

  @Override
  public boolean doesntContainDataPapyrus(Long id) {
    return getById(id).getDataPapyruses().isEmpty();
  }

  @Override
  public Seed removeTagFromSeed(Long seedId, Tag tagInput) {
    Seed seed = getById(seedId);
    if (seed != null) {
      List<Tag> tags = seed.getTags();
      for (Iterator<Tag> tagToRemove = tags.iterator(); tagToRemove.hasNext(); ) {
        Tag tag = tagToRemove.next();
        if (tag.getTagName().equals(tagInput.getTagName())) {
          tagToRemove.remove();
          break;
        }
      }
      seedRepository.save(seed);
    }
    return seed;
  }
}