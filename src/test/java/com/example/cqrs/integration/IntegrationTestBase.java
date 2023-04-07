package com.example.cqrs.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTestBase {

	private static final GenericContainer mongo;
	private static final GenericContainer axonServer;
	private static final int MONGO_PORT = 29019;
	private static final int AXON_HTTP_PORT = 8024;
	private static final int AXON_GRPC_PORT = 8124;
	@LocalServerPort
	protected int randomServerPort;
	@Autowired
	protected TestRestTemplate testRestTemplate;

	static {
		// These containers should be started only once during whole test suite
		axonServer = new GenericContainer("axoniq/axonserver:latest")
				.withExposedPorts(AXON_HTTP_PORT, AXON_GRPC_PORT)
				.waitingFor(
						Wait.forLogMessage(".*Started AxonServer.*", 1)
				);
		axonServer.start();

		mongo = new GenericContainer("mongo:latest")
				.withExposedPorts(MONGO_PORT)
				.withEnv("MONGO_INITDB_DATABASE", "test")
				.withCommand(String.format("mongod --port %d", MONGO_PORT));
		mongo.start();

		System.setProperty("ENV_MONGO_PORT", String.valueOf(mongo.getMappedPort(MONGO_PORT)));
		System.setProperty("ENV_AXON_GRPC_PORT", String.valueOf(axonServer.getMappedPort(AXON_GRPC_PORT)));
	}
}
