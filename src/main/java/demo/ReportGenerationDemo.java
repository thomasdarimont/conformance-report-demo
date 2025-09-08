package demo;

import org.opentest4j.reporting.events.api.DocumentWriter;
import org.opentest4j.reporting.events.api.NamespaceRegistry;
import org.opentest4j.reporting.events.core.CoreFactory;
import org.opentest4j.reporting.events.root.Events;
import org.opentest4j.reporting.schema.Namespace;
import org.opentest4j.reporting.schema.QualifiedName;
import org.opentest4j.reporting.tooling.core.converter.DefaultConverter;
import org.opentest4j.reporting.tooling.core.htmlreport.DefaultHtmlReportWriter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.opentest4j.reporting.events.core.CoreFactory.*;
import static org.opentest4j.reporting.events.core.Result.Status.SUCCESSFUL;
import static org.opentest4j.reporting.events.root.RootFactory.finished;
import static org.opentest4j.reporting.events.root.RootFactory.started;


public class ReportGenerationDemo {

    public static void main(String[] args) throws Exception {

        NamespaceRegistry namespaceRegistry = NamespaceRegistry //
                .builder(Namespace.REPORTING_CORE) //
                .add("e", Namespace.REPORTING_EVENTS) //
                .add("java", Namespace.REPORTING_JAVA) //
                .add("oidf", Conformance.OIDF) // custom namespace
                .build();

        Path eventsXmlFile = Paths.get("events.xml");

        // Generate test execution trace as events.xml file
        try (DocumentWriter<Events> writer = Events.createDocumentWriter(namespaceRegistry, eventsXmlFile)) {
            writer.append(infrastructure(), infrastructure -> infrastructure // (2)
                    .append(userName("alice")) //
                    .append(hostName("wonderland")));
            writer.append(started("1", Instant.now(), "container"), it -> {
                it.withAttribute(QualifiedName.of(Conformance.OIDF, "conformance-test"), "simple-test"); // custom attribute
            }); // (3)
            writer.append(started("2", Instant.now(), "test1"), started -> {
                started.withParentId("1");
                started.withAttribute(QualifiedName.of(Conformance.OIDF, "specref"), "OIDSSF-8.1.1");
            }); // (4)
            writer.append(finished("2", Instant.now()), finished -> finished.append(CoreFactory.result(SUCCESSFUL))); // (5)

            writer.append(started("3", Instant.now(), "test2"), started -> {
                started.withParentId("1");
                started.withAttribute(QualifiedName.of(Conformance.OIDF, "specref"), "OIDSSF-8.1.2");
            }); // (4)
            writer.append(finished("3", Instant.now()), finished -> finished.append(CoreFactory.result(SUCCESSFUL))); // (5)

            writer.append(finished("1", Instant.now()), finished -> finished.append(CoreFactory.result(SUCCESSFUL))); // (6)
        }

        // Generate hierarchical test file
        var hierarchyXmlFile = eventsXmlFile.resolveSibling("hierarchy.xml");
        var converter = new DefaultConverter();
        converter.convert(eventsXmlFile, hierarchyXmlFile);

        // Generate html report
        var firstXmlFile = hierarchyXmlFile;
        var sourceFileName = firstXmlFile.getFileName().getFileName().toString();
        var targetFileName = (sourceFileName.endsWith(".xml")
                ? sourceFileName.substring(0, sourceFileName.length() - 4)
                : sourceFileName) + ".html";
        var htmlReportFile = firstXmlFile.resolveSibling(targetFileName);
        new DefaultHtmlReportWriter().writeHtmlReport(List.of(hierarchyXmlFile), htmlReportFile);
    }
}
