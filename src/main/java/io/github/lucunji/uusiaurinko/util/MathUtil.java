package io.github.lucunji.uusiaurinko.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class MathUtil {

    public static boolean containsInclusive(AxisAlignedBB boundingBox, Vector3d vec) {
        return containsInclusive(boundingBox, vec.getX(), vec.getY(), vec.getZ());
    }

    public static boolean containsInclusive(AxisAlignedBB boundingBox, double x, double y, double z) {
        return x >= boundingBox.getMin(Direction.Axis.X) && x <= boundingBox.getMax(Direction.Axis.X)
                && y >= boundingBox.getMin(Direction.Axis.Y) && y <= boundingBox.getMax(Direction.Axis.Y)
                && z >= boundingBox.getMin(Direction.Axis.Z) && z <= boundingBox.getMax(Direction.Axis.Z);
    }

    public static Vector3d getVectorToTargetNormalized(Entity a, Entity b) {
        return a.getPositionVec().subtract(b.getPositionVec()).normalize();
    }
}
