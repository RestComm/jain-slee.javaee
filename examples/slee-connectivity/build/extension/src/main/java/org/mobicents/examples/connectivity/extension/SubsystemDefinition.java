package org.mobicents.examples.connectivity.extension;

import org.jboss.as.controller.*;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * @author <a href="mailto:tcerar@redhat.com">Tomaz Cerar</a>
 */
public class SubsystemDefinition extends SimpleResourceDefinition {
    public static final SubsystemDefinition INSTANCE = new SubsystemDefinition();

    protected static final SimpleAttributeDefinition REMOTE_RMI_ADDRESS =
            new SimpleAttributeDefinitionBuilder(SubsystemModel.REMOTE_RMI_ADDRESS, ModelType.STRING, false)
                    .setAllowExpression(true)
                    .setXmlName(SubsystemModel.REMOTE_RMI_ADDRESS)
                    .setDefaultValue(new ModelNode("localhost"))
                    .build();

    protected static final SimpleAttributeDefinition REMOTE_RMI_PORT =
            new SimpleAttributeDefinitionBuilder(SubsystemModel.REMOTE_RMI_PORT, ModelType.INT, false)
                    .setAllowExpression(true)
                    .setXmlName(SubsystemModel.REMOTE_RMI_PORT)
                    .setDefaultValue(new ModelNode(5555))
                    .build();

    static final AttributeDefinition[] ATTRIBUTES = {
            REMOTE_RMI_ADDRESS,
            REMOTE_RMI_PORT
    };

    private SubsystemDefinition() {
        super(ConnectivityExtension.SUBSYSTEM_PATH,
                ConnectivityExtension.getResourceDescriptionResolver(null),
                //We always need to add an 'add' operation
                SubsystemAdd.INSTANCE,
                //Every resource that is added, normally needs a remove operation
                SubsystemRemove.INSTANCE);
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
        //you can register aditional operations here
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        //you can register attributes here
        for (AttributeDefinition ad : ATTRIBUTES) {
            resourceRegistration.registerReadWriteAttribute(ad, null,
                    new ReloadRequiredWriteAttributeHandler(ad));
        }
    }
}
