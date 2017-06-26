package com.radiatic.html2pdfserver.dto;

import javax.validation.constraints.NotNull;

public class PageDTO {

	@NotNull
	private float width;

	@NotNull
	private float height;

	@NotNull
	private RectangleDTO margin;

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public RectangleDTO getMargin() {
		return margin;
	}
}
