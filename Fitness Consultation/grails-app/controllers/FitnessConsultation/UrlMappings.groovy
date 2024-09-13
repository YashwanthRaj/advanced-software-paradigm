package FitnessConsultation

class UrlMappings {
    static mappings = {
        //        delete "/$controller/$id(.$format)?"(action:"delete")
//        get "/$controller(.$format)?"(action:"index")
//        get "/$controller/$id(.$format)?"(action:"show")
//        post "/$controller(.$format)?"(action:"save")
//        put "/$controller/$id(.$format)?"(action:"update")
//        patch "/$controller/$id(.$format)?"(action:"patch")



        group "/client", {
            get "/" (controller: "client", action: "index")
            post "/signin" (controller: "client", action: "signIn")
            post "/logout" (controller: "client", action: "logout")
            post "/register/" (controller: "client", action: "register")
            post "/consultation-request/" (controller: "client", action: "requestConsultation")
            get "/consultations/$clientId(.$format)?" (controller: "client", action: "getConsultations")
            get "/appointments/$consultationId(.$format)?" (controller: "client", action: "getAppointments")
            post "/submit-feedback/" (controller: "client", action: "submitFeedback")
            post "/appointment-request/" (controller: "client", action: "requestAppointment")

        }

        group "/trainer", {
            get "/" (controller: "trainer", action: "index")
            post "/signin" (controller: "trainer", action: "signIn")
            post "/logout" (controller: "trainer", action: "logout")
            post "/register/" (controller: "trainer", action: "register")
            post "/consultation-respond" (controller: "trainer", action: "respondToConsultation")
            post "/appointment-respond" (controller: "trainer", action: "respondToAppointment")
            get "/consultations/$trainerId(.$format)?" (controller: "trainer", action: "getConsultations")
            get "/appointments/$consultationId(.$format)?" (controller: "trainer", action: "getAppointments")
            get "/rating/$trainerId(.$format)?" (controller: "trainer", action: "getTrainerRating")
            get "/$service(.$format)?" (controller: "trainer", action: "searchTrainerByService")

        }

        group "/services", {
            get "/" (controller: "services", action: "index")
        }

        group "/services-offered", {
            get "/$trainerId(.$format)?" (controller: "servicesOffered", action: "index")
            post "/" (controller: "servicesOffered", action: "register")
        }

        "/"(controller: 'application', action:'index')
        "500"(view:'/error')
        "404"(view:'/notFound')

    }
}
