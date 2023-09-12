package com.gfa.otocyonknowledgebase.models;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class File {
  @Id
  @GeneratedValue
  @Column(name = "file_id")
  private Long fileId;

  private String type;

  private String fileName;
  private String filepath;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "seed_id")
  private Seed seed;

  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }

  public Long getFileId() {
    return fileId;
  }
}
