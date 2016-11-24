package org.mobicents.examples.connectivity.extension;

import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.mobicents.example.slee.connection.SleeConnectionTest;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.slee.management.TraceMBean;
import java.util.LinkedList;

public class ConnectivityService implements Service<ConnectivityService> {

    private final Logger log = Logger.getLogger(ConnectivityService.class);

    public static ServiceName getServiceName() {
        return ServiceName.of("mobicents","connectivity");
    }

    private final LinkedList<String> registeredMBeans = new LinkedList<String>();
    private final InjectedValue<MBeanServer> mbeanServer = new InjectedValue<MBeanServer>();
    public InjectedValue<MBeanServer> getMbeanServer() {
        return mbeanServer;
    }

    @Override
    public ConnectivityService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
        log.info("Starting ConnectivityService");

        final SleeConnectionTest connectionTest = new SleeConnectionTest();
        registerMBean(connectionTest, SleeConnectionTest.OBJECT_NAME);
    }

    @Override
    public void stop(StopContext context) {
        log.info("Stopping ConnectivityService");
        unregisterMBean(SleeConnectionTest.OBJECT_NAME);
    }

    private void registerMBean(Object mBean, String name) throws StartException {
        try {
            getMbeanServer().getValue().registerMBean(mBean, new ObjectName(name));
        } catch (Throwable e) {
            throw new StartException(e);
        }
    }

    private void unregisterMBean(String name) {
        try {
            getMbeanServer().getValue().unregisterMBean(new ObjectName(name));
        } catch (Throwable e) {
            log.error("failed to unregister mbean", e);
        }
    }
}
