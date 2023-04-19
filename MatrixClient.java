import java.io.*;
import java.net.*;
import java.util.Random;

public class MatrixClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(hostname, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            int p = 5;
            out.writeInt(p);

            Random rand = new Random();

            for (int i = 0; i < p; i++) {
                int m1 = rand.nextInt(10) + 1; // gera um número aleatório entre 1 e 10
                int n1 = rand.nextInt(10) + 1;
                int m2 = n1;
                int n2 = rand.nextInt(10) + 1;

                double[][] matrix1 = generateRandomMatrix(m1, n1);
                double[][] matrix2 = generateRandomMatrix(m2, n2);
                Thread.sleep(2000);

                out.writeObject(matrix1);
                out.writeObject(matrix2);
                out.flush();

                double[][] result = (double[][]) in.readObject();
                printMatrix(result);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double[][] generateRandomMatrix(int m, int n) {
        Random rand = new Random();
        double[][] matrix = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextDouble();
            }
        }
        return matrix;
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
