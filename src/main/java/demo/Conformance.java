package demo;

import org.opentest4j.reporting.events.api.ChildElement;
import org.opentest4j.reporting.events.api.Context;
import org.opentest4j.reporting.events.core.Infrastructure;
import org.opentest4j.reporting.events.core.UserName;
import org.opentest4j.reporting.schema.Namespace;
import org.opentest4j.reporting.schema.QualifiedName;

public class Conformance {
    public static final Namespace OIDF = Namespace.of("https://www.openid.org/schemas/conformance/1.0.0");

    public static class Version extends ChildElement<Infrastructure, Version> {

        public static final QualifiedName ELEMENT = QualifiedName.of(OIDF, "version");

        /**
         * Create an instance with the supplied {@linkplain Context context} and {@linkplain QualifiedName qualified name}.
         *
         * @param context       the context of this instance
         */
        public Version(Context context, String version) {
            super(context, ELEMENT);
            withContent(version);
        }

    }
}
