package com.gfa.otocyonknowledgebase.services;


import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import com.gfa.otocyonknowledgebase.repositories.TagRepository;
import com.gfa.otocyonknowledgebase.security.models.Role;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.services.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
  private final TagRepository tagRepository;
  private final SeedService seedService;
  private final UserService userService;

  @Autowired
  public TagServiceImpl(TagRepository tagRepository, SeedService seedService,
                        UserService userService) {
    this.tagRepository = tagRepository;
    this.seedService = seedService;
    this.userService = userService;
  }

  @Override
  public Tag save(Tag tag) {
    return tagRepository.save(tag);
  }

  @Override
  public List<Tag> getAllTags() {
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();

    List<Tag> tags = tagRepository.findAll();
    List<Tag> output = new ArrayList<>();
    for (Tag tag : tags) {
      if (Objects.equals(tag.getUserId(), user.getUserId())) {
        output.add(tag);
      }
    }
    return output;
  }

  @Override
  public Tag findByTagName(String tagName) {
    return tagRepository.findByTagName(tagName);
  }

  @Override
  public Tag updateTagName(String oldTagName, String newTagName) {

    Tag oldTag = findByTagName(oldTagName);
    if (isPresentTagByName(newTagName)) {
      Tag newTag = findByTagName(newTagName);
      Set<Long> seedIdConnected = removeTagsFromSeedsReturnIdSeeds(oldTag, newTag);
      deleteTag(oldTagName);
      deleteTag(newTagName);
      Tag originalTagToAdd = new Tag(newTagName);
      save(originalTagToAdd);
      for (Long id : seedIdConnected) {
        seedService.addTagToSeed(seedService.getById(id), originalTagToAdd);
      }
    } else {
      oldTag.setTagName(newTagName);
      save(oldTag);
    }
    return findByTagName(newTagName);
  }

  @Override
  public Set<Long> removeTagsFromSeedsReturnIdSeeds(Tag oldTag, Tag newTag) {
    Set<Long> seedIdConnected = new HashSet<>();
    for (Seed seed : oldTag.getSeeds()) {
      seedIdConnected.add(seed.getSeedId());
    }
    for (Seed seed : newTag.getSeeds()) {
      seedIdConnected.add(seed.getSeedId());
    }
    for (Seed seed : seedService.getAllSeedsForLoggedUser()) {
      for (Tag tag : seed.getTags()) {
        if (Objects.equals(tag.getTagName(), oldTag.getTagName())) {
          seedService.removeTagFromSeed(seed.getSeedId(), tag);
        }
        if (Objects.equals(tag.getTagName(), newTag.getTagName())) {
          seedService.removeTagFromSeed(seed.getSeedId(), tag);
        }
      }
    }
    return seedIdConnected;
  }

  @Override
  public boolean isPresentTagByName(String tagName) {
    return !(findByTagName(tagName) == null);
  }

  @Override
  public Tag createNewTag(Tag tag) {
    User user = User.builder()
        .userId(userService.getCurrentUserId())
        .userName(userService.getCurrentUserName())
        .role(Role.USER)
        .build();

    tag.AddUserId(user);
    if (!(isPresentTagByName(tag.getTagName()))) {
      save(tag);
    }
    return findByTagName(tag.getTagName());
  }

  @Override
  public boolean isConnected(String tagName) {
    return !(findByTagName(tagName).getSeeds().size() == 0);
  }

  @Override
  public Tag deleteTag(String tagName) {
    Tag tagToReturn = new Tag(tagName);
    tagRepository.delete(findByTagName(tagName));
    return tagToReturn;
  }

  @Override
  public void deleteTagIfNotConnected(String tagName) {
    if (!(isConnected(tagName))) {
      deleteTag(tagName);
    }
  }
}
