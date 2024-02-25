package MindustryToolkit.identity;

import arc.scene.Element;
import arc.struct.Seq;
import arc.struct.SnapshotSeq;
import arc.util.Log;

public class SnapshotSeqProxy<T> extends SnapshotSeq<T> {
    public SnapshotSeqProxy() {
    }

    public SnapshotSeqProxy(Seq<T> array) {
        super(array);
    }

    public SnapshotSeqProxy(boolean ordered, int capacity, Class<?> arrayType) {
        super(ordered, capacity, arrayType);
    }

    public SnapshotSeqProxy(boolean ordered, int capacity) {
        super(ordered, capacity);
    }

    public SnapshotSeqProxy(boolean ordered, T[] array, int startIndex, int count) {
        super(ordered, array, startIndex, count);
    }

    public SnapshotSeqProxy(Class<?> arrayType) {
        super(arrayType);
    }

    public SnapshotSeqProxy(int capacity) {
        super(capacity);
    }

    public SnapshotSeqProxy(T[] array) {
        super(array);
    }

    public static <T> SnapshotSeqProxy<T> with(T... array) {
        return new SnapshotSeqProxy<>(array);
    }

    public Seq<T> addNoVerbose(T value) {
        return super.add(value);
    }

    @Override
    public Seq<T> add(T value) {
        Log.info("Add: " + value);
        return super.add(value);
    }
}
