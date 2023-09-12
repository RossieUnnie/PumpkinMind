package com.gfa.otocyonknowledgebase.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import com.gfa.otocyonknowledgebase.repositories.TagRepository;
import com.gfa.otocyonknowledgebase.security.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TagServiceImplTest {

  private TagServiceImpl tagService;

  private TagRepository tagRepository;

  private UserService userService;
  private SeedService seedService;

  @BeforeEach
  void setUp() {
    tagRepository = mock(TagRepository.class);
    seedService = mock(SeedService.class);
    userService = mock(UserService.class);
    tagService = new TagServiceImpl(tagRepository, seedService, userService);
  }

  @Test
  @DisplayName("Create new tag if not present")
  void updateTagName_IfNotPresent() {
    String newTagName = "NewTag";
    String oldTagName = "OldTag";
    Tag newTag = new Tag(newTagName);
    Tag oldTag = new Tag(oldTagName);

    when(tagRepository.findByTagName(oldTagName)).thenReturn(oldTag);
    when(tagRepository.findByTagName(newTagName)).thenReturn(newTag);
    when(tagRepository.save(newTag)).thenReturn(newTag);

    Tag result = tagService.updateTagName(oldTagName, newTagName);

    assertEquals(newTag, result);


  }

  @Test
  @DisplayName("Create new tag if already present")
  void updateTagName_IfAlreadyPresent() {
    String newTagName = "NewTag";
    String oldTagName = "OldTag";
    Tag newTag = new Tag(newTagName);
    Tag oldTag = new Tag(oldTagName);
    Tag originalTagToAdd = new Tag(newTagName);

    List<Seed> seedsId = new ArrayList<>();
    Seed testSeed1 = new Seed("test seed one");
    Seed testSeed2 = new Seed("test seed two");

    originalTagToAdd.getSeeds().add(testSeed1);
    originalTagToAdd.getSeeds().add(testSeed2);

    testSeed1.setSeedId(1L);
    testSeed2.setSeedId(2L);
    testSeed1.getTags().add(newTag);
    testSeed2.getTags().add(oldTag);
    seedsId.add(testSeed1);
    seedsId.add(testSeed2);

    when(tagRepository.findByTagName(oldTagName)).thenReturn(oldTag);
    when(tagRepository.findByTagName(newTagName)).thenReturn(newTag);
    when(seedService.getAllSeedsForLoggedUser()).thenReturn(seedsId);
    when(seedService.removeTagFromSeed(testSeed1.getSeedId(), oldTag)).thenReturn(testSeed1);
    when(seedService.removeTagFromSeed(testSeed2.getSeedId(), newTag)).thenReturn(testSeed2);
    when(seedService.getById(1L)).thenReturn(testSeed1);


    Tag result = tagService.updateTagName(oldTagName, newTagName);
    result.getSeeds().add(testSeed1);
    result.getSeeds().add(testSeed2);

    assertEquals(originalTagToAdd, result);
  }

  @Test
  void creatTag_IfAlreadyPresent() {

    String newTagName = "NewTag";
    Tag tag = new Tag(newTagName);

    when(tagRepository.findByTagName("0|" + newTagName)).thenReturn(tag);

    Tag result = tagService.createNewTag(tag);

    assertEquals(tag, result);
  }

  @Test
  void deleteTag() {

    String oldTagName = "OldTag";
    Tag tag = new Tag(oldTagName);

    when(tagRepository.findByTagName("0|" + oldTagName)).thenReturn(tag);

    Tag result = tagService.deleteTag(oldTagName);

    assertEquals(tag, result);
  }

  @Test
  void tagIsConnected() {

    String newTagName = "NewTag";
    Tag newTag = new Tag(newTagName);

    Seed testSeed1 = new Seed("test seed one");
    newTag.getSeeds().add(testSeed1);
    testSeed1.setSeedId(1L);
    testSeed1.getTags().add(newTag);

    when(tagRepository.findByTagName(newTagName)).thenReturn(newTag);

    boolean result = tagService.isConnected(newTagName);

    assertTrue(result);
  }

  @Test
  void deleteTagIfNotConnected_TagNotConnected_TagDeleted() {

    String newTagName = "NewTag";
    Tag newTag = new Tag(newTagName);

    when(tagRepository.findByTagName(newTagName)).thenReturn(newTag);

    TagService spyTagService = spy(tagService);

    spyTagService.deleteTagIfNotConnected(newTagName);

    verify(spyTagService, times(1)).deleteTag(newTagName);
  }


  @Test
  void getAllTags() {

    String newTagName = "0|NewTag";
    String oldTagName = "0|OldTag";
    Tag newTag = new Tag(newTagName);
    Tag oldTag = new Tag(oldTagName);

    List<Tag> dbReturn = new ArrayList<>();
    dbReturn.add(newTag);
    dbReturn.add(oldTag);

    when(tagRepository.findAll()).thenReturn(dbReturn);

    List<Tag> result = tagService.getAllTags();
    assertEquals(result, dbReturn);
  }

}