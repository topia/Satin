package ladysnake.satin.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

    /**
     * Loads a config into a Plain Old Java Object from a file.
     *
     * @param configFile    a file in the config folder
     * @param configClass   the class of the POJO config
     * @param defaultValues a factory for a config object with default values
     * @param <T>           type of the POJO config
     * @return the generated config
     * @throws IOException if the config file cannot be read or created
     */
    public static <T> T load(String configFile, Class<T> configClass, Supplier<T> defaultValues) throws IOException {
        final Path config = FabricLoader.getInstance().getConfigDirectory().toPath().resolve(configFile);
        T features;
        if (Files.exists(config)) {
            try (final Reader reader = Files.newBufferedReader(config)) {
                features = Objects.requireNonNull(GSON.fromJson(reader, configClass), "invalid config: " + reader.toString());
            }
        } else {
            Files.createFile(config);
            features = defaultValues.get();
            try (final Writer writer = Files.newBufferedWriter(config)) {
                writer.append(GSON.toJson(features));
            }
        }
        return features;
    }
}
