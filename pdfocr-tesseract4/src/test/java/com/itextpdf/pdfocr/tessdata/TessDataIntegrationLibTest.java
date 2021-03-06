/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.pdfocr.tessdata;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfocr.PdfOcrLogMessageConstant;
import com.itextpdf.pdfocr.TextInfo;
import com.itextpdf.pdfocr.tesseract4.OutputFormat;
import com.itextpdf.pdfocr.tesseract4.Tesseract4OcrException;
import com.itextpdf.pdfocr.tesseract4.TesseractHelper;
import com.itextpdf.pdfocr.tesseract4.TextPositioning;
import com.itextpdf.pdfocr.tesseract4.Tesseract4OcrEngineProperties;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Category(IntegrationTest.class)
public class TessDataIntegrationLibTest extends TessDataIntegrationTest {
    public TessDataIntegrationLibTest() {
        super(ReaderType.LIB);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = Tesseract4OcrException.PATH_TO_TESS_DATA_DIRECTORY_CONTAINS_NON_ASCII_CHARACTERS)
    })
    @Test
    public void testTessDataWithNonAsciiPath() {
        junitExpectedException.expect(Tesseract4OcrException.class);
        junitExpectedException.expectMessage(
                Tesseract4OcrException.PATH_TO_TESS_DATA_DIRECTORY_CONTAINS_NON_ASCII_CHARACTERS
        );

        // Throws exception for the tesseract lib test
        doOcrAndGetTextUsingTessDataByNonAsciiPath();

        Assert.fail("Should throw exception for the tesseract lib when tess data path contains non ASCII characters");
    }

    @Test(timeout = 60000)
    public void textOutputFromHalftoneFile() {
        String imgPath = TEST_IMAGES_DIRECTORY + "halftone.jpg";
        String expected01 = "Silliness Enablers";
        String expected02 = "You dream it, we enable it";
        String expected03 = "QUANTITY";

        String result = getRecognizedTextFromTextFile(tesseractReader, imgPath,
                Collections.<String>singletonList("eng"));

        // correct result for a halftone input image
        Assert.assertTrue(result.contains(expected01));
        Assert.assertTrue(result.contains(expected02));
        Assert.assertTrue(result.contains(expected03));
    }

    @Test
    public void compareInvoiceFrontThaiImage() throws InterruptedException, java.io.IOException {
        String testName = "compareInvoiceFrontThaiImage";
        String filename = "invoice_front_thai";

        //Tesseract for Java and Tesseract for .NET give different output
        //So we cannot use one reference pdf file for them
        String expectedPdfPathJava = TEST_DOCUMENTS_DIRECTORY + filename + "_" + testFileTypeName + "_java.pdf";
        String expectedPdfPathDotNet = TEST_DOCUMENTS_DIRECTORY + filename + "_" + testFileTypeName + "_dotnet.pdf";

        String resultPdfPath = getTargetDirectory() + filename + "_" + testName + "_" + testFileTypeName + ".pdf";

        Tesseract4OcrEngineProperties properties =
                tesseractReader.getTesseract4OcrEngineProperties();
        properties.setTextPositioning(TextPositioning.BY_WORDS_AND_LINES);
        properties.setPathToTessData(getTessDataDirectory());
        properties.setLanguages(Arrays.asList("tha", "eng"));
        tesseractReader.setTesseract4OcrEngineProperties(properties);

        doOcrAndSavePdfToPath(tesseractReader,
                TEST_IMAGES_DIRECTORY + filename + ".jpg", resultPdfPath,
                Arrays.<String>asList("tha", "eng"), Arrays.<String>asList(NOTO_SANS_THAI_FONT_PATH, NOTO_SANS_FONT_PATH), DeviceRgb.RED);
        boolean javaTest = new CompareTool().compareByContent(resultPdfPath, expectedPdfPathJava,
                TEST_DOCUMENTS_DIRECTORY, "diff_") == null;
        boolean dotNetTest = new CompareTool().compareByContent(resultPdfPath, expectedPdfPathDotNet,
                TEST_DOCUMENTS_DIRECTORY, "diff_") == null;

        Assert.assertTrue(javaTest || dotNetTest);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfOcrLogMessageConstant.COULD_NOT_FIND_CORRESPONDING_GLYPH_TO_UNICODE_CHARACTER, count = 2)
    })
    @Test
    public void compareThaiTextImage() throws InterruptedException, java.io.IOException {
        String testName = "compareThaiTextImage";
        String filename = "thai_01";

        //Tesseract for Java and Tesseract for .NET give different output
        //So we cannot use one reference pdf file for them
        String expectedPdfPathJava = TEST_DOCUMENTS_DIRECTORY + filename + "_" + testFileTypeName + "_java.pdf";
        String expectedPdfPathDotNet = TEST_DOCUMENTS_DIRECTORY + filename + "_" + testFileTypeName + "_dotnet.pdf";

        String resultPdfPath = getTargetDirectory() + filename + "_" + testName + "_" + testFileTypeName + ".pdf";

        Tesseract4OcrEngineProperties properties =
                tesseractReader.getTesseract4OcrEngineProperties();
        properties.setTextPositioning(TextPositioning.BY_WORDS_AND_LINES);
        properties.setPathToTessData(getTessDataDirectory());
        properties.setLanguages(Arrays.asList("tha"));
        tesseractReader.setTesseract4OcrEngineProperties(properties);

        doOcrAndSavePdfToPath(tesseractReader,
                TEST_IMAGES_DIRECTORY + filename + ".jpg", resultPdfPath,
                Arrays.<String>asList("tha"), Arrays.<String>asList(NOTO_SANS_THAI_FONT_PATH), DeviceRgb.RED);
        boolean javaTest = new CompareTool().compareByContent(resultPdfPath, expectedPdfPathJava,
                TEST_DOCUMENTS_DIRECTORY, "diff_") == null;
        boolean dotNetTest = new CompareTool().compareByContent(resultPdfPath, expectedPdfPathDotNet,
                TEST_DOCUMENTS_DIRECTORY, "diff_") == null;

        Assert.assertTrue(javaTest || dotNetTest);
    }

}
