package com.huyvu.lightmessage;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.backend.BackendListener;
import org.apache.jorphan.collections.ListedHashTree;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;

public class JMeterTestPlanGeneratorTest {
    static Logger logger = LoggerFactory.getLogger(JMeterTestPlanGeneratorTest.class);

    @Disabled("Ignore during maven build")
    @Test
    void messageStressTest() {
        try {
            // Initialize JMeter environment
            JMeterUtils.loadJMeterProperties("src/test/resources/jmeter.properties");
            JMeterUtils.initLocale();

            // Create a Test Plan
            TestPlan testPlan = new TestPlan("Example Test Plan");

            // Create a Loop Controller
            LoopController loopController = new LoopController();
            loopController.setLoops(100_000);
            loopController.setFirst(true);
            loopController.initialize();

            // Create a Thread Group
            ThreadGroup threadGroup = new ThreadGroup();
            threadGroup.setName("Example Thread Group");
            threadGroup.setNumThreads(10); // 10 virtual users
            threadGroup.setRampUp(5); // Ramp-up time: 5 seconds
            threadGroup.setSamplerController(loopController);

            // Create an HTTP Sampler
            HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
            httpSampler.setDomain("localhost");
            httpSampler.setPort(8080);
            httpSampler.setPath("/api/v1/messages");
            httpSampler.setMethod("POST");
            httpSampler.addNonEncodedArgument("", "{\"convId\":1,\"content\":\"Hello, World!\"}", "application/json");
            httpSampler.setPostBodyRaw(true);
            httpSampler.setName("Send Message HTTP Request");

            // Create a Backend Listener for Flux (InfluxDB)
            BackendListener backendListener = new BackendListener();
            backendListener.setName("InfluxDB 2.x Backend Listener");
            backendListener.setClassname("io.github.mderevyankoaqa.influxdb2.visualizer.InfluxDatabaseBackendListenerClient");

            Arguments backendListenerProps = new Arguments();

            // Required properties for InfluxDB 2.x
            backendListenerProps.addArgument("influxDBURL", "http://localhost:8086"); // InfluxDB server URL
            backendListenerProps.addArgument("influxDBToken", "wsFnzuKBG6pu2d9ToRKo_33hRnUjdPVWZbutKLXM8GkBbG3xljcU4JROLWbQIJKxx4ft67UksMW1ct-i42c0EA=="); // Token for authentication
            backendListenerProps.addArgument("influxDBOrganization", "example-org");           // Your InfluxDB organization
            backendListenerProps.addArgument("influxDBBucket", "jmeter");            // Your InfluxDB bucket


            backendListenerProps.addArgument("testName", "jmeter");
            backendListenerProps.addArgument("nodeName", "jmeter");
            backendListenerProps.addArgument("runId", "6969");

            backendListenerProps.addArgument("influxDBMaxBatchSize", "2000");             // Data write interval in milliseconds
            backendListenerProps.addArgument("influxDBFlushInterval", "4000");             // Data write interval in milliseconds
            backendListenerProps.addArgument("responseBodyLength", "1000");             // Data write interval in milliseconds

            backendListener.setArguments(backendListenerProps);



            // Build the Test Plan tree
            ListedHashTree testPlanTree = new ListedHashTree();
            ListedHashTree threadGroupTree = testPlanTree.add(testPlan);
            ListedHashTree samplerTree = threadGroupTree.add(threadGroup);
            samplerTree.add(httpSampler);
            threadGroupTree.add(backendListener);

            // Save the Test Plan to a .jmx file
            SaveService.saveTree(testPlanTree, new FileOutputStream("target/example_test_plan.jmx"));
            System.out.println("Test Plan saved as example_test_plan.jmx");

            // Execute the Test Plan programmatically
            StandardJMeterEngine jmeterEngine = new StandardJMeterEngine();
            jmeterEngine.configure(testPlanTree);
            jmeterEngine.run();

            System.out.println("Test execution completed.");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
