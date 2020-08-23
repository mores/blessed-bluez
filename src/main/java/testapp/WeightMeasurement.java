package testapp;

import com.welie.blessed.BluetoothBytesParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16;
import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8;

public class WeightMeasurement {
    private final double weight;
    private final WeightUnit unit;
    private Date timestamp;
    private Integer userID;
    private Integer BMI;
    private Integer height;

    public WeightMeasurement(byte[] byteArray) {
        BluetoothBytesParser parser = new BluetoothBytesParser(byteArray);

        // Parse flag byte
        final int flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
        unit = ((flags & 0x01) > 0) ? WeightUnit.Pounds : WeightUnit.Kilograms;
        final boolean timestampPresent = (flags & 0x02) > 0;
        final boolean userIDPresent = (flags & 0x04) > 0;
        final boolean bmiAndHeightPresent = (flags & 0x08) > 0;

        // Get weight value
        double weightMultiplier = (unit == WeightUnit.Kilograms) ? 0.005 : 0.01;
        weight = parser.getIntValue(FORMAT_UINT16) * weightMultiplier;

        // Get timestamp if present
        if (timestampPresent) {
            timestamp = parser.getDateTime();
        }

        // Get user ID if present
        if (userIDPresent) {
            userID = parser.getIntValue(FORMAT_UINT8);
        }

        // Get BMI and Height if present
        if (bmiAndHeightPresent) {
            BMI = parser.getIntValue(FORMAT_UINT16);
            height = parser.getIntValue(FORMAT_UINT16);
        }
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedTimestamp = df.format(timestamp);
        return String.format("%.1f %s, user %d, BMI %d, height %d at (%s)", weight, unit == WeightUnit.Kilograms ? "kg" : "lb", userID, BMI, height, formattedTimestamp);
    }
}