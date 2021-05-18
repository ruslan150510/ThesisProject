package main.api.response;

public class TagsListResponse {
    private String name;

    private double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
//    {
//        "tags":
//[
//        {"name":"Java", "weight":1},
//        {"name":"Spring", "weight":0.56},
//        {"name":"Hibernate", "weight":0.22},
//        {"name":"Hadoop", "weight":0.17},
//]
//    }
}
