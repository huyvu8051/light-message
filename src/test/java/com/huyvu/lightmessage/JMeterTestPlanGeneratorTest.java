package com.huyvu.lightmessage;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;

public class JMeterTestPlanGeneratorTest {

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
            loopController.setLoops(1); // Run 1 iteration
            loopController.setFirst(true); // Start immediately
            loopController.initialize();

            // Create a Thread Group
            ThreadGroup threadGroup = new ThreadGroup();
            threadGroup.setName("Example Thread Group");
            threadGroup.setNumThreads(10); // 10 virtual users
            threadGroup.setRampUp(5); // Ramp-up time: 5 seconds
            threadGroup.setSamplerController(loopController);

            // Create an HTTP Sampler
            HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
            httpSampler.setDomain("jsonplaceholder.typicode.com");
            httpSampler.setPath("/posts/1");
            httpSampler.setMethod("GET");
            httpSampler.setName("Example HTTP Request");

            // Build the Test Plan tree
            HashTree testPlanTree = new HashTree();
            HashTree threadGroupTree = testPlanTree.add(testPlan);
            HashTree samplerTree = threadGroupTree.add(threadGroup);
            samplerTree.add(httpSampler);

            // Save the Test Plan to a .jmx file
            SaveService.saveTree(testPlanTree, new FileOutputStream("target/example_test_plan.jmx"));
            System.out.println("Test Plan saved as example_test_plan.jmx");

            jmeter.configure(testPlanTree);
            jmeter.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
