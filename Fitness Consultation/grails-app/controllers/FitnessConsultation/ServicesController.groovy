package FitnessConsultation

import grails.converters.JSON


class ServicesController {

    def index() {
        render new Response(status: 1, obj: Services.list()) as JSON
    }
}