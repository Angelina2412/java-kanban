import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value == null ? null : value.toString());
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        String value = in.nextString();
        return value == null ? null : Duration.parse(value);
    }
}