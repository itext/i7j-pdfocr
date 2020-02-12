package com.itextpdf.ocr;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tesseract Library Reader class.
 * (extends TesseractReader class)
 * <p>
 * This class provides possibilities to use features of "tesseract"
 * (optical character recognition engine for various operating systems)
 * <p>
 * This class provides possibility to perform OCR, read data from input files
 * and return contained text in the described format
 * <p>
 * This class provides possibilities to set type of current os,
 * required languages for OCR for input images,
 * set path to directory with tess data.
 */
public class TesseractLibReader extends TesseractReader {

    /**
     * TesseractExecutableReader logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TesseractLibReader.class);

    /**
     * Tesseract Instance.
     * (depends on OS type)
     */
    private ITesseract tesseractInstance;

    /**
     * TesseractLibReader constructor with path to tess data directory.
     */
    public TesseractLibReader(String tessDataPath) {
        setOsType(identifyOSType());
        setTesseractInstance();
        setPathToTessData(tessDataPath);
    }

    /**
     * TesseractLibReader constructor with path to tess data directory,
     * list of languages and path to tess data directory.
     *
     * @param languagesList List<String>
     * @param tessDataPath  String
     */
    public TesseractLibReader(final String tessDataPath,
            final List<String> languagesList) {
        setOsType(identifyOSType());
        setTesseractInstance();
        setPathToTessData(tessDataPath);
        setLanguages(Collections.unmodifiableList(languagesList));
    }

    /**
     * Set tesseract instance depending on the OS type.
     */
    public void setTesseractInstance() {
        if (isWindows()) {
            tesseractInstance = new Tesseract1();
        } else {
            tesseractInstance = new Tesseract();
        }
    }

    /**
     * Get tesseract instance depending on the OS type.
     *
     * @return ITesseract
     */
    public ITesseract getTesseractInstance() {
        return tesseractInstance;
    }

    /**
     * Perform tesseract OCR.
     *
     * @param inputImage - input image file
     * @param outputFile - output file
     */
    public void doTesseractOcr(final File inputImage,
            final File outputFile) {
        getTesseractInstance().setDatapath(getTessData());
        getTesseractInstance()
                .setTessVariable("tessedit_create_hocr", "1");

        if (!getLanguages().isEmpty()) {
            getTesseractInstance()
                    .setLanguage(String.join("+", getLanguages()));
        }
        if (getPageSegMode() != null) {
            getTesseractInstance()
                    .setPageSegMode(getPageSegMode());
        }

        validateLanguages();

        String result = null;
        try {
            BufferedImage preprocessed = null;
            // preprocess if required
            if (isPreprocessingImages()) {
                BufferedImage original = null;
                try {
                    original = ImageIO.read(inputImage);
                    if (original != null) {
                        preprocessed = ImageUtil
                                .preprocessImage(inputImage.getAbsolutePath(), original);
                    } else {
                        preprocessed = ImageUtil
                                .preprocessImage(inputImage.getAbsolutePath());
                    }
                } catch (IOException e) {
                    LOGGER.info("Cannot read from file: " + e.getLocalizedMessage());
                    preprocessed = ImageUtil
                            .preprocessImage(inputImage.getAbsolutePath());
                }
            }
            if (preprocessed == null || !isPreprocessingImages()) {
                result = getTesseractInstance().doOCR(inputImage);
            } else {
                result = getTesseractInstance().doOCR(preprocessed);
            }
        } catch (TesseractException | IOException e) {
            LOGGER.error("OCR failed: " + e.getLocalizedMessage());
            throw new OCRException(OCRException.TESSERACT_FAILED_WITH_REASON)
                    .setMessageParams("OCR failed");
        }

        try (BufferedWriter writer = Files
                .newBufferedWriter(outputFile.toPath())) {
            writer.write(result);
        } catch (IOException e) {
            LOGGER.error("Cannot write to file: " + e.getLocalizedMessage());
            throw new OCRException(OCRException.TESSERACT_FAILED_WITH_REASON)
                    .setMessageParams("Cannot write to file "
                            + outputFile.getAbsolutePath());
        }
    }
}
