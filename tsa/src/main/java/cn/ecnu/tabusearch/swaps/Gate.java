package cn.ecnu.tabusearch.swaps;

import java.util.Objects;

public class Gate {
    private Integer target;
    private Integer control;
    private String type;
    private Double angle;

    public Gate() {
    }

    public Gate(Gate g) {
        this.target=g.getTarget();
        this.control=g.getControl();
        this.type=g.getType();
        this.angle=g.getAngle();
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getControl() {
        return control;
    }

    public void setControl(Integer control) {
        this.control = control;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate gate = (Gate) o;
        return Objects.equals(target, gate.target) &&
                Objects.equals(control, gate.control) &&
                Objects.equals(type, gate.type) &&
                Objects.equals(angle, gate.angle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, control, type, angle);
    }

    @Override
    public String toString() {
        return "Gate{" +
                "target=" + target +
                ", control=" + control +
                ", type='" + type + '\'' +
                ", angle=" + angle +
                '}';
    }
}
