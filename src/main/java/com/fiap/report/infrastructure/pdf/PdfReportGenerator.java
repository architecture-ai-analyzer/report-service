package com.fiap.report.infrastructure.pdf;

import com.fiap.report.domain.report.AnalysisReport;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PdfReportGenerator {

    public byte[] generateReportPdf(AnalysisReport report) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = createFont(16, Font.BOLD);
            Font headerFont = createFont(12, Font.BOLD);
            Font bodyFont = createFont(11, Font.NORMAL);

            document.add(createParagraph(getReportTitle(report), titleFont, Element.ALIGN_LEFT));
            document.add(createParagraph("Upload ID: " + report.getDiagramId(), bodyFont, Element.ALIGN_LEFT));
            document.add(createParagraph("Data de geracao: " + report.getGeneratedAt(), bodyFont, Element.ALIGN_LEFT));
            document.add(createParagraph("Status: " + (report.getStatus() != null ? report.getStatus().getDisplayName() : "Desconhecido"), bodyFont, Element.ALIGN_LEFT));
            document.add(createParagraph("", bodyFont, Element.ALIGN_LEFT));

            TemplateType templateType = determineTemplateType(report);
            switch (templateType) {
                case EXECUTIVE:
                    addExecutiveSection(report, document, headerFont, bodyFont);
                    break;
                case SECURITY:
                    addSecuritySection(report, document, headerFont, bodyFont);
                    break;
                case TECHNICAL:
                default:
                    addTechnicalSection(report, document, headerFont, bodyFont);
                    break;
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for report {}", report.getDiagramId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                baos.close();
            } catch (IOException ignored) {
            }
        }
    }

    private TemplateType determineTemplateType(AnalysisReport report) {
        String templateId = report.getTemplateId();
        if (templateId == null) {
            return TemplateType.TECHNICAL;
        }
        String normalized = templateId.toLowerCase();
        if (normalized.contains("executivo") || normalized.contains("executive")) {
            return TemplateType.EXECUTIVE;
        }
        if (normalized.contains("seguranca") || normalized.contains("security") || normalized.contains("pci")) {
            return TemplateType.SECURITY;
        }
        return TemplateType.TECHNICAL;
    }

    private void addExecutiveSection(AnalysisReport report, Document document, Font headerFont, Font bodyFont) {
        addSectionTitle(document, "Resumo Executivo", headerFont);
        addLine(document, "Total de Componentes Mapeados: " + (report.getComponents() != null ? report.getComponents().size() : 0), bodyFont);

        long criticalRisks = report.getRisks() != null ? report.getRisks().stream()
                .filter(r -> "CRITICAL".equalsIgnoreCase(r.getLevel().name()))
                .count() : 0;
        long highRisks = report.getRisks() != null ? report.getRisks().stream()
                .filter(r -> "HIGH".equalsIgnoreCase(r.getLevel().name()))
                .count() : 0;

        addLine(document, "Nível de Risco Geral: " + (criticalRisks > 0 ? "CRÍTICO (Requer Atenção Imediata)" : "CONTROLADO"), bodyFont);
        addLine(document, "Total de Riscos Severos (Crítico/Alto): " + (criticalRisks + highRisks), bodyFont);
        addLine(document, "", bodyFont);

        addSectionTitle(document, "Principais Impactos no Negócio", headerFont);
        if (report.getRisks() != null && !report.getRisks().isEmpty() && (criticalRisks > 0 || highRisks > 0)) {
            report.getRisks().stream()
                    .filter(r -> "CRITICAL".equalsIgnoreCase(r.getLevel().name()) || "HIGH".equalsIgnoreCase(r.getLevel().name()))
                    .limit(3)
                    .forEach(risk -> addLine(document, "- " + risk.getImpact(), bodyFont));
        } else {
            addLine(document, "- Nenhum risco com impacto crítico ao negócio foi identificado.", bodyFont);
            addLine(document, "- Recomendação: Arquitetura liberada para próximas fases.", bodyFont);
        }
        addLine(document, "", bodyFont);
    }

    private void addSecuritySection(AnalysisReport report, Document document, Font headerFont, Font bodyFont) {
        addSectionTitle(document, "Status de Conformidade", headerFont);
        addLine(document, "Template Aplicado: Regras de Segurança Estrita", bodyFont);

        boolean hasCritical = report.getRisks() != null && report.getRisks().stream()
                .anyMatch(r -> "CRITICAL".equalsIgnoreCase(r.getLevel().name()));
        addLine(document, "Avaliação de Vulnerabilidade: " + (hasCritical ? "CRÍTICO" : "ALTO"), bodyFont);
        addLine(document, "", bodyFont);

        addSectionTitle(document, "Vulnerabilidades Detectadas", headerFont);
        if (report.getRisks() != null && !report.getRisks().isEmpty()) {
            report.getRisks().stream()
                    .filter(r -> "CRITICAL".equalsIgnoreCase(r.getLevel().name()) || "HIGH".equalsIgnoreCase(r.getLevel().name()))
                    .forEach(risk -> addLine(document, "- [" + risk.getLevel().name() + "] " + risk.getDescription(), bodyFont));
        } else {
            addLine(document, "- Nenhum risco crítico ou alto detectado.", bodyFont);
        }
        addLine(document, "", bodyFont);

        addSectionTitle(document, "Plano de Ação Recomendado (SecOps)", headerFont);
        if (report.getRisks() != null) {
            report.getRisks().stream()
                    .filter(r -> r.getMitigation() != null && !r.getMitigation().isEmpty())
                    .flatMap(r -> r.getMitigation().stream())
                    .distinct()
                    .limit(4)
                    .forEach(mitigation -> addLine(document, "- " + mitigation, bodyFont));
        }
        addLine(document, "", bodyFont);
    }

    private void addTechnicalSection(AnalysisReport report, Document document, Font headerFont, Font bodyFont) {
        addSectionTitle(document, "Inventário de Componentes Mapeados", headerFont);
        if (report.getComponents() != null && !report.getComponents().isEmpty()) {
            report.getComponents().forEach(comp -> addLine(document, "- " + comp.getName() + " (" + comp.getType().name() + ")", bodyFont));
        } else {
            addLine(document, "- Nenhum componente identificado no diagrama.", bodyFont);
        }
        addLine(document, "", bodyFont);

        addSectionTitle(document, "Riscos de Arquitetura Identificados", headerFont);
        if (report.getRisks() != null && !report.getRisks().isEmpty()) {
            report.getRisks().stream()
                    .limit(3)
                    .forEach(risk -> addLine(document, "- " + risk.getDescription() + " (Impacto: " + risk.getImpact() + ")", bodyFont));
        } else {
            addLine(document, "- Nenhum risco arquitetural listado.", bodyFont);
        }
        addLine(document, "", bodyFont);

        addSectionTitle(document, "Recomendações de Engenharia", headerFont);
        if (report.getRecommendations() != null && !report.getRecommendations().isEmpty()) {
            report.getRecommendations().stream()
                    .limit(3)
                    .forEach(rec -> {
                        Paragraph item = new Paragraph(
                                "- " + rec.getDescription() + " [Prioridade: " + rec.getPriority().name() + "]",
                                bodyFont);
                        item.setFirstLineIndent(12);
                        item.setIndentationLeft(12);
                        item.setSpacingBefore(0);
                        item.setSpacingAfter(0);
                        item.setLeading(0, 1f);
                        document.add(item);
                    });
        } else {
            addLine(document, "- Nenhuma recomendação gerada para esta estrutura.", bodyFont);
        }
    }

    private Paragraph createParagraph(String text, Font font, int alignment) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(alignment);
        paragraph.setSpacingAfter(8);
        return paragraph;
    }

    private void addSectionTitle(Document document, String title, Font font) {
        try {
            document.add(createParagraph(title, font, Element.ALIGN_LEFT));
        } catch (Exception e) {
            throw new RuntimeException("Failed to add section title to PDF", e);
        }
    }

    private void addLine(Document document, String text, Font font) {
        try {
            Paragraph line = new Paragraph(text, font);
            line.setFirstLineIndent(12);
            line.setSpacingAfter(4);
            document.add(line);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add line to PDF", e);
        }
    }

    private Font createFont(float size, int style) {
        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF font", e);
        }
    }

    private String getReportTitle(AnalysisReport report) {
        String templateId = report.getTemplateId();
        return "Relatório de Arquitetura - " +
                (templateId != null && !templateId.isBlank() ? templateId : "Técnico");
    }

    private enum TemplateType {
        EXECUTIVE,
        SECURITY,
        TECHNICAL
    }
}
