package pers.xyy.deprecatedapi.study;

public class Project {

    private Integer id;
    private String repoName;
    private String localAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = "/home/fdse/data/repo/" + localAddress;
    }
}
