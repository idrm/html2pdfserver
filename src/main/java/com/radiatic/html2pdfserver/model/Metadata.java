package com.radiatic.html2pdfserver.model;

import org.joda.time.DateTime;

public class Metadata {

	private final String producer;
	private final String author;
	private final String title;
	private final String subject;
	private final String keywords;
	private final String creator;
	private final DateTime creationDate;
	private final DateTime modificationDate;

	public Metadata(String producer, String author, String title, String subject, String keywords, String creator, DateTime creationDate, DateTime modificationDate) {
		this.producer = producer;
		this.author = author;
		this.title = title;
		this.subject = subject;
		this.keywords = keywords;
		this.creator = creator;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

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
