package FitnessConsultation

import groovy.util.logging.Slf4j

@Slf4j
class Bootstrap{

    def init = { servletContext ->

        print("Bootsrapping")


    }

    def destroy = {
    }
}
