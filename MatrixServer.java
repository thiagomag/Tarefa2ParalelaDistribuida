import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MatrixServer {
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Servidor iniciado. Aguardando conexão de clientes...");
            while (true) {
                Socket socket = server.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());

                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                System.out.println("Thread iniciada para o cliente " + socket.getInetAddress());

                int p = in.readInt();
                for (int i = 0; i < p; i++) {
                    double[][] matrix1 = (double[][]) in.readObject();
                    double[][] matrix2 = (double[][]) in.readObject();

                    Thread thread = new Thread(new MatrixMultiplier(matrix1, matrix2));
                    thread.start();

                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    double[][] result = MatrixMultiplier.getResult();

                    out.writeObject(result);
                    out.flush();
                }

                System.out.println("Thread finalizada para o cliente " + socket.getInetAddress());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MatrixMultiplier implements Runnable {
        private static double[][] result;
        private double[][] matrix1;
        private double[][] matrix2;

        public MatrixMultiplier(double[][] matrix1, double[][] matrix2) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
        }

        @Override
        public void run() {
            System.out.println("Thread de multiplicação iniciada.");

            int m = matrix1.length;
            int n = matrix2[0].length;
            int p = matrix2.length;
            double[][] c = new double[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < p; k++) {
                        c[i][j] += matrix1[i][k] * matrix2[k][j];
                    }
                }
            }

            result = c;

            System.out.println("Thread de multiplicação finalizada.");
        }

        public static double[][] getResult() {
            return result;
        }
    }
}
