package me.marvin.smp.utils.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

/**
 * A simple configuration loader.
 */
public class ConfigurationLoader {
    /**
     * Codec map.
     */
    public static final Map<Class<?>, Codec<?, ?>> CODECS = Map.of(
        Component.class, new Codec<String, Component>(
            s -> MiniMessage.miniMessage().deserialize(s),
            c -> MiniMessage.miniMessage().serialize(c)
        )
    );

    /**
     * Loads the given configuration.
     *
     * @param plugin the plugin
     * @param clazz the configuration class
     */
    @SuppressWarnings("unchecked")
    public static void initializeConfig(JavaPlugin plugin, Class<?> clazz) {
        String fileName = Objects.requireNonNull(clazz.getAnnotation(Config.class), Config.class + " annotation was not present.").value();
        ConfigData data = new ConfigData(plugin, fileName);

        Stream.of(clazz.getDeclaredFields())
            .forEach(field -> {
                Class<?> type = field.getType();
                if (type == ConfigData.class) {
                    try {
                        field.set(null, data);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        assert data.configuration() != null;
        assert data.file() != null;

        getAllDeclaredClasses(clazz).stream()
            .flatMap(aClass -> Stream.of(aClass.getDeclaredFields()))
            .filter(field -> field.isAnnotationPresent(Value.class))
            .forEach(field -> {
                String path = getPath(field);
                Codec<Object, Object> codec = (Codec<Object, Object>) CODECS.getOrDefault(field.getType(), Codec.identity());

                try {
                    if (data.configuration().contains(path)) {
                        //noinspection ConstantConditions - check above
                        field.set(null, codec.deserialize(data.configuration().get(path)));
                    } else {
                        data.configuration().set(path, codec.serialize(field.get(null)));
                    }
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });

        getAllDeclaredClasses(clazz).stream()
            .flatMap(aClass -> Stream.of(aClass.getDeclaredMethods()))
            .filter(method -> method.isAnnotationPresent(Initialize.class))
            .forEach(method -> {
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });

        data.save();
        data.saveDefault();
    }

    private static String getPath(Field field) {
        Value value = field.getAnnotation(Value.class);
        Class<?> declaring = field.getDeclaringClass();
        StringBuilder pathBuilder = new StringBuilder();

        while (declaring != null) {
            Value section = declaring.getAnnotation(Value.class);
            if (section != null) {
                if (pathBuilder.length() != 0) {
                    pathBuilder.insert(0, '.');
                }
                pathBuilder.insert(0, section.value());
            }
            declaring = declaring.getDeclaringClass();
        }

        pathBuilder.append('.').append(value.value());
        return pathBuilder.toString().trim();
    }

    private static List<Class<?>> getAllDeclaredClasses(Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();
        List<Class<?>> nested = Arrays.asList(clazz.getDeclaredClasses());
        list.add(clazz);

        for (Class<?> aClass : nested) {
            list.addAll(getAllDeclaredClasses(aClass));
        }

        list.addAll(nested);
        return list;
    }
}
