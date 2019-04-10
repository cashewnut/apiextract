package pers.xyy.deprecatedapi.jdk.model;

import java.util.Objects;

public class JDKDeprecatedAPI {

    private Integer id;
    private String packageName;
    private String className;
    private String methodName;
    private String methodReturnType;
    private String methodArgs;
    private String comment;
    private Integer line;
    private String replacedComment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public String getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
        this.methodArgs = methodArgs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getReplacedComment() {
        return replacedComment;
    }

    public void setReplacedComment(String replaceComment) {
        this.replacedComment = replaceComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JDKDeprecatedAPI that = (JDKDeprecatedAPI) o;
        return Objects.equals(packageName, that.packageName) &&
                Objects.equals(className, that.className) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(methodReturnType, that.methodReturnType) &&
                Objects.equals(methodArgs, that.methodArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className, methodName, methodReturnType, methodArgs);
    }
}
