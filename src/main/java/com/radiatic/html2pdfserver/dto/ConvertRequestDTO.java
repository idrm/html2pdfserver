package com.radiatic.html2pdfserver.dto;

import javax.validation.constraints.NotNull;

public final class ConvertRequestDTO {

	@NotNull
	private String content;

	@NotNull
	private PageDTO page;

	private String stamper;

	private String css;

	private String background;

	private MetadataDTO metadata;

	public String getContent() {
		return content;
	}


	public String getCss() {
		return css;
	}

	public String getStamper() {
		return stamper;
	}

	public PageDTO getPage() {
		return page;
	}

	public String getBackground() {
		return background;
	}

	public MetadataDTO getMetadata() {
		return metadata;
	}
}
