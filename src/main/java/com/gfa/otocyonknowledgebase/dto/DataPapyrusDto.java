package com.gfa.otocyonknowledgebase.dto;

import com.gfa.otocyonknowledgebase.models.DataPapyrus;

public class DataPapyrusDto {

  private Long fileId;
  private String type;
  private String fileName;

  public DataPapyrusDto(DataPapyrus dataPapyrus) {
    this.fileId = dataPapyrus.getFileId();
    this.type = dataPapyrus.getType();
    this.fileName = dataPapyrus.getFileName();
  }

  public Long getFileId() {
    return fileId;
  }

  public void setFileId(Long fileId) {
    this.fileId = fileId;
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
}
