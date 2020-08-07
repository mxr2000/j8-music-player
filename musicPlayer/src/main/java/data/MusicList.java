package data;
import java.util.ArrayList;
import java.util.List;

public class MusicList {
    List<Music> musicList = new ArrayList<Music>();
    String name;
    String icon;
    public MusicList(String name, String icon){
        this.name = name;
        this.icon = icon;
    }
    public Music getMusic(int index){
        return musicList.get(index);
    }
    public void addMusic(Music music){
        musicList.add(music);
    }
    public void removeMusic(String name){
        for(Music music : musicList)
            if(name == music.getName()){
                musicList.remove(music);
                return;
            }

    }
    public String getName(){
        return name;
    }
    public int getLength(){
        return musicList.size();
    }
}
