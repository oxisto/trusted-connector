package de.fhg.aisec.ids

import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.support.jsse.KeyManagersParameters
import org.apache.camel.support.jsse.KeyStoreParameters
import org.apache.camel.support.jsse.SSLContextParameters
import org.apache.camel.support.jsse.TrustManagersParameters
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.io.File

@SpringBootApplication
open class ExampleConnector : TrustedConnector() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ExampleConnector>(*args)
        }
    }
}