package data;

public class MusicWrapper extends Music {
    boolean isMusic;
    MusicList list;
    public MusicWrapper(){
        super("root", "default");
        this.list = null;
        isMusic = false;
    }
    public MusicWrapper(String listName, MusicList list){
        super(listName, "default");
        this.list = list;
        isMusic = false;
    }
    public MusicWrapper(String musicName, String musicPath){
        super(musicName, musicPath);
        isMusic = true;
    }
    public MusicWrapper(Music music){
        super(music.getName(), music.getPath());
        isMusic = true;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public MusicList getList() {
        return list;
    }
}
