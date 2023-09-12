package com.gfa.otocyonknowledgebase.services;

import com.gfa.otocyonknowledgebase.dto.DataPapyrusDto;
import com.gfa.otocyonknowledgebase.models.DataPapyrus;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface DataPapyrusService {
  DataPapyrus saveFile(MultipartFile file) throws IOException;

  byte[] getFileById(Long id) throws IOException;

  String getContentType(Long id);

  DataPapyrusDto deleteFile(Long id) throws IOException;

  DataPapyrus save(DataPapyrus dp);

  Long findSeedIdByDataPapyrus(Long id);

  Long getFileSize(Optional<MultipartFile> file);
}
