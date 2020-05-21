import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._




object Requests {
    val addClient = http("addClient").post("/api/add_client")
                .body(StringBody("""{ "name": "Вася" , "last_name: "Царев" } """)).asJson

    val getClients = http("getEvents").get("/api/clients")

    val addEmployee = http("addEmployee").post("/api/add_employee")
    			.body(StringBody("""{"name": "Ваня", "last_name": "Медведев"}""")).asJson

    val getEmployees = http("getEvents").get("/api/employees")
}

class SimpleSimulation extends Simulation {
	val url = ConfigFactory.load().getString("url")

    val clientScn = scenario("client").exec(Requests.getClients).exec(Requests.addClient)
    val employeeScn = scenario("employee").exec(Requests.getEmployees).exec(Requests.addEmployee)

    val httpConf = http.baseUrl(url)

    setUp(clientScn.inject(incrementConcurrentUsers(2)
    						.times(50)
    						.eachLevelLasting(20 seconds)
    						.separatedByRampsLasting(10 seconds)
    						.startingFrom(0)
    					).protocols(httpConf),

		  employeeScn.inject(incrementConcurrentUsers(2)
    						.times(50)
    						.eachLevelLasting(20 seconds)
    						.separatedByRampsLasting(10 seconds)
    						.startingFrom(0)
    					).protocols(httpConf)
		)
}




