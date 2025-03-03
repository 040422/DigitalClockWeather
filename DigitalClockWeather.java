import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DigitalClockWeather extends JFrame {

    private JLabel clockLabel;
    private JLabel weatherLabel;
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String CITY = "London";
    private static final String COUNTRY = "UK";

    public DigitalClockWeather() {
        // Setup JFrame
        setTitle("Digital Clock and Weather");
        setSize(400, 200);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create labels
        clockLabel = new JLabel("", SwingConstants.CENTER);
        clockLabel.setFont(new Font("Arial", Font.BOLD, 40));
        add(clockLabel, BorderLayout.CENTER);

        weatherLabel = new JLabel("Fetching weather...", SwingConstants.CENTER);
        weatherLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(weatherLabel, BorderLayout.SOUTH);

        // Timer for updating the clock every second
        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        // Update weather information every 10 minutes
        Timer weatherTimer = new Timer(600000, e -> updateWeather());
        weatherTimer.start();

        // Initial updates
        updateClock();
        updateWeather();
    }

    private void updateClock() {
        // Use local time zone
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        clockLabel.setText(currentTime);
    }

    private void updateWeather() {
        try {
            String weather = getWeatherData();
            weatherLabel.setText(weather);
        } catch (Exception e) {
            weatherLabel.setText("Failed to fetch weather.");
            e.printStackTrace();  // Print stack trace for debugging
        }
    }

    private String getWeatherData() throws Exception {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "," + COUNTRY + "&appid=" + API_KEY + "&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP GET Request Failed with Error Code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        System.out.println("Weather Data: " + content.toString());  // Debug: Print the raw JSON data
        return parseWeatherData(content.toString());
    }

    private String parseWeatherData(String data) {
        String description = extractValue(data, "\"description\":\"", "\"");
        String tempStr = extractValue(data, "\"temp\":", ",");
        double temp = Double.parseDouble(tempStr);
        return String.format("Weather: %s, %.1f°C", description, temp);
    }

    private String extractValue(String data, String startDelimiter, String endDelimiter) {
        int startIndex = data.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = data.indexOf(endDelimiter, startIndex);
        return data.substring(startIndex, endIndex);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DigitalClockWeather frame = new DigitalClockWeather();
            frame.setVisible(true);
        });
    }
}
