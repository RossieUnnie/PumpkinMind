package com.gfa.otocyonknowledgebase.dto;

import com.gfa.otocyonknowledgebase.models.Tag;
import lombok.Data;

@Data
public class TagDto {
  private String tagName;

  public TagDto(Tag tag) {
    String s = tag.getTagName();
    this.tagName = s.substring(s.indexOf("|") + 1);
  }
}

