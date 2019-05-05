package pers.xyy.deprecatedapi.jdk.model;

import java.util.List;

public class Args {

    private String type;
    private String name;
    private Boolean related;
    private List<String> operations;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRelated() {
        return related;
    }

    public void setRelated(Boolean related) {
        this.related = related;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }
}
