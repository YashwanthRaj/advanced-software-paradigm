package FitnessConsultation

import grails.gorm.transactions.Transactional
import grails.converters.JSON
import grails.validation.Validateable



class ClientController {

    def index() {
        ArrayList<Object> result = new ArrayList<Object>();

        ArrayList<Trainer> allClients = Client.list()

        allClients.each {client ->
            def obj = [
                    firstName: client.firstName,
                    lastName: client.lastName ,
                    gender: client.gender ,
                    address: client.address,
                    emai: client.email,
                    dob: client.dob,
            ]
            result.push(obj)
        }
        render result as JSON
    }

    @Transactional
    def register(Client client){

        println("Registering...")
        if(client.hasErrors()){
            render new Response(status: 0, obj: client.errors) as JSON
        }
        else{
            client.save()
            render new Response(status: 1, message: "Registered Successfully. Client Id " + client.id) as JSON
        }
    }

    @Transactional
    def signIn(SignInPayload payload){
        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            def query = Client.where {
                email == payload.email
            }
            Client client = query.find()

            if(client){
                if(client.password == payload.password){
                    client.sessionStatus = SessionStatus.IN
                    client.save()
                    render new Response(status: 1, message: "Login Success") as JSON
                }
                else{
                    render new Response(status: 0, message: "Incorrect Password") as JSON
                }
            }
            else{
                render new Response(status: 0, message: "No client present with given emailId") as JSON
            }
        }

    }

    @Transactional
    def logout(LogoutPayload payload){
        if(payload.hasErrors()){
            render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            def query = Client.where {
                email == payload.email
            }
            Client client = query.find()

            if(client){
                if(client.email == payload.email){
                    client.sessionStatus = SessionStatus.OUT
                    client.save()
                    render new Response(status: 1, message: "Successfully Logged Out") as JSON
                }
                else{
                    render new Response(status: 0, message: "Incorrect email") as JSON
                }
            }
            else{
                render new Response(status: 0, message: "No client present with given emailId") as JSON
            }
        }
    }

    @Transactional
    def requestConsultation(Consultation consultation){

        if(consultation.hasErrors()){
            render new Response(status: 0, obj:  consultation.errors) as JSON
        }
        else{
            Client client = Client.get(consultation.clientId)
            if(client.sessionStatus == SessionStatus.IN){

                consultation.status = Status.PENDING
                consultation.save()

                render new Response(status: 1, message: "Consultation requested with " + consultation.id ) as JSON
            }
            else {
                render new Response(status: 0, message: "Client of this consultation must be signed in to use this API" ) as JSON
            }

        }
    }

    @Transactional
    def requestAppointment(Appointment appointment){
        if(appointment.hasErrors()){
            render new Response(status: 0, obj:  appointment.errors) as JSON
        }
        else{
            print("CLIENTID" +  appointment.consultation.clientId)
            Client client = Client.get(appointment.consultation.clientId)

            if(client.sessionStatus == SessionStatus.IN){

                Consultation consultation = Consultation.get(appointment.consultation.id)
                if(consultation.status == Status.ACCEPTED){
                    appointment.status = Status.PENDING
                    appointment.save()

                    render new Response(status: 1, message: "Appointment Request Created" ) as JSON
                }
                else {
                    render new Response(status: 0, message: "Consultation Must Be Accepted By the trainer first") as JSON
                }
            }
            else {
                render new Response(status: 0, message: "Client of this appointment must be signed in to use this API" ) as JSON
            }


        }

    }

    def getConsultations(Long clientId){

        if(clientId > 0){
            def client = Client.get(clientId)
            if(client){
                def hql = "from Consultation where client = " + clientId
                def result = Consultation.executeQuery(hql)

                render new Response(status: 1, message: "Success", obj: result) as JSON
            }
            render new Response(status: 0, message: "No Client present with given clientId") as JSON
        }
        render new Response(status: 0, message: "Provide a valid clientId") as JSON
    }

    def getAppointments(Long consultationId){

        print(consultationId)

        if(consultationId > 0){
            def consultation = Consultation.get(consultationId)
            if(consultation){
                def hql = "from Appointment where consultation = " + consultationId
                def result = Appointment.executeQuery(hql)

                render new Response(status: 1, message: "Success", obj: result) as JSON
            }
            else{
                render new Response(status: 0, message:  "No Consultation Present with the given consultationId") as JSON
            }
        }
        else{
            render new Response(status: 0, message:  "Provide a valid consultationId") as JSON
        }
    }

    @Transactional
    def submitFeedback(SubmitFeedBackPayload payload){

        if(payload.hasErrors()){
            render new Response(status: 0, obj:  payload.errors) as JSON
        }
        else {

            Client client = Client.get(payload.clientId)
            if (client.sessionStatus == SessionStatus.IN) {
                Consultation consultation = Consultation.get(payload.consultationId)
                if (consultation.status == Status.ACCEPTED) {
                    consultation.rating = payload.rating
                    consultation.feedback = payload.feedback
                    consultation.status = Status.CLOSED

                    consultation.save()

                    def query = Appointment.where {
                        status == Status.ACCEPTED
                    }
                    ArrayList<Appointment> result = query.findAll()

                    if (result.size() > 0) {
                        def flag = false
                        //Can be used in real time, to ensure all appointments are past their scheduled times
//                        result.each {apt ->
//                            if(apt.scheduledTime < new Date()){
//                                flag = true
//                            }

                        flag = true
                        if (flag) {

                            //Calculate Overall Rating
                            def trainerId = consultation.trainer.id
                            def hql = "from Consultation where trainer = " + trainerId + " and status = '" + Status.CLOSED + "'"
                            def allConsultations = Consultation.executeQuery(hql)

                            float rating = 0

                            if (allConsultations instanceof ArrayList) {
                                def count = allConsultations.size()
                                println(count)

                                if (count > 0) {
                                    allConsultations.each { cons -> rating += cons.rating; println(rating) }
                                    rating = rating / count;
                                    println(rating)

                                    Trainer trainer = Trainer.get(trainerId)
                                    trainer.rating = rating
                                    trainer.save()
                                }
                            }

                            render new Response(status: 1, message: "Updated Successfully") as JSON
                        } else {
                            render new Response(status: 0, message: "There still pending appointments. Please finish them first before submitting feedback.") as JSON
                        }
                    } else {
                        render new Response(status: 0, message: "No accepted appointments available to submit feedback") as JSON
                    }

                } else {
                    render new Response(status: 0, message: "Status of given consultation " + consultation.status) as JSON
                }
            }
            else {
                render new Response(status: 0, message: "Client must be signed in to use this API" ) as JSON
            }
        }



    }
}



class SubmitFeedBackPayload implements Validateable {
     int consultationId
     int rating;
     String feedback;
    int clientId

    static constraints = {
        clientId min: 1
        rating range: 1..5
        consultationId min: 1
    }
}



class RequestAppointmentPayload implements Validateable{
    int clientId;
    Appointment appointment

    static  constraints = {
        clientId min: 1
    }

}




