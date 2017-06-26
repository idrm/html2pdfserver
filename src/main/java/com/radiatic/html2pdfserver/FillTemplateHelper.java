package com.radiatic.html2pdfserver;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FillTemplateHelper extends PdfPageEventHelper {

	protected PdfReader reader;
	protected PdfTemplate background;

	public FillTemplateHelper (byte[] pdf) throws IOException, DocumentException {
		reader = new PdfReader(new ByteArrayInputStream(pdf));
	}

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		background = writer.getImportedPage(reader, 1);
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.addTemplate(background, 0, 0);
	}

}
