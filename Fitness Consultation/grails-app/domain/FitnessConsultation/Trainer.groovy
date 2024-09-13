package FitnessConsultation

import grails.databinding.BindingFormat
import grails.persistence.Entity

@Entity()
class Trainer {
    String firstName;
    String lastName;
    String gender;
    String address;
    String email;
    SessionStatus sessionStatus;

    @BindingFormat('yyyy-MM-dd HH:mm:ss.S')
    Date dob;

    String password
    float rating

    static mapping = {
        version false
    }

    static constraints = {
        firstName size: 3..15, blank: false
        lastName size: 3..15, blank: false
        gender size: 1..2, blank: false
        address size: 7..255, blank: false
        //dob max: new Date()
        email email: true, unique: true

    }
}