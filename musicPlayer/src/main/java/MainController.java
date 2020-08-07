import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import data.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.mpatric.mp3agic.*;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import sun.font.FontFamily;

import java.io.*;
import java.util.Optional;

public class MainController {
    Timeline timer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            timeout();
        }
    }));
    Stage stage;
    Stage dialogAddList;
    DialogAddListController dialogAddListController;
    @FXML
    ImageView imgPlayPause;
    @FXML
    Button btnPlayPause, btnStop, btnNext, btnVolume;
    @FXML
    ImageView imgCover, imgVolume;
    @FXML
    Slider sliderVolume, sliderTime;
    @FXML
    Label lbTime, lbSongAlbum, lbSongName;
    @FXML
    TreeView<MusicWrapper> treeList;
    @FXML
    ListView<MusicInfoItem> listViewMusicInfo;
    @FXML
    MenuItem menuItemAddList;

    ContextMenu contextMenuSong, contextMenuList, contextMenuView;
    Image imgSong = new Image("images/song.png", 20, 20, true, true);
    Image imgFolder = new Image("images/folder.png", 20, 20, true, true);
    MediaPlayer player;
    Media media;

    @FXML
    void onAddListAction(){
        dialogAddListController.setMusicListManager(listManager);
        dialogAddList.show();
        listManager = dialogAddListController.getMusicListManager();
        if(dialogAddListController.isUpdated())
            updateTree(false);
    }
    @FXML
    void onBtnPlayPauseClicked(){
        if(media != null){
            if(player.getStatus() == MediaPlayer.Status.PLAYING){
                player.pause();
                imgPlayPause.setImage(new Image("images/play.png"));
            }else{
                player.play();
                imgPlayPause.setImage(new Image("images/pause.png"));
            }

        }
    }
    @FXML
    void onBtnStopClicked(){
        if(media != null){
            player.stop();
        }
    }
    @FXML
    void onBtnNextClicked(){
        File file = nextMedia();
        loadMusic(file);
        process(file);
    }
    @FXML
    void onBtnVolumeClicked(){
        if(media != null){
            if(player.getVolume() > 0.01){
                sliderVolume.setValue(0);
                imgVolume.setImage(new Image("images/mute.png"));
            }else{
                sliderVolume.setValue(50);
                imgVolume.setImage(new Image("images/volume.png"));
            }
        }
    }
    void timeout(){
        if(media != null && player.getStatus() == MediaPlayer.Status.PLAYING){
            double currentMediaDuration = media.getDuration().toSeconds();
            sliderTime.setValue(player.getCurrentTime().toSeconds() / currentMediaDuration * sliderTime.getMax());
            lbTime.setText(toRightFormat(player.getCurrentTime().toSeconds()) + "/" + toRightFormat(media.getDuration().toSeconds()));
        }
    }
    String toRightFormat(double seconds){
        int s = (int)seconds;
        int m = s / 60;
        int finalS = s % 60;
        if(finalS < 10)
            return "" + m + ":0" + finalS;
        else
            return "" + m + ":" + finalS;
    }

    String tranfromToLeaglPath(String path){
        String result = path.replaceAll(" ", "%20");
        result = result.replace('\\', '/');
        return "file:///" + result;
    }

    void setState(Stage stage, Stage dialogStage, DialogAddListController controller){
        this.dialogAddList = dialogStage;
        this.stage = stage;
        this.dialogAddListController = controller;
    }

    EventHandler<MouseEvent> treeItemClickHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if(event.getClickCount() == 2){
                TreeItem<MusicWrapper> item = treeList.getSelectionModel().getSelectedItem();
                if(item != null && item.getValue().isMusic()){
                    loadMusic(item);
                    processMusic(item);
                }
            }
        }
    };
    void loadMusic(TreeItem<MusicWrapper> treeItem){
        if(treeItem.getParent().getValue().getName() == currentMusicList.getName()){
            currentMusicIndex = treeItem.getParent().getChildren().indexOf(treeItem);
            loadMusic(new File(treeItem.getValue().getPath() + "/" + treeItem.getValue().getName()));
        }else{
            currentMusicIndex = treeItem.getParent().getChildren().indexOf(treeItem);
            currentMusicList = treeItem.getParent().getValue().getList();
            loadMusic(new File(treeItem.getValue().getPath() + "/" + treeItem.getValue().getName()));
        }
    }
    void processMusic(TreeItem<MusicWrapper> treeItem){
        process(new File(treeItem.getValue().getPath() + "/" + treeItem.getValue().getName()));
    }
    void loadMusic(Music music){
        loadMusic(new File(music.getPath() + "/" + music.getName()));
    }
    void processMusic(Music music){
        process(new File(music.getPath() + "/" + music.getName()));
    }
    void loadMusic(File file){
        if(player != null){
            player.stop();
            player.dispose();
        }
        System.out.println(tranfromToLeaglPath(file.getAbsolutePath()));
        media = new Media(tranfromToLeaglPath(file.getAbsolutePath()));
        player = new MediaPlayer(media);

        player.volumeProperty().bind(sliderVolume.valueProperty().divide(100));
        imgPlayPause.setImage(new Image("images/pause.png"));
        player.play();
        player.setOnEndOfMedia(new Runnable() {
            public void run() {
                if(currentMusicIndex == currentMusicList.getLength() - 1)
                    currentMusicIndex = 0;
                else
                    ++currentMusicIndex;
                loadMusic(currentMusicList.getMusic(currentMusicIndex));
                processMusic(currentMusicList.getMusic(currentMusicIndex));
            }
        });
    }
    void process(File file){
        try{
            Mp3File mp3file = new Mp3File(file);
            System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
            System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream("a.jpeg"));
            if(mp3file.getId3v2Tag().getAlbumImage() != null)
                outputStream.write(mp3file.getId3v2Tag().getAlbumImage());
            outputStream.close();
            Image image = new Image(new FileInputStream("a.jpeg"));
            System.out.println(image.getHeight());
            imgCover.setImage(image);

            listViewMusicInfo.getItems().clear();
            listViewMusicInfo.getItems().add(new MusicInfoItem("Mp3 length: ", String.valueOf(mp3file.getLengthInSeconds()), new Image("images/length.png")));
            listViewMusicInfo.getItems().add(new MusicInfoItem("Artist: ", mp3file.getId3v2Tag().getArtist(), new Image("images/artist.png")));
            listViewMusicInfo.getItems().add(new MusicInfoItem("Has ID3v1 tag?: ", (mp3file.hasId3v1Tag() ? "YES" : "NO"), new Image("images/tag1.png")));
            listViewMusicInfo.getItems().add(new MusicInfoItem("Has ID3v2 tag?: ", (mp3file.hasId3v2Tag() ? "YES" : "NO"), new Image("images/tag2.png")));

            lbSongName.setText(file.getName());
            lbSongAlbum.setText(mp3file.getId3v2Tag().getAlbum());
            /*listViewMusicInfo.getItems().add("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
            listViewMusicInfo.getItems().add("Sample rate: " + mp3file.getSampleRate() + " Hz");
            listViewMusicInfo.getItems().add("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
            listViewMusicInfo.getItems().add("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
            listViewMusicInfo.getItems().add("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));*/

            listViewMusicInfo.setCellFactory(new Callback<ListView<MusicInfoItem>, ListCell<MusicInfoItem>>() {
                @Override
                public ListCell<MusicInfoItem> call(ListView<MusicInfoItem> param) {
                    return new ListCell<MusicInfoItem>(){
                        @Override
                        protected void updateItem(MusicInfoItem item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item != null && !empty){
                                setText(item.getDescrition());
                                setGraphic(new ImageView(item.getImage()));
                                setFont(Font.font("Times New Roman"));
                            }else{
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
            System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
            System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
            System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
            System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
            System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
            System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ListManager listManager = new ListManager();

    public void updateTree(boolean changeCurrentList){
        TreeItem<MusicWrapper> root = new TreeItem(new MusicWrapper());
        treeList.setRoot(root);
        listManager.load(new File("intermediaries/books.xml"));
        for(int i = 0; i < listManager.getLength(); i++) {
            MusicList musicList = listManager.getMusicList(i);
            TreeItem<MusicWrapper> list = new TreeItem<MusicWrapper>(new MusicWrapper(musicList.getName(), musicList));
            root.getChildren().add(list);

            for (int j = 0; j < musicList.getLength(); j++) {
                Music music = musicList.getMusic(j);
                TreeItem<MusicWrapper> song = new TreeItem<MusicWrapper>(new MusicWrapper(music));

                list.getChildren().add(song);
            }
            System.out.println("list name = " + musicList.getName());
            if (changeCurrentList && musicList.getName().equals("default"))
                currentMusicList = musicList;
        }

        treeList.setCellFactory(new Callback<TreeView<MusicWrapper>, TreeCell<MusicWrapper>>() {
            public TreeCell<MusicWrapper> call(TreeView<MusicWrapper> param) {
                return new TreeCell<MusicWrapper>(){
                    @Override
                    protected void updateItem(MusicWrapper item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null && !empty){
                            this.setText(item.getName());
                            if(item.isMusic())
                                this.setGraphic(new ImageView(imgSong));
                            else
                                this.setGraphic(new ImageView(imgFolder));
                        }else{
                            this.setText(null);
                            this.setGraphic(null);
                        }
                        this.setOnMouseClicked(treeItemClickHandler);
                    }
                };
            }
        });
    }
    public void init(){
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        updateTree(true);
        currentMusicIndex = -1;
        File file = nextMedia();
        loadMusic(file);
        process(file);
        setContextMenu();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                try{
                    System.out.println("saved");
                    listManager.save(new File("intermediaries/books.xml"));
                    listManager.save(new File(getClass().getClassLoader().getResource("books.xml").toString()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        sliderTime.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(sliderTime.getValue());
                player.stop();
                player.setStartTime(Duration.seconds(sliderTime.getValue() / 100 * media.getDuration().toSeconds()));
                player.play();
            }
        });
    }
    void setContextMenu(){
        MenuItem menuItemAddSong = new MenuItem("Add a song");
        MenuItem menuItemEditSong = new MenuItem("Edit a song");
        MenuItem menuItemRemoveSong = new MenuItem("Remove this song");
        MenuItem menuItemEditList = new MenuItem("Edit a list");
        MenuItem menuItemRemoveList = new MenuItem("Remove this list");

        MenuItem menuItemAddList1 = new MenuItem("Add a list");
        MenuItem menuItemAddList2 = new MenuItem("Add a list");
        MenuItem menuItemAddList3 = new MenuItem("Add a list");
        contextMenuSong = new ContextMenu(menuItemEditSong, menuItemRemoveSong, menuItemAddList1);
        contextMenuList = new ContextMenu(menuItemAddSong, menuItemRemoveList, menuItemEditList, menuItemAddList2);
        contextMenuView = new ContextMenu(menuItemAddList3);
        treeList.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            public void handle(ContextMenuEvent e) {
                if(treeList.getSelectionModel().getSelectedItem() == null)
                    contextMenuView.show(stage, e.getScreenX(), e.getScreenY());
                else{
                    if(treeList.getSelectionModel().getSelectedItem().getParent() == treeList.getRoot())
                        contextMenuList.show(stage, e.getScreenX(), e.getScreenY());
                    else
                        contextMenuSong.show(stage, e.getScreenX(), e.getScreenY());
                }
            }
        });
        menuItemAddSong.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ChoiceDialog<String> dialog = new ChoiceDialog<>();
                dialog.show();
            }
        });
        menuItemRemoveSong.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                TreeItem<MusicWrapper> item = treeList.getSelectionModel().getSelectedItem();
                listManager.getMusicList(item.getParent().getValue().getName()).removeMusic(item.getValue().getName());
                item.getParent().getChildren().remove(item);
            }
        });
        menuItemAddList1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                addANewList();
            }
        });
        menuItemAddList2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                addANewList();
            }
        });
        menuItemAddList3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                addANewList();
            }
        });
        menuItemRemoveList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<MusicWrapper> item = treeList.getSelectionModel().getSelectedItem();
                listManager.removeList(item.getValue().getName());
                item.getParent().getChildren().remove(item);
            }
        });
    }

    void addANewList(){
        TextInputDialog dialog = new TextInputDialog();
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            listManager.addList(result.get());
            System.out.println(result.get());
        }
    }

    //Media Player
    MusicList currentMusicList;
    int currentMusicIndex;
    File nextMedia(){
        if(currentMusicIndex == currentMusicList.getLength() - 1)
            currentMusicIndex = -1;
        System.out.println(currentMusicList.getMusic(currentMusicIndex + 1).getPath() + "/" +
                currentMusicList.getMusic(currentMusicIndex + 1).getName());
        File file = new File(currentMusicList.getMusic(currentMusicIndex + 1).getPath() + "/" +
                currentMusicList.getMusic(currentMusicIndex + 1).getName());
        currentMusicIndex ++;
        return file;
    }
}
