package com.gfa.otocyonknowledgebase.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gfa.otocyonknowledgebase.security.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {

  @Column(unique = true)
  private String tagName;
  @Id
  @GeneratedValue
  @Column(name = "id_tag")
  private Long tagId;
  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags")
  private List<Seed> seeds = new ArrayList<>();

  public Tag(String tagName) {
    this.tagName = tagName;
  }

  @SuppressWarnings("checkstyle:MethodName")
  public void AddUserId(User user) {
    tagName = user.getUserId() + "|" + tagName;
  }

  public Long getUserId() {
    try {
      return Long.parseLong(tagName.split("\\|")[0]);
    } catch (Exception e) {
      return null;
    }

  }
}
