package FitnessConsultation

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ClientSpec extends Specification implements DomainUnitTest<Client> {

     void "test domain constraints"() {
        when:
        Client domain = new Client()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
