package com.radiatic.html2pdfserver.dto;

import javax.validation.constraints.NotNull;

public class RectangleDTO {

	@NotNull
	private float top;

	@NotNull
	private float right;

	@NotNull
	private float bottom;

	@NotNull
	private float left;

	public float getTop() {
		return top;
	}

	public float getRight() {
		return right;
	}

	public float getBottom() {
		return bottom;
	}

	public float getLeft() {
		return left;
	}
}
