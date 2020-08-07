import data.ListManager;
import data.Music;
import data.MusicList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DialogAddListController {
    Stage stage;
    ListManager listManager;
    boolean isUpdated;
    void setStage(Stage stage){
        this.stage = stage;
    }
    boolean isUpdated(){
        return isUpdated;
    }
    void setMusicListManager(ListManager listManager){
        isUpdated = false;
        filesToBeAdded = new ArrayList<File>();
        this.listManager = listManager;
        for(int i = 0; i < listManager.getLength(); i++){
            cbItemCurrentLists.getItems().add(listManager.getMusicList(i).getName());
        }
    }
    ListManager getMusicListManager(){
        return listManager;
    }
    @FXML
    Button btnImportItem, btnImportPath, btnPathNewList, btnListNewList, btnAddItem, btnAddPath;
    @FXML
    RadioButton rbListToCurrent, rbListToANew, rbPathToCurrent, rbPathToANew;
    @FXML
    ComboBox cbItemCurrentLists, cbPathCurrentLists;
    @FXML
    ListView listItemsToBeAdded, listPathsToBeAdded;
    @FXML
    TextField tfItemNewListName, tfPathNewListName;

    @FXML
    void onBtnAddItemClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select music");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp3", "*.mp3"));
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        filesToBeAdded.addAll(files);
        for(File file : files)
            listItemsToBeAdded.getItems().add(file.getName());
    }
    List<File> filesToBeAdded;
    @FXML
    void onBtnImportItemClicked(){
        isUpdated = true;
        if(rbListToCurrent.isSelected()){
            if(cbItemCurrentLists.getSelectionModel().getSelectedItem() != null){
                System.out.println(cbItemCurrentLists.getSelectionModel().getSelectedItem().toString());
                MusicList list = listManager.getMusicList(cbItemCurrentLists.getSelectionModel().getSelectedItem().toString());
                for(int i = 0; i < filesToBeAdded.size(); i++){
                    File file = filesToBeAdded.get(i);
                    Music music = new Music(file.getName(), file.getParent());
                    list.addMusic(music);
                }
                try{
                    listManager.save(new File("intermediaries/books.xml"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            if(tfItemNewListName.getText() != ""){
                MusicList list = new MusicList(tfItemNewListName.getText(), "default path");
                for(int i = 0; i < filesToBeAdded.size(); i++){
                    File file = filesToBeAdded.get(i);
                    Music music = new Music(file.getName(), file.getParent());
                    list.addMusic(music);
                }
                listManager.addList(list);
                try{
                    listManager.save(new File("intermediaries/books.xml"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void init(){
        ToggleGroup itemToggleGroup = new ToggleGroup();
        itemToggleGroup.getToggles().addAll(rbListToCurrent, rbListToANew);
        rbListToCurrent.setSelected(true);
        cbItemCurrentLists.disableProperty().bind(rbListToANew.selectedProperty());
        tfItemNewListName.disableProperty().bind(rbListToCurrent.selectedProperty());
        btnListNewList.disableProperty().bind(rbListToCurrent.selectedProperty());
    }
}
