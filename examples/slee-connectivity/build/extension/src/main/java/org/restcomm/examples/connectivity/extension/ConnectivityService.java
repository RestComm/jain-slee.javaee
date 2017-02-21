package org.restcomm.examples.connectivity.extension;

import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.restcomm.examples.connectivity.connection.SleeConnectionTest;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ConnectivityService implements Service<ConnectivityService> {

    private final Logger log = Logger.getLogger(ConnectivityService.class);

    public static ServiceName getServiceName() {
        return ServiceName.of("restcomm","slee-connectivity");
    }

    private final InjectedValue<MBeanServer> mbeanServer = new InjectedValue<MBeanServer>();
    public InjectedValue<MBeanServer> getMbeanServer() {
        return mbeanServer;
    }

    private String rmiAddress = "localhost";
    private int rmiPort = 5555;

    public ConnectivityService(String rmiAddress, int rmiPort) {
        this.rmiAddress = rmiAddress;
        this.rmiPort = rmiPort;
    }

    @Override
    public ConnectivityService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
        log.info("Starting ConnectivityService");

        final SleeConnectionTest connectionTest = new SleeConnectionTest(this.rmiAddress, this.rmiPort);
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
