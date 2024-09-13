package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ServicesOfferedSpec extends Specification implements DomainUnitTest<ServicesOffered> {

     void "test domain constraints"() {
        when:
        ServicesOffered domain = new ServicesOffered()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
