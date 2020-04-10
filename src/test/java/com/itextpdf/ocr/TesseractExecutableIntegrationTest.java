package com.itextpdf.ocr;

import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TesseractExecutableIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testIncorrectLanguages() {
        File file = new File(testImagesDirectory + "spanish_01.jpg");

        TesseractExecutableReader tesseractReader = new TesseractExecutableReader(
                getTesseractDirectory(), getTessDataDirectory());

        tesseractReader.setPathToExecutable(getTesseractDirectory());
        tesseractReader.setPathToScript(getPathToHocrScript());
        try {
            getTextFromPdf(tesseractReader, file, Arrays.<String>asList("spa", "spa_new", "spa_old"));
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "spa_new.traineddata",
                            langTessDataDirectory);
            Assert.assertEquals(expectedMsg, e.getMessage());
        }

        try {
            getTextFromPdf(tesseractReader, file, Collections.<String>singletonList("spa_new"));
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "spa_new.traineddata",
                            langTessDataDirectory);
            Assert.assertEquals(expectedMsg, e.getMessage());
        }

        Assert.assertEquals(getTesseractDirectory(),
                tesseractReader.getPathToExecutable());

        Assert.assertEquals(getPathToHocrScript(),
                 tesseractReader.getPathToScript());
    }

    @Test
    public void testIncorrectLanguagesScripts() {
        File file = new File(testImagesDirectory + "spanish_01.jpg");

        TesseractExecutableReader tesseractReader = new TesseractExecutableReader(
                getTesseractDirectory(), getTessDataDirectory());

        tesseractReader.setPathToScript(getPathToHocrScript());
        tesseractReader.setPathToTessData(scriptTessDataDirectory);
        try {
            getTextFromPdf(tesseractReader, file, Collections.<String>singletonList("English"));
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "English.traineddata",
                            scriptTessDataDirectory);
            Assert.assertEquals(expectedMsg, e.getMessage());
        }

        try {
            tesseractReader.setPathToTessData(scriptTessDataDirectory);
            getTextFromPdf(tesseractReader, file,
                    Arrays.<String>asList("Georgian", "Japanese", "English"));
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "English.traineddata",
                            scriptTessDataDirectory);
            Assert.assertEquals(expectedMsg, e.getMessage());
        }

        try {
            tesseractReader.setPathToTessData(scriptTessDataDirectory);
            getTextFromPdf(tesseractReader, file, new ArrayList<String>());
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "eng.traineddata", scriptTessDataDirectory);
            Assert.assertEquals(expectedMsg, e.getMessage());
            tesseractReader.setPathToTessData(getTessDataDirectory());
        }
        tesseractReader.setPathToTessData(getTessDataDirectory());

        Assert.assertEquals(getTesseractDirectory(),
                tesseractReader.getPathToExecutable());

        Assert.assertEquals(getPathToHocrScript(),
                tesseractReader.getPathToScript());
    }

    @Test
    public void testCorruptedImageAndCatchException() {
        try {
            File file = new File(testImagesDirectory
                    + "corrupted.jpg");
            TesseractExecutableReader tesseractReader = new TesseractExecutableReader(
                    getTesseractDirectory(), getTessDataDirectory());

            String realOutput = getTextFromPdf(tesseractReader, file);
            Assert.assertNotNull(realOutput);
            Assert.assertEquals("", realOutput);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.CANNOT_READ_INPUT_IMAGE,
                    e.getMessage());
        }
    }

    @Test
    public void testIncorrectPathToTessData() {
        File file = new File(testImagesDirectory + "spanish_01.jpg");
        TesseractExecutableReader tesseractReader = new TesseractExecutableReader(
                getTesseractDirectory(),
                "", Collections.<String>singletonList("eng"));

        try {
            Assert.assertEquals("", tesseractReader.getPathToTessData());
            getTextFromPdf(tesseractReader, file);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.CANNOT_FIND_PATH_TO_TESSDATA, e.getMessage());
        }
        tesseractReader.setPathToTessData(getTessDataDirectory());

        try {
            tesseractReader.setPathToTessData("/test");
            getTextFromPdf(tesseractReader, file, Collections.<String>singletonList("eng"));
        } catch (OCRException e) {
            String expectedMsg = MessageFormat
                    .format(OCRException.INCORRECT_LANGUAGE,
                            "eng.traineddata",
                            "/test");
            Assert.assertEquals(expectedMsg, e.getMessage());
        }
        tesseractReader.setPathToTessData(getTessDataDirectory());
    }

    @Test
    public void testIncorrectPathToTesseractExecutable() {
        File file = new File(testImagesDirectory + "spanish_01.jpg");

        try {
            getTextFromPdf(new TesseractExecutableReader(null,null), file);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.CANNOT_FIND_PATH_TO_TESSERACT_EXECUTABLE,
                    e.getMessage());
        }

        try {
            getTextFromPdf(new TesseractExecutableReader("", ""), file);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.CANNOT_FIND_PATH_TO_TESSERACT_EXECUTABLE,
                    e.getMessage());
        }
    }

    @Test
    public void testRunningTesseractCmd() {
        try {
            TesseractUtil.runCommand(Arrays.<String>asList("tesseract",
                    "random.jpg"), false);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.TESSERACT_FAILED, e.getMessage());
        }

        try {
            TesseractUtil.runCommand(null, false);
        } catch (OCRException e) {
            Assert.assertEquals(OCRException.TESSERACT_FAILED, e.getMessage());
        }
    }
}