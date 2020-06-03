package com.itextpdf.pdfocr;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.pdfocr.helpers.CustomOcrEngine;
import com.itextpdf.pdfocr.helpers.PdfHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfLayersTest extends ExtendedITextTest {

    @Test
    public void testPdfLayersWithDefaultNames() {
        String path = PdfHelper.getDefaultImagePath();
        File file = new File(path);

        OcrEngineProperties ocrEngineProperties = new OcrEngineProperties();
        ocrEngineProperties.setLanguages(
                Collections.<String>singletonList("eng"));
        CustomOcrEngine engine = new CustomOcrEngine(ocrEngineProperties);

        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(engine);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file),
                        PdfHelper.getPdfWriter());

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(0, layers.size());
        doc.close();

        Assert.assertEquals(engine, ocrPdfCreator.getOcrEngine());
        Assert.assertEquals(1, engine.getOcrEngineProperties().getLanguages().size());
        Assert.assertEquals("eng", engine.getOcrEngineProperties().getLanguages().get(0));
    }

    @Test
    public void testPdfLayersWithCustomNames() {
        String path = PdfHelper.getDefaultImagePath();
        File file = new File(path);

        OcrPdfCreatorProperties properties = new OcrPdfCreatorProperties();
        properties.setImageLayerName("name image 1");
        properties.setTextLayerName("name text 1");

        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new CustomOcrEngine(), properties);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file),
                        PdfHelper.getPdfWriter());

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(2, layers.size());
        Assert.assertEquals("name image 1",
                layers.get(0).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(0).isOn());
        Assert.assertEquals("name text 1",
                layers.get(1).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(1).isOn());

        doc.close();
    }

    @Test
    public void testTextFromPdfLayers() throws IOException {
        String testName = "testTextFromPdfLayers";
        String path = PdfHelper.getDefaultImagePath();
        String pdfPath = PdfHelper.getTargetDirectory() + testName + ".pdf";
        File file = new File(path);

        OcrPdfCreatorProperties properties = new OcrPdfCreatorProperties();
        properties.setImageLayerName("Image Layer");
        properties.setTextLayerName("Text Layer");
        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new CustomOcrEngine(), properties);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file), PdfHelper.getPdfWriter(pdfPath));

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(2, layers.size());
        Assert.assertEquals("Image Layer",
                layers.get(0).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(0).isOn());
        Assert.assertEquals("Text Layer",
                layers.get(1).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(1).isOn());

        doc.close();

        Assert.assertEquals(PdfHelper.DEFAULT_EXPECTED_RESULT,
                PdfHelper.getTextFromPdfLayer(pdfPath, "Text Layer"));
        Assert.assertEquals("",
                PdfHelper.getTextFromPdfLayer(pdfPath, "Image Layer"));
    }

    @Test
    public void testPdfLayersWithImageLayerOnly() {
        String path = PdfHelper.getDefaultImagePath();
        File file = new File(path);

        OcrPdfCreatorProperties properties = new OcrPdfCreatorProperties();
        properties.setImageLayerName("Image Layer");

        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new CustomOcrEngine(), properties);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file),
                        PdfHelper.getPdfWriter());

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(1, layers.size());
        Assert.assertEquals("Image Layer",
                layers.get(0).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(0).isOn());

        doc.close();
    }

    @Test
    public void testPdfLayersWithTextLayerOnly() {
        String path = PdfHelper.getDefaultImagePath();
        File file = new File(path);

        OcrPdfCreatorProperties properties = new OcrPdfCreatorProperties();
        properties.setTextLayerName("Text Layer");

        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new CustomOcrEngine(), properties);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file),
                        PdfHelper.getPdfWriter());

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(1, layers.size());
        Assert.assertEquals("Text Layer",
                layers.get(0).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(0).isOn());

        doc.close();
    }

    @Test
    public void testPdfLayersWithTextAndImageLayerWithTheSameName() {
        String path = PdfHelper.getDefaultImagePath();
        File file = new File(path);

        OcrPdfCreatorProperties properties = new OcrPdfCreatorProperties();
        properties.setTextLayerName("Mixed Layer");
        properties.setImageLayerName("Mixed Layer");

        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new CustomOcrEngine(), properties);
        PdfDocument doc =
                ocrPdfCreator.createPdf(Collections.<File>singletonList(file),
                        PdfHelper.getPdfWriter());

        Assert.assertNotNull(doc);
        List<PdfLayer> layers = doc.getCatalog()
                .getOCProperties(true).getLayers();

        Assert.assertEquals(1, layers.size());
        Assert.assertEquals("Mixed Layer",
                layers.get(0).getPdfObject().get(PdfName.Name).toString());
        Assert.assertTrue(layers.get(0).isOn());

        doc.close();
    }


}
