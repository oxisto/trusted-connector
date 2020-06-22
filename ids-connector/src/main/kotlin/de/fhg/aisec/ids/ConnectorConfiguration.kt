package de.fhg.aisec.ids

import de.fhg.aisec.ids.api.cm.ContainerManager
import de.fhg.aisec.ids.api.infomodel.InfoModel
import de.fhg.aisec.ids.rm.RouteManagerService
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class ConnectorConfiguration {

    @Autowired
    private lateinit var cml: ContainerManager

    @Autowired
    private lateinit var im: InfoModel

    @Autowired
    private lateinit var rm: RouteManagerService

    @Bean
    fun listBeans(ctx: ApplicationContext): CommandLineRunner? {
        return CommandLineRunner {
            val beans: Array<String> = ctx.beanDefinitionNames

            Arrays.sort(beans)

            for (bean in beans) {
                TrustedConnector.LOG.info("Loaded bean: {}", bean)
            }
        }
    }

    @Bean
    fun listContainers(ctx: ApplicationContext): CommandLineRunner? {
        return CommandLineRunner {
            val containers = cml.list(false)

            for (container in containers) {
                TrustedConnector.LOG.debug("Container: {}", container.names)
            }
        }
    }

    @Bean
    fun showConnectorProfile(ctx: ApplicationContext): CommandLineRunner? {
        return CommandLineRunner {
            val connector = im.connector

            if (connector == null) {
                TrustedConnector.LOG.info("No connector profile stored yet.")
            } else {
                TrustedConnector.LOG.info("Connector profile: {}", connector)
            }
        }
    }

    @Bean
    fun showCamelInfo(ctx: ApplicationContext): CommandLineRunner? {
        return CommandLineRunner {
            val routes = rm.routes

            for (route in routes) {
                TrustedConnector.LOG.debug("Route: {}", route.shortName)
            }

            val components = rm.listComponents()

            for (component in components) {
                TrustedConnector.LOG.debug("Component: {}", component.bundle)
            }
        }
    }

}