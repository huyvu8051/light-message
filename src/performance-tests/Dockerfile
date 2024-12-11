# Use official JMeter image
FROM justb4/jmeter:latest

# Copy the JMeter test plan into the container
COPY ./jmeter-scripts/my-test-plan.jmx /test-plan.jmx

# Run JMeter with the test plan
CMD ["jmeter", "-n", "-t", "/test-plan.jmx", "-l", "/results/result.jtl"]
