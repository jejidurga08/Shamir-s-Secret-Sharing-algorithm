import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        String[] fileNames = {"testcase1.json", "testcase2.json"};
        for (String fileName : fileNames) {
            try (FileReader fileReader = new FileReader(fileName)) {
                JSONObject jsonObject = new JSONObject(new JSONTokener(fileReader));
                BigInteger secret = findSecret(jsonObject);
                System.out.println("Secret for " + fileName + ": " + secret);
            } catch (IOException e) {
                System.err.println("Error reading file: " + fileName);
            }
        }
    }

    private static BigInteger findSecret(JSONObject jsonObject) {
        int n = jsonObject.getJSONObject("keys").getInt("n");
        int k = jsonObject.getJSONObject("keys").getInt("k");

        BigInteger[] xValues = new BigInteger[k];
        BigInteger[] yValues = new BigInteger[k];

        int index = 0;
        for (String key : jsonObject.keySet()) {
            if (!key.equals("keys")) {
                JSONObject point = jsonObject.getJSONObject(key);
                xValues[index] = BigInteger.valueOf(Long.parseLong(key));
                yValues[index] = new BigInteger(point.getString("value"), Integer.parseInt(point.getString("base")));
                index++;
                if (index == k) break;
            }
        }

        return lagrangeInterpolation(xValues, yValues);
    }

    private static BigInteger lagrangeInterpolation(BigInteger[] xValues, BigInteger[] yValues) {
        BigInteger secret = BigInteger.ZERO;
        for (int i = 0; i < xValues.length; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for (int j = 0; j < xValues.length; j++) {
                if (i != j) {
                    numerator = numerator.multiply(xValues[j].negate());
                    denominator = denominator.multiply(xValues[j].subtract(xValues[i]));
                }
            }
            BigInteger term = yValues[i].multiply(numerator).divide(denominator);
            secret = secret.add(term);
            }
        return secret;
    }
}

