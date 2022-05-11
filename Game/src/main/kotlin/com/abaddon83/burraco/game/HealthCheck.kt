package com.abaddon83.burraco.game


//class HealthCheck(val vertx: Vertx) {
//    var healthCheckHandler = HealthCheckHandler.create(vertx)
//
//
//    fun build(): HealthCheckHandler{
//
//        healthCheckHandler.register("my-procedure-name"){ promise ->
//            promise.complete(Status.OK());
//            //promise.complete(Status.KO());
//        };
//
//        // Register another procedure with a timeout (2s). If the procedure does not complete in
//        // the given time, the check fails.
//        healthCheckHandler.register(
//            "my-procedure-name-with-timeout",
//            2000
//        ) { promise: Promise<Status?> ->
//            promise.complete(Status.OK())
//            //promise.complete(Status.KO())
//        }
//
//        return healthCheckHandler;
//    }
//}