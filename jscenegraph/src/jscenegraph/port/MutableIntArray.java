package jscenegraph.port;

import jscenegraph.database.inventor.SbVec3f;
import jscenegraph.port.memorybuffer.FloatMemoryBuffer;

public class MutableIntArray {
    private int[] valuesArray;

    private int delta;

    public static MutableIntArray from(IntArray values) {
        if ( values == null ) {
            return null;
        }
        return new MutableIntArray(values);
    }

    public static MutableIntArray from(IntArrayPtr values) {
        if ( values == null ) {
            return null;
        }
        return new MutableIntArray(values);
    }

    private MutableIntArray(IntArray other) {
        valuesArray = other.values();
        this.delta = other.delta();
    }

    private MutableIntArray(MutableIntArray other, int delta) {
        valuesArray = other.valuesArray;
        this.delta = other.delta + delta;
    }

    private MutableIntArray(IntArrayPtr other) {
        valuesArray = other.getValues();
        this.delta = other.getStart();
    }

    public void plusPlus() {
        delta++;
    }

    public int get() {
        return valuesArray[delta];
    }

    public MutableIntArray plus(int num) {
        return new MutableIntArray(this,num);
    }

    public boolean lessThan(MutableIntArray other) {
        if ( other == null ) {
            return false;
        }
        if ( other.valuesArray != valuesArray) {
            throw new IllegalArgumentException();
        }
        return delta < other.delta;
    }

    public int getPlusPlus() {
        int ret_val = get();
        plusPlus();
        return ret_val;
    }

    public boolean plusLessThan(int i, MutableIntArray other) {
        if ( other == null ) {
            return false;
        }
        if ( other.valuesArray != valuesArray) {
            throw new IllegalArgumentException();
        }
        return delta + i < other.delta;
    }
}
