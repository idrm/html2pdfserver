package com.radiatic.html2pdfserver.model;

public final class Page {

	private final float width;
	private final float height;
	private final Rectangle margin;

	public Page(float width, float height, Rectangle margin) {
		this.width = width;
		this.height = height;
		this.margin = margin;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public Rectangle getMargin() {
		return margin;
	}
}
