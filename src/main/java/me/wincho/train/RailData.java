package me.wincho.train;

public enum RailData {
    SLOW(0),
    STATION1(1),
    STATION2(2),
    STATION3(3),
    STATION4(4),
    STATION5(5),
    STATION6(6),
    STATION7(7),
    STATION8(8),
    STATION9(9),
    STATION10(10);
    private final int value;

    RailData(int value) {
        this.value = value;
    }

    public int getID() {
        return value;
    }
    public static int getIDbyRailData(RailData railData) {
        return railData.getID();
    }

    public static RailData fromId(int id) {
        for (RailData type : values()) {
            if (type.getID() == id) {
                return type;
            }
        }
        return null;
    }
}
