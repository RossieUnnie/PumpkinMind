package com.gfa.otocyonknowledgebase.models;

import jakarta.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_seeds")
public class FileSeed {
  @Id
  @GeneratedValue
  @Column(name = "file_seed_id")
  private Long fileSeedId;
  private String name;
  private String type;
  private String filePath;
  private String timestamp = new SimpleDateFormat("dd.MM. yyyy - HH:mm").format(new Date());
}
