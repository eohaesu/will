package com.deotis.digitalars.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MasterBanner{

	private Integer b_seq;
	private String site_code;
	private String b_name;
	private String b_desc;
	private Date start_date;
	private Date end_date;
	private Integer applied_yn;
	private String url_link;
	private Integer orderby;
	private String tracecode;
	private String deep_link;
	private String etc_str;
	private String queue_no;
	private String dnis;
	private byte[] blob_data;
	private String ext;
}
