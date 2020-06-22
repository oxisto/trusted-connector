package de.fhg.aisec.ids;

import de.fhg.aisec.ids.api.cm.ContainerManager
import de.fhg.aisec.ids.api.infomodel.InfoModel
import de.fhg.aisec.ids.rm.RouteManagerService
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import java.util.*


/**
 * Main startup class for the Trusted Connector using Spring Boot.
 */
@SpringBootApplication
class TrustedConnector {

    companion object {

        val LOG = LoggerFactory.getLogger(TrustedConnector::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TrustedConnector>(*args)
        }
    }
}
