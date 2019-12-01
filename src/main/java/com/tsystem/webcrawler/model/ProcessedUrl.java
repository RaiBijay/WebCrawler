package com.tsystem.webcrawler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "process_url")
@Data
@NoArgsConstructor
public class ProcessedUrl {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	Long id;

	String url;
	Boolean processedStatus;

}
