package FitnessConsultation

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ClientControllerSpec extends Specification implements ControllerUnitTest<ClientController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
