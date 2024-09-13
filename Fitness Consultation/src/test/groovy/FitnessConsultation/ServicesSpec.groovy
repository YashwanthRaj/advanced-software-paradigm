package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ServicesSpec extends Specification implements DomainUnitTest<Services> {

     void "test domain constraints"() {
        when:
        Services domain = new Services()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
