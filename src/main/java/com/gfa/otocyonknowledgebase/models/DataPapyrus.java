package com.gfa.otocyonknowledgebase.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "files")
public class DataPapyrus {
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

  public DataPapyrus(Long fileId, String type, String fileName, String filepath, Seed seed) {
    this.fileId = fileId;
    this.type = type;
    this.fileName = fileName;
    this.filepath = filepath;
    this.seed = seed;
  }

  public DataPapyrus() {

  }

  public void setFileId(Long fileId) {
    this.fileId = fileId;
  }

  public Long getFileId() {
    return fileId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public Seed getSeed() {
    return seed;
  }

  public void setSeed(Seed seed) {
    this.seed = seed;
  }
}
