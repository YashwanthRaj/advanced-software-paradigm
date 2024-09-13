package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AppointmentSpec extends Specification implements DomainUnitTest<Appointment> {

     void "test domain constraints"() {
        when:
        Appointment domain = new Appointment()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
