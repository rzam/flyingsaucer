package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.BaseFont;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ITextFontResolverTest {
    private final ITextFontResolver resolver = new ITextFontResolver();

    @Test
    void normalizeFontFamily() {
        assertThat(resolver.normalizeFontFamily("ArialUnicodeMS")).isEqualTo("ArialUnicodeMS");
        assertThat(resolver.normalizeFontFamily("\"ArialUnicodeMS")).isEqualTo("ArialUnicodeMS");
        assertThat(resolver.normalizeFontFamily("ArialUnicodeMS\"")).isEqualTo("ArialUnicodeMS");
        assertThat(resolver.normalizeFontFamily("\"ArialUnicodeMS\"")).isEqualTo("ArialUnicodeMS");
    }

    @Test
    void normalizeFontFamily_serif() {
        assertThat(resolver.normalizeFontFamily("serif")).isEqualTo("Serif");
        assertThat(resolver.normalizeFontFamily("SERIF")).isEqualTo("Serif");
        assertThat(resolver.normalizeFontFamily("sErIf")).isEqualTo("Serif");
    }

    @Test
    void normalizeFontFamily_sans_serif() {
        assertThat(resolver.normalizeFontFamily("sans-serif")).isEqualTo("SansSerif");
        assertThat(resolver.normalizeFontFamily("SANS-serif")).isEqualTo("SansSerif");
        assertThat(resolver.normalizeFontFamily("sans-SERIF")).isEqualTo("SansSerif");
        assertThat(resolver.normalizeFontFamily("\"sans-serif")).isEqualTo("SansSerif");
        assertThat(resolver.normalizeFontFamily("sans-serif\"")).isEqualTo("SansSerif");
        assertThat(resolver.normalizeFontFamily("\"sans-serif\"")).isEqualTo("SansSerif");
    }

    @Test
    void normalizeFontFamily_monospace() {
        assertThat(resolver.normalizeFontFamily("monospace")).isEqualTo("Monospaced");
        assertThat(resolver.normalizeFontFamily("MONOSPACE")).isEqualTo("Monospaced");
        assertThat(resolver.normalizeFontFamily("\"monospace\"")).isEqualTo("Monospaced");
    }

    @Test
    void loadFromMemory_ttf() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.ttf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.ttf", BaseFont.IDENTITY_H, true, data);
        assertFontIsIncludedInPDF(renderer, "Jacquard 24", "Jacquard");
    }

    @Test
    void loadFromMemory_ttf_override() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.ttf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.ttf", BaseFont.IDENTITY_H, true, data, "OverriddenName");
        assertFontIsIncludedInPDF(renderer, "OverriddenName", "Jacquard");
    }

    @Test
    void loadFromMemory_ttc() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.ttc");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, data);
        assertFontIsIncludedInPDF(renderer, "Jacquard 24", "Jacquard");
    }

    @Test
    void loadFromMemory_ttc_override() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.ttc");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, data, "OverriddenName");
        assertFontIsIncludedInPDF(renderer, "OverriddenName", "Jacquard");
    }

    @Test
    void loadFromMemory_otf() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.otf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.otf", BaseFont.IDENTITY_H, true, data);
        assertFontIsIncludedInPDF(renderer, "Jacquard 24", "Jacquard");
    }

    @Test
    void loadFromMemory_otf_override() throws IOException {
        byte[] data = loadResource("fonts/Jacquard24-Regular.otf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.otf", BaseFont.IDENTITY_H, true, data, "OverriddenName");
        assertFontIsIncludedInPDF(renderer, "OverriddenName", "Jacquard");
    }

    @Test
    void loadFromMemory_afm() throws IOException {
        byte[] afmData = loadResource("fonts/Jacquard24-Regular.afm");
        byte[] pfbData = loadResource("fonts/Jacquard24-Regular.pfb");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.afm", BaseFont.CP1252, true, afmData, pfbData);
        assertFontIsIncludedInPDF(renderer, "Jacquard 24", "Jacquard");
    }

    @Test
    void loadFromMemory_afm_override() throws IOException {
        byte[] afmData = loadResource("fonts/Jacquard24-Regular.afm");
        byte[] pfbData = loadResource("fonts/Jacquard24-Regular.pfb");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.afm", BaseFont.CP1252, true, afmData, pfbData, "OverriddenName");
        assertFontIsIncludedInPDF(renderer, "OverriddenName", "Jacquard");
    }

    @Test
    void loadFromMemory_pfm() throws IOException {
        byte[] pfmData = loadResource("fonts/Jacquard24-Regular.pfm");
        byte[] pfbData = loadResource("fonts/Jacquard24-Regular.pfb");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.pfm", BaseFont.CP1252, true, pfmData, pfbData);
        assertFontIsIncludedInPDF(renderer, "Jacquard 24", "Jacquard");
    }

    @Test
    void loadFromMemory_pfm_override() throws IOException {
        byte[] pfmData = loadResource("fonts/Jacquard24-Regular.pfm");
        byte[] pfbData = loadResource("fonts/Jacquard24-Regular.pfb");
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Jacquard24-Regular.pfm", BaseFont.CP1252, true, pfmData, pfbData, "OverriddenName");
        assertFontIsIncludedInPDF(renderer, "OverriddenName", "Jacquard");
    }

    // Utilities

    private byte[] loadResource(String path) throws IOException {
        URL resource = ITextFontResolver.class.getClassLoader().getResource(path);
        try (InputStream inputStream = resource.openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private void assertFontIsIncludedInPDF(ITextRenderer renderer,
                                           String fontNameToInclude,
                                           String fontNameToSearch) throws IOException {

        renderer.setDocumentFromString("<div style=\"font-family: '" + fontNameToInclude + "'\">Test</div>");
        renderer.layout();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.createPDF(outputStream, true);
        byte[] rendered = outputStream.toByteArray();

        Set<String> detectedFontNames = new HashSet<>();
        try (PDDocument document = Loader.loadPDF(rendered)) {
            PDResources resources = document.getPage(0).getResources();
            COSName fontName = resources.getFontNames().iterator().next();
            PDFont pdFont = resources.getFont(fontName);
            detectedFontNames.add(pdFont.getFontDescriptor().getFontName());
        }

        assertThat(detectedFontNames).anyMatch(fontName -> fontName.contains(fontNameToSearch));

    }

}
