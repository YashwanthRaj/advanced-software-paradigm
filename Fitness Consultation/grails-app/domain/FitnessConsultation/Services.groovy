package FitnessConsultation

import grails.persistence.Entity

@Entity()
class Services {

    String service

    static mapping = {
        version false
    }

    static constraints = {
        service blank: false
    }
}