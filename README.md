# Html2Pdf Server

This application runs as an HTTP server rendering PDF documents from HTML input.

The features include:

* customized page size and PDF metadata
* support for CSS stylesheets
* support for stamping of page numbers and other header / footer HTML elements on each page
* support for adding a background template from a supplied PDF to each page
* support for custom fonts
* embedding of raster and vector images

## Requirements

Java 8 or above

## Usage

Run `java -Dserver.por=8199 -jar html2pdfserver-0.0.2.jar --fontDirectory=/my/fonts` to start the app up.

`8199` is the HTTP port the application will run on, and `/my/fonts` is the directory
where custom fonts are located. You can skip the `--fontDirectory` option if you do not
plan to use custom fonts.

The command should be run from the directory where the application's `jar` file is located.

Alternately, you can prepend `html2pdfserver-0.0.2.jar` in the command with the directory 
where the `jar` file is located, e.g. `/some/directory/html2pdfserver-0.0.2.jar`.

Once the application has started up you can load `http://localhost:8199/registered-fonts` 
in a web browser to see a list of the fonts that you can use. This will include the standard PDF fonts together with
any TTF fonts you may have added in the custom font directory (if you have specified
that option).

To generate a PDF send a POST request to `http://localhost:8199/convert` with the following JSON as the body of the
request (make sure the `Content-Type` header of the request is set to `application/json`):

```json
{
  "page": {
    "width": 595.0,
    "height": 842.0,
    "margin": {
      "top": 40.0,
      "right": 40.0,
      "bottom": 40.0,
      "left": 40.0
    }
  },
  "content": "<html><body>...</body></html>",
  "stamper": "<table>...</table>",
  "css": ".someclass { color: red;} .anotherclass { padding-top: 10pt;}",
  "background": "BASE64_ENCODED_PDF_DOCUMENT",
  "metadata": {
    "producer": "Some Software",
    "author": "Jane Doe",
    "title": "My document title",
    "subject": "My document subject",
    "keywords": "My document keywords",
    "creator": "John Doe",
    "creationDate": "2017-06-25T10:13:50+00:00",
    "modificationDate": "2017-06-25T11:13:50+00:00"
  }
}
```

The page dimensions are specified in `pt`, which are 1/72th of an inch each. The demo JSON request above specifies an A4
page size, with 40pt of margin on all sides.

The `content` property is the actual HTML document that will be rendered. Keep in mind that the PDF renderer does not support all
HTML and CSS features, so you should test all scenarios you might encounter in terms of what you need to be rendered and confirm
that you're getting the expected results. The renderer will frequently behave very unlike how you'd expect a modern web browser
to render the content.

The `css` property contains the stylesheet CSS code. Not all CSS properties are supported, and not in all elements. The 
[iTextPDF CSS Conformance List](http://demo.itextsupport.com/xmlworker/itextdoc/CSS-conformance-list.htm) can help guide you
through what is and what isn't supported. The `css` property is optional.

The `stamper` property contains the HTML of a single element (it can be `table`, `div`, `p`, etc.) that will be rendered on
every page of the document. This is useful to have if you'd like to render a common header and footer on each page. It also
lets you stamp a page counter. If you put `##TOTAL_PAGES##` in the `stamper` HTML code it will be replaced by the total 
number of pages in the final render of the stamper on each page, and if you insert `##CURRENT_PAGE##` in the `stamper` that
will be replaced with the number of the page being rendered. You may use either placeholder as many times as you want in
the `stamper` HTML code. The `stamper` property is optional.

The `background` property is a Base64-encoded string of a PDF document that will be inserted as a background on each
page of the rendered PDF. It should consist of a single page and have the same page size as the rendered PDF. The `background`
property is optional.

The `metadata` property serves to specify several of the basic metadata fields in the rendered PDF. The `metadata` object property
and its constituents properties (`author`, `creationDate`, etc.) are all optional. When specified, the creation and modification date properties
should be entered in the ISO 8601 format.

You can insert images by using the `data` nomenclature in an `<img src="..."/>` block. For example, to insert a PNG image, the code
would look like `<img src="data:image/png;base64,XYZ" />`, where `XYZ` is the Base64-encoded text of the PNG image source. Alongside
PNG you can also use JPG and TIFF images (together with the corresponding MIME type). If you'd like to insert a vector image 
you'll have to convert it to the WMF format, and use `image/wmf` as the MIME type.

### Example

To see the converter in action follow these steps:

* open a terminal / command line prompt window (note that you may have issues with the Windows PowerShell, so you should
try with `cmd.exe` instead)
* `cd` into the `sample` directory of the source
* run `java -Dserver.port=8199 -jar ..\dist\html2pdfserver-0.0.2.jar --fontDirectory=.`
This should start the HTTP server up.
* open a new terminal / command line prompt window
* `cd` into the `sample` directory of the the source
* run `curl -X POST -H "Content-Type: application/json" --data @sample.json http://localhost:8199/convert -o mytest.pdf`
This should generate a `mytest.pdf` file that is the same as the `result.pdf` file already present in the `sample`
directory.

The generated PDF will contain an olive-colored oval on each page which comes from the background PDF in the request. It also
demonstrates usage of custom fonts (the Ubuntu fonts in the `sample` directory), embedding of vector and raster images, as well as
the stamping of each page with a header, a footer, and a page counter.


### As a Windows service

One option, if you plan to run this application as a Windows service, is [NSSM](https://nssm.cc/). Check [this
post](http://giordanomaestro.blogspot.bg/2013/01/running-java-applications-as-windows.html) out for some tips.

## Security

The HTTP server that is launched by the application does not implement any kind of authentication or
authorization scheme. As a basic precaution, you should add an IP whitelist rule to your firewall software to prevent
unauthorized access.

This application does not verify or sanitize the input data it receives (HTML, CSS, and PDF data), and the
author does not guarantee these inputs cannot be used as attack vectors to breach the server this application
runs on.

## Licensing Concerns

The following legal aspects of using this application are the opinion of the author of this application. 

Since the author is not a lawyer, nor has a lawyer been consulted on this matter, you are advised
to consult one on your own if you want to get a valid legal opinion.

### iTextPDF

The code of this application is released under the Apache 2.0 license. 

At the same time, it has a compile-time dependency on the [iTextPDF](http://itextpdf.com) (version 5) library. iTextPDF is used 
under the Affero GPL license, which requires that this application and any derivatives of it are also 
released as open source. 

Consequently, you may not distribute the `jar` file of this application (which contains the iTextPDF jar files) 
with your own software, unless that software is also open source. You may get around this limitation by 
having your software run a script that downloads and configures this application during the setup process. 

Because client applications communicate with this application via HTTP they are not encumbered by this
application's licensing and distribution restrictions.

### Custom Fonts

When using custom fonts, parts of those fonts will be embedded in the generated PDF. Confirm with each font's
license that that is permitted.

## Disclaimer

Per the Apache 2.0 License, the author shall not be held liable for any damages incurred while using this
application. Use at your own risk.