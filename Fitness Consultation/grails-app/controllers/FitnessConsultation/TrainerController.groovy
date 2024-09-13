package FitnessConsultation

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.Validateable

class TrainerController {

    def index() {

        ArrayList<Object> result = new ArrayList<Object>();

        ArrayList<Trainer> allTrainers = Trainer.list()

        allTrainers.each {trainer1 ->

            def services1 = Services.executeQuery("""
                                    SELECT S.service
                                    FROM Services S
                                    INNER JOIN ServicesOffered SO ON S.id = SO.service
                                    WHERE SO.trainer = :trainerId
                                """, [trainerId: trainer1])

            if(services1 == null || services1.size() == 0){
                services1 = 'No services registered yet'
            }

            def obj = [
                    firstName: trainer1.firstName,
                    lastName: trainer1.lastName ,
                    gender: trainer1.gender ,
                    address: trainer1.address,
                    emai: trainer1.email,
                    dob: trainer1.dob,
                    rating: trainer1.rating,
                    services: services1
            ]
            result.push(obj)
        }

        render result as JSON
    }

    def searchTrainerByService(String service){

        render ServicesOffered.executeQuery("""
                                        Select SO.trainer as trainer, S.service as service FROM ServicesOffered SO
                                         INNER JOIN Services S ON S.id = SO.service
                                         WHERE S.service LIKE :pattern
                                        """, [pattern: "%" + service + "%" ]) as JSON
    }

    @Transactional
    def register(Trainer trainer){
        if(trainer.hasErrors()){
            render new Response(status: 0, obj: trainer.errors) as JSON
        }
        else{
            trainer.save()
            render new Response(status: 1, message: "Registered Successfully. Trainer Id " + trainer.id) as JSON
        }
    }

    @Transactional
    def signIn(SignInPayload payload){
        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            def query = Trainer.where {
                email == payload.email
            }
            Trainer trainer = query.find()

            if(trainer){
                if(trainer.password == payload.password){
                    trainer.sessionStatus = SessionStatus.IN
                    trainer.save()
                    render new Response(status: 1, message: "Login Success") as JSON
                }
                else{
                    render new Response(status: 0, message: "Incorrect Password") as JSON
                }
            }
            else{
                render new Response(status: 0, message: "No trainer present with given emailId") as JSON
            }
        }

    }

    @Transactional
    def logout(LogoutPayload payload){
        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            def query = Trainer.where {
                email == payload.email
            }
            Trainer trainer = query.find()

            if(trainer){
                if(trainer.email == payload.email){
                    trainer.sessionStatus = SessionStatus.OUT
                    trainer.save()
                    render new Response(status: 1, message: "Successfully Logged Out") as JSON
                }
                else{
                    render new Response(status: 0, message: "Incorrect email") as JSON
                }
            }
            else{
                render new Response(status: 0, message: "No trainer present with given emailId") as JSON
            }
        }
    }

    @Transactional
    def respondToConsultation(RespondToConsultationPayload payload){

        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            Trainer trainer = Trainer.get(payload.trainerId)
            if(trainer.sessionStatus == SessionStatus.IN){
                def consultation = Consultation.get(payload.consultationId);
                if(consultation){
                    if(payload.answer == Answer.YES){
                        consultation.status = Status.ACCEPTED
                    } else {
                        consultation.status = Status.REJECTED
                    }
                    consultation.save()
                    render new Response(status: 1, message: "Updated Successfully") as JSON
                }
                else{
                    render new Response(status: 0, message: "No consultation present with given consultationId") as JSON
                }
            }
            else {
                render new Response(status: 0, message: "Trainer must be signed in to use this API" ) as JSON
            }
        }
    }

    @Transactional
    def respondToAppointment(RespondToAppointmentPayload payload){
        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            Trainer trainer = Trainer.get(payload.trainerId)
            if(trainer.sessionStatus == SessionStatus.IN){
                Appointment aptm = Appointment.get(payload.appointmentId)
                if(aptm) {
                    if(payload.answer = Answer.YES){
                        aptm.status = Status.ACCEPTED
                    }
                    else {
                        aptm.status = Status.REJECTED
                    }
                    aptm.save()
                    render new Response(status: 1, message: "Updated Successfully") as JSON
                }
                else{
                    render new Response(status: 0, message: "No appointment present with given appointmentId") as JSON
                }
            }
            else {
                render new Response(status: 0, message: "Trainer must be signed in to use this API" ) as JSON
            }
        }
    }

    def getConsultations(Long trainerId){
        if(trainerId > 0){
            Trainer trainer1 = Trainer.get(trainerId)
            if(trainer1){
                def query = Consultation.where {
                    trainer == trainer1
                }
                def result = query.find()
                render new Response(status: 1, message: "Success", obj: result) as JSON
            }
            else{
                render new Response(status: 0, message: "No consultation present with given trainerId") as JSON
            }
        }
        else{
            render new Response(status: 0, message: "Provide a valid trainerId") as JSON
        }
    }

    def getAppointments(Long consultationId){
        if(consultationId > 0){
            Consultation consultation1 = Consultation.get(consultationId)
            if(consultation1){
                def query = Appointment.where {
                    consultation == consultation1
                }
                def result = query.find()
                render new Response(status: 1, message: "Success", obj: result) as JSON
            }
            else{
                render new Response(status: 0, message: "No consultation present with given trainerId") as JSON
            }
        }
        else{
            render new Response(status: 0, message: "Provide a valid consultationId") as JSON
        }
    }

    @Transactional
    def getTrainerRating(Long trainerId) {
        if(trainerId > 0){
            Trainer trainer1 = Trainer.get(trainerId)
            if(trainer1){
                def hql = "from Consultation where trainer = " + trainerId + " and status = '" + Status.CLOSED + "'"
                def allConsultations = Consultation.executeQuery(hql)

                float rating = 0

                if(allConsultations instanceof ArrayList){
                    def count = allConsultations.size()
                    println(count)

                    if(count > 0) {
                        allConsultations.each { consultation -> rating += consultation.rating; println(rating)}
                        rating = rating / count;
                        println(rating)

                        trainer1.rating = rating
                        trainer1.save()
                        render new Response(status: 1, message: trainer1.firstName + "'s Rating - " + trainer1.rating) as JSON
                    }
                    render new Response(status: 0, message: "No Closed Consultations yet") as JSON
                }


            }
            else{
                render new Response(status: 0, message: "No Trainer present with given trainerId") as JSON
            }
        }
        else{
            render new Response(status: 0, message: "Please provide a valid trainerId") as JSON
        }
    }

}

class Response {
    int status;
    String message
    Object obj;

}



class SignInPayload implements Validateable{
    String email;
    String password;
}

class LogoutPayload implements Validateable{
    String email;
}

enum Answer {
    YES, NO
}

class RespondToConsultationPayload implements Validateable{
    int consultationId;
    Answer answer;
    int trainerId;

    static constraints = {
        consultationId min: 1
        trainerId  min: 1
    }
}

class RespondToAppointmentPayload implements Validateable{
    int appointmentId;
    Answer answer;
    int trainerId;

    static constraints = {
        appointmentId min: 1
        trainerId min: 1
    }

}

