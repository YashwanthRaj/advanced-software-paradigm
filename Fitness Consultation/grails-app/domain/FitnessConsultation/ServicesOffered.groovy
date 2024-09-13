package FitnessConsultation

import grails.persistence.Entity

@Entity()
class ServicesOffered {

    Trainer trainer
    Services service

    static mapping = {
        version false
    }
}