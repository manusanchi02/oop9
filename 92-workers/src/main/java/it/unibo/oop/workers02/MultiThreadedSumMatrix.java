package it.unibo.oop.workers02;

public class MultiThreadedSumMatrix implements SumMatrix {

    /**
     * Builds a new MultiThreadedSumMatrix
     */
    int threads;
    public MultiThreadedSumMatrix(int threads) {
        this.threads = threads;
    }

    @Override
    public double sum(double[][] matrix) {
        Worker worker = new Worker(0, 10, matrix);
        return worker.res;
    }

    private static class Worker extends Thread {
        private final int startpos;
        private final int nelem;
        private long res;
        private double[][] m;

        /**
        * Builds a new worker
        */
        Worker(int startpos, int nelem, double[][] m) {
            super();
            this.startpos = startpos;
            this.nelem = nelem;
            this.m = m;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < m.length && i < startpos + nelem; i++) {
                this.res += Double.doubleToLongBits(m[i][i]);
            }
        }

    }
    
}