package com.deotis.digitalars.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TraceJourney{

	private Long wcseq;
	private Integer input_type;
	private String tracecode;

	@Builder
	public TraceJourney(long wcseq, int input_type, String tracecode)
	{
		this.wcseq = wcseq;
		this.input_type = input_type;
		this.tracecode = tracecode;
	}
}
