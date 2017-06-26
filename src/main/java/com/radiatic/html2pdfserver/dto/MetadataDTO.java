package com.radiatic.html2pdfserver.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.radiatic.html2pdfserver.StringToDateTimeDeserializer;
import org.joda.time.DateTime;

public class MetadataDTO {

	private String producer;
	private String author;
	private String title;
	private String subject;
	private String keywords;
	private String creator;

	@JsonDeserialize(using = StringToDateTimeDeserializer.class)
	private DateTime creationDate;

	@JsonDeserialize(using = StringToDateTimeDeserializer.class)
	private DateTime modificationDate;

	public String getProducer() {
		return producer;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public String getSubject() {
		return subject;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getCreator() {
		return creator;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	public DateTime getModificationDate() {
		return modificationDate;
	}
}
