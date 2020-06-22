package de.fhg.aisec.ids

import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.support.jsse.KeyManagersParameters
import org.apache.camel.support.jsse.KeyStoreParameters
import org.apache.camel.support.jsse.SSLContextParameters
import org.apache.camel.support.jsse.TrustManagersParameters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
open class ExampleConfiguration {

    @Bean("sslContext")
    open fun createSSLContext(): SSLContextParameters {
        val ctx = SSLContextParameters()
        ctx.certAlias = "1.0.1"
        ctx.keyManagers = KeyManagersParameters()
        ctx.keyManagers.keyStore = KeyStoreParameters()
        ctx.keyManagers.keyStore.resource = File(Thread.currentThread().contextClassLoader.getResource("etc/idscp2/aisecconnector1-keystore.jks").path).path
        ctx.keyManagers.keyStore.password = "password"
        ctx.trustManagers = TrustManagersParameters()
        ctx.trustManagers.keyStore = KeyStoreParameters()
        ctx.trustManagers.keyStore.resource = File(Thread.currentThread().contextClassLoader.getResource("etc/idscp2/client-truststore_new.jks").path).path
        ctx.trustManagers.keyStore.password = "password"

        return ctx
    }

    @Bean
    open fun server(): RoutesBuilder? {
        return object : RouteBuilder() {
            override fun configure() {
                from("idscp2server://0.0.0.0:29292?sslContextParameters=#sslContext")
                        .log("Server received: \${body} (idscp2.type: \${headers[idscp2.type]})")
                        .setBody().simple("PONG")
                        .setHeader("idscp2.type").simple("pong")
                        .log("Server response: \${body} (idscp2.type: \${headers[idscp2.type]})")
            }
        }
    }

    @Bean
    open fun client(): RoutesBuilder {
        return object : RouteBuilder() {
            override fun configure() {
                from("idscp2client://localhost:29292?connectionShareId=pingPongConnection&sslContextParameters=#sslContext")
                        .log("Client received: \${body} (idscp2.type: \${headers[idscp2.type]})")
                        .removeHeader("idscp2.type") // Prevents the client consumer from sending the message back to the server
            }
        }
    }

    @Bean
    open fun pingPongTimer(): RoutesBuilder {
        return object : RouteBuilder() {
            override fun configure() {
                from("timer://tenSecondsTimer?fixedRate=true&period=10000")
                        .setBody().simple("PING")
                        .setHeader("idscp2.type").simple("ping")
                        .log("Client sends: \${body} (idscp2.type: \${headers[idscp2.type]})")
                        .to("idscp2client://localhost:29292?connectionShareId=pingPongConnection&sslContextParameters=#sslContext")
            }
        }
    }
}