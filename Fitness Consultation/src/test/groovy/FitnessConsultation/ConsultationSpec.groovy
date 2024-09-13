package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ConsultationSpec extends Specification implements DomainUnitTest<Consultation> {

     void "test domain constraints"() {
        when:
        Consultation domain = new Consultation()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
