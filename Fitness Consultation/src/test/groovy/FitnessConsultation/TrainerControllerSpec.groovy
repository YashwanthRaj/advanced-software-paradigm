package FitnessConsultation

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class TrainerControllerSpec extends Specification implements ControllerUnitTest<TrainerController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
