package com.brewpoint.pos.report;

import com.brewpoint.pos.report.util.JasperFontBootstrap;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractReportTest {
    @BeforeAll
    public static void initFonts() {
        JasperFontBootstrap.ensureInitialized();
    }
}
