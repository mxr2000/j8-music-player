package data;

import javafx.scene.image.Image;

public class MusicInfoItem {
    String tag;
    String info;
    Image image;
    public MusicInfoItem(String tag, String info, Image image){
        this.tag = tag;
        this.info = info;
        this.image = image;
    }
    public String getDescrition(){
        int length = 30 - tag.length();
        String result = tag;
        for(int i = 0; i < length; i++)
            result += " ";
        return result + info;
    }

    public Image getImage(){
        return image;
    }
}
