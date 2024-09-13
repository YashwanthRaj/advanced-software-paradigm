package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TrainerSpec extends Specification implements DomainUnitTest<Trainer> {

     void "test domain constraints"() {
        when:
        Trainer domain = new Trainer()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
