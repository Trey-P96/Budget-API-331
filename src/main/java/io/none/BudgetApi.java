package io.none;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@QuarkusMain
@ApplicationScoped
public class BudgetApi implements QuarkusApplication {
  public static void main(String... args) {
    Quarkus.run(BudgetApi.class, args);
  }

  @Override
  public int run(String... args) {
    Quarkus.waitForExit();
    return 0;
  }
}