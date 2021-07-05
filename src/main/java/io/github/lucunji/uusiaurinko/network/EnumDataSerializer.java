package io.github.lucunji.uusiaurinko.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class EnumDataSerializer<T extends Enum<T>> implements IDataSerializer<T> {
    private final Class<T> type;

    public EnumDataSerializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public void write(PacketBuffer buf, T value) {
        buf.writeEnumValue(value);
    }

    @Override
    public T read(PacketBuffer buf) {
        return buf.readEnumValue(type);
    }

    @Override
    public T copyValue(T value) {
        return value;
    }
}
