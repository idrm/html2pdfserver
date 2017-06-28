package com.radiatic.html2pdfserver;

import com.itextpdf.text.FontFactory;
import com.radiatic.html2pdfserver.dto.ConvertRequestDTO;
import com.radiatic.html2pdfserver.model.Metadata;
import com.radiatic.html2pdfserver.model.Page;
import com.radiatic.html2pdfserver.model.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
public class Html2PdfController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Html2PdfConverter html2PdfConverter;

	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> convert(
		@Validated @RequestBody ConvertRequestDTO convertRequestDTO
	) {
		HttpHeaders headers = new HttpHeaders();

		try {
			byte[] pdfBytes = html2PdfConverter.convert(
				new Page(
					convertRequestDTO.getPage().getWidth(),
					convertRequestDTO.getPage().getHeight(),
					new Rectangle(
						convertRequestDTO.getPage().getMargin().getTop(),
						convertRequestDTO.getPage().getMargin().getRight(),
						convertRequestDTO.getPage().getMargin().getBottom(),
						convertRequestDTO.getPage().getMargin().getLeft()
					)
				),
				convertRequestDTO.getContent(),
				convertRequestDTO.getStamper(),
				convertRequestDTO.getCss(),
				convertRequestDTO.getBackground() != null ? Base64.getDecoder().decode(convertRequestDTO.getBackground()) : null,
				convertRequestDTO.getMetadata() != null ? new Metadata(
					convertRequestDTO.getMetadata().getProducer(),
					convertRequestDTO.getMetadata().getAuthor(),
					convertRequestDTO.getMetadata().getTitle(),
					convertRequestDTO.getMetadata().getSubject(),
					convertRequestDTO.getMetadata().getKeywords(),
					convertRequestDTO.getMetadata().getCreator(),
					convertRequestDTO.getMetadata().getCreationDate(),
					convertRequestDTO.getMetadata().getModificationDate()
				) : null
			);
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			headers.setContentLength(pdfBytes.length);
			ResponseEntity<InputStreamResource> response = new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(pdfBytes)), headers, HttpStatus.OK);
			return response;
		} catch (Exception ex) {
			headers.setContentType(MediaType.parseMediaType("text/plain; charset=UTF-8"));
			ResponseEntity<InputStreamResource> response = new ResponseEntity<>(new InputStreamResource(new ByteArrayInputStream(ex.getMessage().getBytes())), headers, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@RequestMapping(value = "/registered-fonts", method = RequestMethod.GET, produces = "application/json")
	public Collection<String> registeredFons() {
		return Stream.concat(FontFactory.getRegisteredFonts().stream(), html2PdfConverter.getRegisteredFonts().stream()).collect(toList());
	}
}
