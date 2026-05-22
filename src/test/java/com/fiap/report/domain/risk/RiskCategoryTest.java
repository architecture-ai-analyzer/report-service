package com.fiap.report.domain.risk;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskCategoryTest {

    @Test
    void riskCategory_valuesContainExpectedCategories() {
        RiskCategory[] categories = RiskCategory.values();
        
        assertThat(categories).isNotEmpty();
        assertThat(categories).contains(RiskCategory.SECURITY);
        assertThat(categories).contains(RiskCategory.RELIABILITY);
        assertThat(categories).contains(RiskCategory.PERFORMANCE);
        assertThat(categories).contains(RiskCategory.SCALABILITY);
    }

    @Test
    void riskCategory_fromString_returnsCorrectCategory() {
        RiskCategory category = RiskCategory.valueOf("SECURITY");
        
        assertThat(category).isEqualTo(RiskCategory.SECURITY);
    }
}
