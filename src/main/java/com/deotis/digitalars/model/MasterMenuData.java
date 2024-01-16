package com.deotis.digitalars.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MasterMenuData{

	private String site_code;
	private Integer menu_seq;
	private Integer menu_code;
	private String tracecode;
	private Integer menu_seq_parent;
	private String menu_name;
	private String menu_url;
	private Float menu_depth;
	private String menu_number;
	private String etc_str;
	private String deep_link;
	private String icon_url;
	private Integer menu_type;
	private String queue_no;
	private Integer child_yn;
	private Integer max_menu_depth;
	private List<MasterMenuData> sub_menu;
	private int mb_seq;
	private int icon_type;
	private byte[] blob_data;
	private String ext;
}
