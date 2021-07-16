package io.github.lucunji.uusiaurinko.network;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.entity.NewSunEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModDataSerializers {
    public static List<IDataSerializer<?>> REGISTRY = Lists.newArrayList();

    public static final IDataSerializer<NewSunEntity.ConsumedMagicStone> CONSUMED_MAGIC_STONE =
            register(new EnumDataSerializer<>(NewSunEntity.ConsumedMagicStone.class));

    private static <T> IDataSerializer<T> register(IDataSerializer<T> dataSerializer) {
        REGISTRY.add(dataSerializer);
        return dataSerializer;
    }

    private static <T> IDataSerializer<T> register(BiConsumer<PacketBuffer, T> writer, Function<PacketBuffer, T> reader, Function<T, T> copier) {
        IDataSerializer<T> dataSerializer = new IDataSerializer<T>() {
            @Override
            public void write(PacketBuffer buf, T value) {
                writer.accept(buf, value);
            }

            @Override
            public T read(PacketBuffer buf) {
                return reader.apply(buf);
            }

            @Override
            public T copyValue(T value) {
                return copier.apply(value);
            }
        };
        return register(dataSerializer);
    }
}
