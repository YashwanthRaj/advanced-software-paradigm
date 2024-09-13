package FitnessConsultation

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ServicesOfferedControllerSpec extends Specification implements ControllerUnitTest<ServicesOfferedController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
