package com.gfa.otocyonknowledgebase.repositories;

import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.security.models.User;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedRepository extends ListCrudRepository<Seed, Long> {
  List<Seed> findAllByUser(User user);
}
