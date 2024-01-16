package com.deotis.digitalars.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MasterMenu{

	private String site_code;
	private Integer menu_code;
	private String menu_title;
	private Date update_date;
	private String dnis;
	private Integer member_yn;

}
