package demo;

import com.google.auto.service.AutoService;
import org.opentest4j.reporting.tooling.spi.htmlreport.Contributor;
import org.opentest4j.reporting.tooling.spi.htmlreport.KeyValuePairs;
import org.opentest4j.reporting.tooling.spi.htmlreport.Labels;
import org.opentest4j.reporting.tooling.spi.htmlreport.PreFormattedOutput;
import org.opentest4j.reporting.tooling.spi.htmlreport.Section;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@AutoService(Contributor.class)
public class ConformanceContribution implements Contributor {

    @Override
    public List<Section> contributeSectionsForExecution(Context context) {
        List<Section> sections = new ArrayList<>();
        Element element = context.element();
        sections.add(Section.builder().title("Test Execution")
                .addBlock(KeyValuePairs.builder()
                        .putContent("testname", "testname")
                        .putContent("Variant", "key=value,key2=value2")
                        .build())
                .build());
        return sections;
    }

    @Override
    public List<Section> contributeSectionsForTestNode(Context context) {

        List<Section> sections = new ArrayList<>();

        Element element = context.element();
        switch (element.getTagName()) {
            case "h:root" -> {
                sections.add(Section.builder().title("Exported Values")
                        .addBlock(KeyValuePairs.builder()
                                .putContent("key1", "value1")
                                .putContent("key2", "value2")
                                .putContent("key3", "value3")
                                .build())
                        .build());
            }
            case "h:child" -> {

                String specref = element.getAttributeNS(Conformance.OIDF.getUri(), "specref");
                if (specref != null) {
                    var specReference = Section.builder()
                            .title("Spec Reference")
                            .metaInfo(specref)
                            .addBlock(Labels.builder()
                                    .addContent("SSF")
                                    .addContent(specref).build())
                            .build();
                    sections.add(specReference);
                }

                sections.add(Section.builder().title("Request Parameters")
                        .addBlock(KeyValuePairs.builder()
                                .putContent("key1", "value1")
                                .putContent("key2", "value2")
                                .putContent("key3", "value3")
                                .build())
                        .build());

                sections.add(Section.builder().title("RAW Request").addBlocks(
                        PreFormattedOutput.builder().content("""
                                HTTP GET /token HTTP/1.1
                                Authorization Basic djajdajkh
                                """).build()
                ).build());
            }
        }

        return sections;
    }
}
