package org.barrelmc.barrel.math;

import com.nukkitx.math.GenericMath;
import com.nukkitx.math.vector.Vector3f;



public class Vector3 {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public int getFloorX() {
        return GenericMath.floor(this.x);
    }

    public int getFloorY() {
        return GenericMath.floor(this.y);
    }

    public int getFloorZ() {
        return GenericMath.floor(this.z);
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector3f getVector3f() {
        return Vector3f.from(this.x, this.y + 1.62, this.z);
    }
}
