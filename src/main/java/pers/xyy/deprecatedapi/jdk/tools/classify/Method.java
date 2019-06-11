package pers.xyy.deprecatedapi.jdk.tools.classify;

public class Method {

    private String packageName;
    private String className;
    private String name;
    private String returnType;
    private String methodArgs;

    public Method() {
    }

    public Method(String packageName, String className, String name, String returnType, String methodArgs) {
        this.packageName = packageName;
        this.className = className;
        this.name = name;
        this.returnType = returnType;
        this.methodArgs = methodArgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
        this.methodArgs = methodArgs;
    }

    @Override
    public String toString() {
        return "Method{" +
                "packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", methodArgs='" + methodArgs + '\'' +
                '}';
    }
}
