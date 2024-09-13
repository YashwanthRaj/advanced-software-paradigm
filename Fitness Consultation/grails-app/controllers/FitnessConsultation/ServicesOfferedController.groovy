package FitnessConsultation

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.Validateable


class ServicesOfferedController {

    def index(Long trainerId) {

        if(trainerId > 0){
            Trainer trainer1  = Trainer.get(trainerId)
            if(trainer1){
                def query = ServicesOffered.where {
                    trainer == trainer1
                }
                def so = query.findAll()

                render new Response(status: 1, obj: so) as JSON
            }
            else
            {
                render new Response(status: 0, message: "No trainer found with given trainerId") as JSON
            }
        }else {
            render new Response(status: 0, message: "Provide valid trainerId") as JSON
        }


    }

    @Transactional
    def register(RegisterServicesPayload payload){

        if(payload.hasErrors()){
           render new Response(status: 0, obj: payload.errors) as JSON
        }
        else{
            Trainer trainer1 = Trainer.get(payload.trainerId)
            if(trainer1){
                if(trainer1.sessionStatus == SessionStatus.IN){
                    ArrayList<ServicesOffered> servicesList = new ArrayList<>()

                    payload.serviceIds.each {serviceId ->
                        Services ser = Services.get(serviceId)
                        servicesList.push( new ServicesOffered(service: ser, trainer: trainer1 ))
                    }
                    ServicesOffered.saveAll(servicesList)
                    render new Response(status: 1, message: "Successfully Registered the services") as JSON
                }
                else {
                    render new Response(status: 0, message: "Trainer must be signed in to use this API" ) as JSON
                }
            }
            else{
                render new Response(status: 0, message: "No trainer present with given trainerId") as JSON
            }
        }
    }
}

class RegisterServicesPayload implements  Validateable{
    int trainerId;
    int[] serviceIds;

    static constraints ={
        serviceIds maxSize: 5
    }

}