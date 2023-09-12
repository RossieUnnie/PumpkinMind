package com.gfa.otocyonknowledgebase.controllers;

import com.gfa.otocyonknowledgebase.services.DataPapyrusService;
import com.gfa.otocyonknowledgebase.services.SeedService;
import com.gfa.otocyonknowledgebase.services.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiController.class)
class ApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SeedService seedService;
  @MockBean
  private TagService tagService;
  @MockBean
  private DataPapyrusService dataPapyrusService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
}