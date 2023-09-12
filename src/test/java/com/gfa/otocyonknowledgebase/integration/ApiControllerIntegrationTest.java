package com.gfa.otocyonknowledgebase.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gfa.otocyonknowledgebase.models.DataPapyrus;
import com.gfa.otocyonknowledgebase.models.Seed;
import com.gfa.otocyonknowledgebase.models.Tag;
import com.gfa.otocyonknowledgebase.repositories.DataPapyrusRepository;
import com.gfa.otocyonknowledgebase.repositories.SeedRepository;
import com.gfa.otocyonknowledgebase.repositories.TagRepository;
import com.gfa.otocyonknowledgebase.security.models.Role;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.repositories.UserRepository;
import com.gfa.otocyonknowledgebase.services.DataPapyrusService;
import com.gfa.otocyonknowledgebase.services.SeedService;
import com.gfa.otocyonknowledgebase.services.TagService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApiControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SeedService seedService;

  @Autowired
  private TagService tagService;

  @Autowired
  private SeedRepository seedRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private DataPapyrusService dataPapyrusService;

  @Autowired
  private DataPapyrusRepository dataPapyrusRepository;

  @Autowired
  private UserRepository userRepository;


  @BeforeEach
  void setUp() {
    seedRepository.deleteAll();
    tagRepository.deleteAll();
    Seed testSeed1 = new Seed("I am test seed one");
    Seed testSeed2 = new Seed("I am test seed two");
    Seed testSeed3 = new Seed("I am test seed three");

    Tag testTag1 = new Tag("1|Book");
    Tag testTag2 = new Tag("1|Technology");
    Tag testTag3 = new Tag("1|Food");
    Tag testTag4 = new Tag("1|Family");

    DataPapyrus dataPapyrus1 = new DataPapyrus(1L, "Type1", "File1", "/path/to/file1", null);
    DataPapyrus dataPapyrus2 = new DataPapyrus(2L, "Type2", "File2", "/path/to/file2", null);

    User user = User.builder()
        .userId(1L)
        .userName("Pepa")
        .email("john@example.com")
        .password("hashedPassword")
        .role(Role.USER)
        .build();
    userRepository.save(user);

    testSeed1.setUser(user);
    testSeed2.setUser(user);
    testSeed3.setUser(user);

    tagService.save(testTag1);
    tagService.save(testTag2);
    tagService.save(testTag3);
    tagService.save(testTag4);

    testSeed1.getTags().add(testTag1);
    testSeed1.getTags().add(testTag2);
    testSeed1.getTags().add(testTag3);
    testSeed1.getTags().add(testTag4);

    testSeed1.getDataPapyruses().add(dataPapyrus1);
    testSeed2.getDataPapyruses().add(dataPapyrus2);

    testSeed2.getTags().add(testTag3);

    testSeed3.getTags().add(testTag1);
    testSeed3.getTags().add(testTag2);

    seedService.save(testSeed1);
    seedService.save(testSeed2);
    seedService.save(testSeed3);

    dataPapyrus1.setSeed(testSeed1);
    dataPapyrus2.setSeed(testSeed2);

    dataPapyrusService.save(dataPapyrus1);
    dataPapyrusService.save(dataPapyrus2);
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get All Seeds")
  void getAllSeeds() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(status().isOk())
        .andDo(mvcResult -> System.out.println(
            ">>>>>>>>>> " + mvcResult.getResponse().getContentAsString() + " <<<<<<<<<<"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].content", is("I am test seed one")))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].dataPapyruslings.[0].fileName", is("File1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].content", is("I am test seed two")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].taglings[0].tagName", is("Food")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].content", is("I am test seed three")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[3].taglings[3]").doesNotExist());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get All Seeds connected by selected tags we have")
  void getSeedsByTagsName() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/query").with(csrf())
            .param("providedTags", "Book")
            .param("providedTags", "Technology"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].content", is("I am test seed one")))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].dataPapyruslings.[0].fileName", is("File1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].content", is("I am test seed three")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].content").doesNotExist());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get All Seeds connected by selected tags we don't have")
  void getSeedsByTagsNameNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/query").with(csrf())
            .param("providedTags", "cookie")
            .param("providedTags", "Technology"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string("No seed found for the given tags."));
  }


  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Delete seed, seed is not present")
  void deleteSeedNotPresent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/seeds/5").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string("Seed not found for ID: 5"));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get All Tags")
  public void testGetAllTags() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/tags").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].tagName", is("Book")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].tagName", is("Technology")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].tagName", is("Food")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[3].tagName", is("Family")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[4].tagName").doesNotExist());

  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Update tag name")
  public void testUpdateTagName_Success() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.put("/tagname")
            .param("oldTagName", "Book")
            .param("newTagName", "Journal"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value("Journal"));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Update tag name, tag name is not present")
  public void testUpdateTagName_TagNotFound() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.put("/tagname").with(csrf())
            .param("oldTagName", "Magazine")
            .param("newTagName", "Journal"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Update tag name, missing params")
  void testUpdateTagName_MissingParams() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/tagname").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get Seed by ID")
  void testGetSeedById_ExistingSeedId() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seeds/1").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content", is("I am test seed one")))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.dataPapyruslings.[0].fileName", is("File1")));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get Seed by ID, seed is not present")
  void testGetSeedById_NotExistingSeedId() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seeds/5").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Update Seed Content")
  void testUpdateSeedContent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/seeds/1").with(csrf())
            .param("newContent", "I am test seed one updated"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content", is("I am test seed one updated")));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Update Seed Content, seed not found")
  void testUpdateSeedContent_SeedNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/seeds/5").with(csrf())
            .param("newContent", "I am test seed one updated"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Save Seed with Seed Content Only")
  void testSaveSeedWithContentOnly() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/seeds").with(csrf())
            .param("seed", "I am test seed four"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content", is("I am test seed four")));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get all tags of seed by seed id when seed exist")
  void getAllTagsOfSeedBySeedId_seedExist() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seeds/1/tags").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].tagName", is("Book")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].tagName", is("Technology")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].tagName", is("Food")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[3].tagName", is("Family")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[4].tagName").doesNotExist());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Get all tags of seed by seed id when seed do not exist")
  void getAllTagsOfSeedBySeedId_seedDoNotExist() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seeds/10/tags").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string("Seed not found for ID: 10"));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Add tag to seed, seed exist")
  void addTagToSeed_seedExist() throws Exception {
    String tagNameInput = "Pumpička";

    mockMvc.perform(MockMvcRequestBuilders.post("/seeds/1/tags").with(csrf())
            .param("tagInput", tagNameInput))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[0].tagName", is("Book")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[1].tagName", is("Technology")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[2].tagName", is("Food")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[3].tagName", is("Family")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[4].tagName", is("Pumpička")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taglings.[5].tagName").doesNotExist());
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Add tag to seed, seed not exist")
  void addTagToSeed_seedNotExist() throws Exception {
    String tagNameInput = "Pumpička";

    mockMvc.perform(MockMvcRequestBuilders.post("/seeds/10/tags").with(csrf())
            .param("tagInput", tagNameInput))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string("Seed not found for ID: 10"));
  }

  @Test
  @WithMockUser(username = "Pepa")
  @DisplayName("Add tag to seed, no tag name")
  void addTagToSeed_noTagName() throws Exception {
    String tagNameInput = "Pumpička";

    mockMvc.perform(MockMvcRequestBuilders.post("/seeds/1/tags").with(csrf()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }


}