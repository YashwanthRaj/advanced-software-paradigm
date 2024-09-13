package FitnessConsultation

import grails.persistence.Entity

@Entity()
class Consultation {

    Client client
    Trainer trainer
    int rating;
    String description
    Services services
    Status status
    String feedback

    //static hasMany = [appointments: Appointment]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 1000, blank: false
        rating range: 1..5, blank: false
        feedback blank: true, nullable: true
    }
}