package FitnessConsultation

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ServicesControllerSpec extends Specification implements ControllerUnitTest<ServicesController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
