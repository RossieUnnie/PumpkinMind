package com.gfa.otocyonknowledgebase.models;

import com.gfa.otocyonknowledgebase.security.models.User;
import jakarta.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seeds")
public class Seed {
  @Id
  @GeneratedValue
  @Column(name = "id_seed")
  private Long seedId;
  private String content;
  private String timestamp = new SimpleDateFormat("dd.MM. yyyy - HH:mm").format(new Date());

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "seed_tag",
      joinColumns = @JoinColumn(name = "seed_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))

  private List<Tag> tags = new ArrayList<>();


  @OneToMany(mappedBy = "seed", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<File> files = new ArrayList<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "seed", orphanRemoval = true, fetch = FetchType.EAGER)
  private List<DataPapyrus> dataPapyruses = new ArrayList<>();

  public Seed(String content) {
    this.content = content;
  }
}