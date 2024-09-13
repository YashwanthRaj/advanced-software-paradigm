package FitnessConsultation

import grails.databinding.BindingFormat
import grails.persistence.Entity

public enum Status{
    PENDING, REJECTED, ACCEPTED, CLOSED
}

public enum SessionStatus {
    IN, OUT
}

@Entity()
class Appointment {

    Consultation consultation

    @BindingFormat('yyyy-MM-dd HH:mm:ss.S')
    Date scheduledTime

    Status status

    static mapping = {
        version false
    }
    static constraints = {
        scheduledTime min: new Date()
    }

}