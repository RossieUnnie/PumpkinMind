package com.gfa.otocyonknowledgebase.services;

import com.gfa.otocyonknowledgebase.dto.SeedDto;
import com.gfa.otocyonknowledgebase.dto.TagDto;
import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import java.util.List;

public interface SeedService {

  Seed delete(Long id);

  Seed save(Seed seed);


  boolean isPresent(Long id);

  Seed getById(Long id);

  void addTagToSeed(Seed seed, Tag tag);

  boolean doesntContainDataPapyrus(Long id);

  Seed removeTagFromSeed(Long seedId, Tag tag);


  List<TagDto> getAllTagNamesOfThisSeed(Long seedId);

  List<SeedDto> getAllSeedsContainingTheseTags(List<String> providedTags, List<Seed> seeds);

  boolean isJustForFile(Long id);

  List<Seed> getAllSeedsForLoggedUser();

  Seed getSeedByIdForLoggedUser(Long id);
}