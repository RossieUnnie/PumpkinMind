package com.gfa.otocyonknowledgebase.services;

import com.gfa.otocyonknowledgebase.models.Tag;
import java.util.List;
import java.util.Set;

public interface TagService {
  List<Tag> getAllTags();

  Tag save(Tag tag);

  Tag findByTagName(String tagName);

  Tag updateTagName(String oldTagName, String newTagName);

  boolean isPresentTagByName(String tagName);

  Tag createNewTag(Tag tag);

  boolean isConnected(String tagName);

  Tag deleteTag(String tagName);

  void deleteTagIfNotConnected(String tagName);

  Set<Long> removeTagsFromSeedsReturnIdSeeds(Tag oldTag, Tag newTag);
}
