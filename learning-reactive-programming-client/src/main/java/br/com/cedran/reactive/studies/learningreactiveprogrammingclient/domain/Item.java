package br.com.cedran.reactive.studies.learningreactiveprogrammingclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

  private String id;
  private String description;
  private Double price;

}
