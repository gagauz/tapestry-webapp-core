package com.xl0e.testdata;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataBaseScenario {

    private static final Set<Class<?>> executedScenarios = new HashSet<>();

    protected final Logger LOG;

    protected final Random rand = new Random(System.currentTimeMillis());

    protected abstract void execute();

    public DataBaseScenario() {
        LOG = LoggerFactory.getLogger(getClass());
    }

    public synchronized final void run() {

        if (wasExecuted()) {
            return;
        }

        for (DataBaseScenario scenario : getDependsOn()) {
            if (scenario.equals(this)) {
                throw new IllegalStateException("Scenario " + this.getClass() + " must not depend on itself!");
            }

            if (!scenario.wasExecuted()) {
                scenario.run();
            }
        }

        try {
            final long start = System.currentTimeMillis();
            execute();
            LOG.info("Executed scenario {} in [{}] ms", getClass().getSimpleName(), (System.currentTimeMillis() - start));
        } finally {
            executedScenarios.add(this.getClass());
        }
    }

    protected final boolean wasExecuted() {
        return executedScenarios.contains(this.getClass());
    }

    protected DataBaseScenario[] getDependsOn() {
        return new DataBaseScenario[0];
    }
}
