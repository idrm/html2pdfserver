package com.radiatic.html2pdfserver;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.radiatic.html2pdfserver.model.Metadata;
import com.radiatic.html2pdfserver.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class Html2PdfConverter implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${fontDirectory}")
	private String fontDirectory;

	private XMLWorkerFontProvider fontProvider;

	private java.util.List<String> registeredFonts;

	public Collection<String> getRegisteredFonts() {
		return registeredFonts;
	}

	public byte[] convert(Page page, String content, String stamperContent, String cssText, byte[] backgroundPdf, Metadata metadata) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(60000);

		Document document = new Document();
		document.setPageSize(new com.itextpdf.text.Rectangle(page.getWidth(), page.getHeight()));
		document.setMargins(
			page.getMargin().getLeft(),
			page.getMargin().getRight(),
			page.getMargin().getTop(),
			page.getMargin().getBottom()
		);

		PdfWriter writer = PdfWriter.getInstance(document, baos);

		if (backgroundPdf != null) {
			FillTemplateHelper template = new FillTemplateHelper(backgroundPdf);
			writer.setPageEvent(template);
		}

		document.open();

		CSSResolver cssResolver = new StyleAttrCSSResolver();

		if (cssText != null) {
			CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(cssText.getBytes()));
			cssResolver.addCss(cssFile);
		}

		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		htmlContext.setImageProvider(new Base64ImageProvider());

		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

		XMLWorker worker = new XMLWorker(css, true);
		XMLParser p = new XMLParser(worker);
		p.parse(new ByteArrayInputStream(content.getBytes()));

		document.close();
		byte[] pdfBytes = baos.toByteArray();

		if (stamperContent != null) {

			PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
			int numPages = reader.getNumberOfPages();

			ByteArrayOutputStream baos2 = new ByteArrayOutputStream(60000);

			PdfStamper stamper = new PdfStamper(reader, baos2);

			PdfContentByte pageContent;

			for (int i = 0; i < numPages; ) {
				pageContent = stamper.getOverContent(++i);

				ElementList elements = new ElementList();
				ElementHandlerPipeline end = new ElementHandlerPipeline(elements, null);
				html = new HtmlPipeline(htmlContext, end);
				css = new CssResolverPipeline(cssResolver, html);
				worker = new XMLWorker(css, true);
				p = new XMLParser(worker);

				try {
					p.parse(new ByteArrayInputStream(stamperContent.replaceAll("##TOTAL_PAGES##", "" + numPages).replaceAll("##CURRENT_PAGE##", "" + i).getBytes()), Charset.forName("UTF-8"));
				} catch (IOException ex) {
					ex.printStackTrace();
					break;
				}

				ColumnText ct = new ColumnText(pageContent);
				ct.setSimpleColumn(
					0,
					0,
					page.getWidth(),
					page.getHeight()
				);

				elements.forEach(ct::addElement);
				ct.go();
			}
			stamper.close();
			reader.close();

			pdfBytes = baos2.toByteArray();
		}

		if (metadata != null) {
			PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes));
			PDDocumentInformation info = doc.getDocumentInformation();

			if (metadata.getProducer() != null)
				info.setProducer(metadata.getProducer());

			if (metadata.getAuthor() != null)
				info.setAuthor(metadata.getAuthor());

			if (metadata.getTitle() != null)
				info.setTitle(metadata.getTitle());

			if (metadata.getSubject() != null)
				info.setSubject(metadata.getSubject());

			if (metadata.getKeywords() != null)
				info.setKeywords(metadata.getKeywords());

			if (metadata.getCreator() != null)
				info.setCreator(metadata.getCreator());

			if (metadata.getCreationDate() != null)
				info.setCreationDate(metadata.getCreationDate().toGregorianCalendar());

			if (metadata.getModificationDate() != null)
				info.setModificationDate(metadata.getModificationDate().toGregorianCalendar());

			ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
			doc.save(baos3);
			doc.close();
			pdfBytes = baos3.toByteArray();
		}

		return pdfBytes;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		registeredFonts = new ArrayList<>();
		fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);

		if (fontDirectory != null && fontDirectory.length() > 0) {
			log.info("Font directory=" + fontDirectory);
			for (File f : new File(fontDirectory).listFiles()) {
				if (f.getPath().endsWith(".ttf")) {
					String fontName = f.getName().substring(0, f.getName().lastIndexOf("."));
					registeredFonts.add(fontName);
					fontProvider.register(f.getPath(), fontName);
				}
			}
		}
	}
}
