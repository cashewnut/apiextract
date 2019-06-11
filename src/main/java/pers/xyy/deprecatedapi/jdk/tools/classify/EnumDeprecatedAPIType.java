package pers.xyy.deprecatedapi.jdk.tools.classify;


public enum EnumDeprecatedAPIType {
    REMOVE_DUP(11),
    REPLACE(12),
    OPEN_ARGS_TRANSFER(21),
    OPEN_ARGS(22),
    CLOSE_ARGS_TRANSFER(31),
    CLOSE_ARGS(32),
    COMPACT_ARGS_TRANSFER(41),
    COMPACT_ARGS(42),
    COMPACT_API_TRANSFER(51),
    COMPACT_API(52),
    SPLIT_API_TRANSFER(61),
    SPLIT_API(62),
    OTHER_TRANSFER(71),
    OTHER(72);

    private int value;

    EnumDeprecatedAPIType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
