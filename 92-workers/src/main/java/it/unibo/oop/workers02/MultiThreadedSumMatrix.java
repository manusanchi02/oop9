package it.unibo.oop.workers02;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of summing matrix values.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int threads;
    /**
     * Builds a new MultiThreadedSumMatrix.
     * 
     * @param threads
     *      the number of threads
     */
    public MultiThreadedSumMatrix(final int threads) {
        this.threads = threads;
    }

    @Override
    public double sum(final double[][] matrix) {
        /**
         * Separate the matrix and build a list of workers (a worker for each line).
         */
        final int lineSeparator = matrix.length % this.threads + matrix.length / threads;
        final List<Worker> workers = new ArrayList<>(this.threads);
        for (int i = 0; i < matrix.length; i = i + lineSeparator) {
            workers.add(new Worker(i, lineSeparator, matrix));
        }
        /**
         * Start the workers.
         */
        for (final Worker w : workers) {
            w.start();
        }
        /**
         * Wait until they finish the computation.
         */
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum = sum + w.getRes();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private static class Worker extends Thread {
        private final int startpos;
        private final int nelem;
        private double res;
        private final double[][] m;

        /**
        * Builds a new worker.
        @param  startpos
            the start position
        @param nelem 
            the number of elements
        @param m
            the matrix
        */
        Worker(final int startpos, final int nelem, final double[][] m) {
            super();
            this.startpos = startpos;
            this.nelem = nelem;
            this.m = m; // NOPMD
        }

        @Override
        public void run() {
            System.out.println("Working from position " + this.startpos + " to position " //NOPMD
             + (this.startpos + this.nelem - 1)); // NOPMD
            for (int i = this.startpos; i < this.m.length && i < this.startpos + this.nelem; i++) {
                for (final double d : this.m[i]) {
                    this.res = this.res + d;
                }
            }
        }

        /**
         * Returns the result of the sum.
         * @return the sum of that worker
         */
        public double getRes() {
            return this.res;
        }
    }
}
