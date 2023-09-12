package com.gfa.otocyonknowledgebase.dto;

import com.gfa.otocyonknowledgebase.models.DataPapyrus;
import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeedDto {
  private final Long seedId;
  private final String content;
  private final List<TagDto> taglings;
  private final List<DataPapyrusDto> dataPapyruslings;

  public SeedDto(Seed seed) {
    this.seedId = seed.getSeedId();
    this.content = seed.getContent();
    this.taglings = tagsToTagsDto(seed.getTags());
    this.dataPapyruslings = dataPapyrusToDataPapyrusDto(seed.getDataPapyruses());
  }

  public List<TagDto> tagsToTagsDto(List<Tag> tags) {
    List<TagDto> tagsDtos = new ArrayList<>();
    for (Tag tag : tags) {
      tagsDtos.add(new TagDto(tag));
    }
    return tagsDtos;
  }

  public List<DataPapyrusDto> dataPapyrusToDataPapyrusDto(List<DataPapyrus> dataPapyrusesInput) {
    List<DataPapyrusDto> dpDto = new ArrayList<>();
    for (DataPapyrus dataPapyrus : dataPapyrusesInput) {
      dpDto.add(new DataPapyrusDto(dataPapyrus));
    }
    return dpDto;
  }

  public List<DataPapyrusDto> getDataPapyruslings() {
    return dataPapyruslings;
  }
}
