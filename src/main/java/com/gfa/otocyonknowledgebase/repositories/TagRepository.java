package com.gfa.otocyonknowledgebase.repositories;

import com.gfa.otocyonknowledgebase.models.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {
  Tag findByTagName(String tagName);

}


