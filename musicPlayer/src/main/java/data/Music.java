package data;

public class Music {
    String name;
    String path;
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setPath(String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }
    public Music(String name, String path){
        this.name = name;
        this.path = path;
    }
    public Music(){
        this("default name", "default path");
    }
}
