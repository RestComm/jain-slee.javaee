package org.mobicents.examples.connectivity.extension;

import org.jboss.as.controller.*;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.EnumSet;
import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;


/**
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ConnectivityExtension implements Extension {

    /**
     * The name space used for the {@code substystem} element
     */
    public static final String NAMESPACE = "urn:mobicents:connectivity:1.0";

    /**
     * The name of our subsystem within the model.
     */
    public static final String SUBSYSTEM_NAME = "connectivity";

    /**
     * The parser used for parsing our subsystem
     */
    private final SubsystemParser parser = new SubsystemParser();

    protected static final PathElement SUBSYSTEM_PATH = PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME);
    private static final String RESOURCE_NAME = ConnectivityExtension.class.getPackage().getName() + ".LocalDescriptions";

    static StandardResourceDescriptionResolver getResourceDescriptionResolver(final String keyPrefix) {
        String prefix = SUBSYSTEM_NAME + (keyPrefix == null ? "" : "." + keyPrefix);
        return new StandardResourceDescriptionResolver(prefix, RESOURCE_NAME, ConnectivityExtension.class.getClassLoader(), true, false);
    }

    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, NAMESPACE, parser);
    }

    @Override
    public void initialize(ExtensionContext context) {
        final SubsystemRegistration subsystem = context.registerSubsystem(SUBSYSTEM_NAME, 1, 0);
        final ManagementResourceRegistration registration = subsystem.registerSubsystemModel(SubsystemDefinition.INSTANCE);

        final OperationDefinition describeOp = new SimpleOperationDefinitionBuilder(DESCRIBE,
                getResourceDescriptionResolver(null))
                .setEntryType(OperationEntry.EntryType.PRIVATE)
                .build();
        registration.registerOperationHandler(describeOp, GenericSubsystemDescribeHandler.INSTANCE, false);

        subsystem.registerXMLElementWriter(parser);
    }

    /**
     * The subsystem parser, which uses stax to read and write to and from xml
     */
    private static class SubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>, XMLElementWriter<SubsystemMarshallingContext> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context) throws XMLStreamException {
            context.startSubsystemElement(ConnectivityExtension.NAMESPACE, false);

            final ModelNode javaeeSubsystem = context.getModelNode();
            SubsystemDefinition.REMOTE_RMI_ADDRESS.marshallAsElement(javaeeSubsystem, writer);
            SubsystemDefinition.REMOTE_RMI_PORT.marshallAsElement(javaeeSubsystem, writer);

            writer.writeEndElement();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
            ParseUtils.requireNoAttributes(reader);

            final ModelNode address = new ModelNode();
            address.add(SUBSYSTEM, ConnectivityExtension.SUBSYSTEM_NAME);
            address.protect();

            final ModelNode subsystem = new ModelNode();
            subsystem.get(OP).set(ADD);
            subsystem.get(OP_ADDR).set(address);
            list.add(subsystem);

            // elements
            final EnumSet<Element> encountered = EnumSet.noneOf(Element.class);
            while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
                final Element element = Element.forName(reader.getLocalName());
                if (!encountered.add(element)) {
                    throw ParseUtils.unexpectedElement(reader);
                }
                switch (element) {
                    case REMOTE_RMI_ADDRESS: {
                        final String value = parseRemoteRmiAddress(reader);
                        SubsystemDefinition.REMOTE_RMI_ADDRESS.parseAndSetParameter(value, subsystem, reader);
                        break;
                    }
                    case REMOTE_RMI_PORT: {
                        final String value = parseRemoteRmiPort(reader);
                        SubsystemDefinition.REMOTE_RMI_PORT.parseAndSetParameter(value, subsystem, reader);
                        break;
                    }
                    default: {
                        throw ParseUtils.unexpectedElement(reader);
                    }
                }
            }
        }
    }

    static String parseRemoteRmiAddress(XMLExtendedStreamReader reader) throws XMLStreamException {
        // we don't expect any attributes for this element.
        ParseUtils.requireNoAttributes(reader);

        final String value = reader.getElementText();
        if (value == null || value.trim().isEmpty()) {
            throw new XMLStreamException(
                    "Invalid value: " + value + " for '" + Element.REMOTE_RMI_ADDRESS.getLocalName() + "' element",
                    reader.getLocation());
        }
        return value.trim();
    }

    static String parseRemoteRmiPort(XMLExtendedStreamReader reader) throws XMLStreamException {
        // we don't expect any attributes for this element.
        ParseUtils.requireNoAttributes(reader);

        final String value = reader.getElementText();
        if (value == null || value.trim().isEmpty()) {
            throw new XMLStreamException(
                    "Invalid value: " + value + " for '" + Element.REMOTE_RMI_PORT.getLocalName() + "' element",
                    reader.getLocation());
        }
        return value.trim();
    }

}
