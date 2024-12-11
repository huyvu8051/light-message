package com.huyvu.lightmessage;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
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
            loopController.setLoops(10_000);
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

            // Build the Test Plan tree
            ListedHashTree testPlanTree = new ListedHashTree();
            ListedHashTree threadGroupTree = testPlanTree.add(testPlan);
            ListedHashTree samplerTree = threadGroupTree.add(threadGroup);
            samplerTree.add(httpSampler);

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
