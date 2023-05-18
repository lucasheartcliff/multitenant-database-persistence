package com.cda.controller;

import com.cda.RequestHandler;
import com.cda.persistence.DatabaseContext;
import com.cda.service.ServiceFactoryImpl;
import com.cda.utils.functional.ThrowableConsumer;
import com.cda.utils.functional.ThrowableFunction;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
public abstract class BaseController {
  private final RequestHandler requestHandler;

  protected BaseController(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  protected <T> T encapsulateRequest(ThrowableFunction<ServiceFactoryImpl, T> function) {
    try (DatabaseContext databaseContext = requestHandler.buildDatabaseContext();) {
      ServiceFactoryImpl serviceFactory = requestHandler.buildServiceFactory(databaseContext);
      return function.apply(serviceFactory);
    } catch (Exception e) {
      System.out.println(e);
      // log error
    }
    return null;
  }

  protected void encapsulateRequest(ThrowableConsumer<ServiceFactoryImpl> function) {
    try (DatabaseContext databaseContext = requestHandler.buildDatabaseContext();) {
      ServiceFactoryImpl serviceFactory = requestHandler.buildServiceFactory(databaseContext);
      function.run(serviceFactory);
    } catch (Exception e) {
      System.out.println(e);
      // log error
    }
  }

}
