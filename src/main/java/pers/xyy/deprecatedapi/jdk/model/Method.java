package pers.xyy.deprecatedapi.jdk.model;

import java.util.List;

public class Method {

    private String invoker;
    private String name;
    private List<Args> args;
    private Boolean related;

    public Boolean getRelated() {
        return related;
    }

    public void setRelated(Boolean related) {
        this.related = related;
    }

    public String getInvoker() {
        return invoker;
    }

    public void setInvoker(String invoker) {
        this.invoker = invoker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Args> getArgs() {
        return args;
    }

    public void setArgs(List<Args> args) {
        this.args = args;
    }
}
