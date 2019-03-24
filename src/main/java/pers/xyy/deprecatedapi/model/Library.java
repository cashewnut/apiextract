package pers.xyy.deprecatedapi.model;

public class Library {

    private Integer id;
    private String name;
    private String version;
    private String path;

    public Library() {
    }

    public Library(Integer id, String name, String version, String path) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
