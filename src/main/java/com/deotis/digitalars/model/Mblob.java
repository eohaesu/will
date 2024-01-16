package com.deotis.digitalars.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Mblob {
  
  int mb_seq;
  byte[] blob_data;
  String ext;
  
}
