package pers.xyy.deprecatedapi.jdk.tools.classify;



public enum EnumDeprecatedAPIType2 {
    TYPE1(1),
    TYPE2(2),
    TYPE3(3),
    TYPE4(4),
    TYPE5(5),
    TYPE6(6),
    TYPE7(7);

    private int value;

    EnumDeprecatedAPIType2(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
