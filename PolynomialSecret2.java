import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSecret2 {

    // Class to represent a point (x, y)
    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        // Specify the path to the JSON file
        String jsonFilePath = "input.json";

        // Load the JSON file content
        String jsonContent = loadJSONFile(jsonFilePath);
        if (jsonContent == null) {
            System.out.println("Failed to load the JSON file.");
            return;
        }

        // Parse JSON input
        JSONObject input = new JSONObject(jsonContent);
        JSONObject keys = input.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<Point> points = new ArrayList<>();

        // Extract points (x, y) and decode y
        for (String key : input.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                JSONObject root = input.getJSONObject(key);
                int base = root.getInt("base");
                String valueStr = root.getString("value");

                // Decode y value from the given base using BigInteger
                BigInteger yDecoded = new BigInteger(valueStr, base);

                // Store the point (x, y)
                points.add(new Point(x, yDecoded));
            }
        }

        // Ensure we have at least k points
        if (points.size() < k) {
            System.out.println("Insufficient number of points for interpolation.");
            return;
        }

        // Select the first k points for interpolation
        List<Point> selectedPoints = points.subList(0, k);

        // Solve for the constant term c using Lagrange Interpolation
        BigInteger c = lagrangeInterpolation(selectedPoints);

        // Print the constant term
        System.out.println("The constant term c as an integer is: " + c);
    }

    // Function to load the JSON file content
    private static String loadJSONFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return null;
        }
    }

    // Function to perform Lagrange Interpolation using BigInteger
    public static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        int k = points.size();

        for (int i = 0; i < k; i++) {
            Point pointI = points.get(i);
            int xi = pointI.x;
            BigInteger yi = pointI.y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    Point pointJ = points.get(j);
                    int xj = pointJ.x;

                    // numerator *= (-xj)
                    numerator = numerator.multiply(BigInteger.valueOf(-xj));

                    // denominator *= (xi - xj)
                    denominator = denominator.multiply(BigInteger.valueOf(xi - xj));
                }
            }

            // Compute term = yi * numerator / denominator
            // Ensure exact division
            BigInteger term = yi.multiply(numerator).divide(denominator);

            // Add to the result
            result = result.add(term);
        }

        return result;
    }
}
