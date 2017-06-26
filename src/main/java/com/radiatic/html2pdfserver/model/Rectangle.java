package com.radiatic.html2pdfserver.model;

public final class Rectangle {

	private final float top;
	private final float right;
	private final float bottom;
	private final float left;

	public Rectangle(float top, float right, float bottom, float left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

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
