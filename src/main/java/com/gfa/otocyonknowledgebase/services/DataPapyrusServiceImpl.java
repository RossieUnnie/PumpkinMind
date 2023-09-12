package com.gfa.otocyonknowledgebase.services;

import com.gfa.otocyonknowledgebase.dto.DataPapyrusDto;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedFileNotFoundException;
import com.gfa.otocyonknowledgebase.models.DataPapyrus;
import com.gfa.otocyonknowledgebase.repositories.DataPapyrusRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DataPapyrusServiceImpl implements DataPapyrusService {
  private final DataPapyrusRepository dataPapyrusRepository;

  public DataPapyrusServiceImpl(DataPapyrusRepository dataPapyrusRepository) {
    this.dataPapyrusRepository = dataPapyrusRepository;
  }

  private final Path path = Paths.get("src/main/resources/static/storedFiles/");

  @Override
  public DataPapyrus saveFile(MultipartFile file) throws IOException {
    Path filePath = Paths.get(String.valueOf(path), file.getOriginalFilename() + " -- "
        + new SimpleDateFormat("ddMMyyyy-HHmmss").format(new Date()));
    try {
      file.transferTo(new File(filePath.toUri()));
      DataPapyrus papyrusToDb = new DataPapyrus();
      papyrusToDb.setFileName(file.getOriginalFilename() + " -- "
          + new SimpleDateFormat("dd.MM. yyyy - HH:mm:ss").format(new Date()));
      papyrusToDb.setType(file.getContentType());
      papyrusToDb.setFilepath(String.valueOf(filePath));
      return dataPapyrusRepository.save(papyrusToDb);
    } catch (IOException ioException) {
      throw ioException;
    }
  }

  @Override
  public byte[] getFileById(Long id) throws IOException {
    try {
      Optional<DataPapyrus> dataPapyrus = dataPapyrusRepository.findById(id);
      Path filePath = Path.of(dataPapyrus.get().getFilepath());
      return Files.readAllBytes(new File(filePath.toUri()).toPath());
    } catch (NoSuchElementException ex) {
      throw new SeedFileNotFoundException(id);
    }
  }

  @Override
  public String getContentType(Long id) {
    return dataPapyrusRepository.findById(id).get().getType();
  }

  @Override
  public DataPapyrusDto deleteFile(Long id) throws IOException {
    DataPapyrusDto dpReturn = new DataPapyrusDto(dataPapyrusRepository.findById(id).get());
    Files.deleteIfExists(Path.of(dataPapyrusRepository.findById(id).get().getFilepath()));
    dataPapyrusRepository.delete(dataPapyrusRepository.findById(id).get());
    return dpReturn;
  }

  @Override
  public DataPapyrus save(DataPapyrus dp) {
    return dataPapyrusRepository.save(dp);
  }

  @Override
  public Long findSeedIdByDataPapyrus(Long id) {
    try {
      return dataPapyrusRepository.findById(id).get().getSeed().getSeedId();
    } catch (NoSuchElementException ex) {
      throw new SeedFileNotFoundException(id);
    }
  }

  @Override
  public Long getFileSize(Optional<MultipartFile> file) {
    return file.get().getSize();
  }
}
