package data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListManager {
    List<MusicList> lists = new ArrayList<MusicList>();
    public void load(File file){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            document = builder.parse(getClass().getClassLoader().getResource("books.xml").toString());
            document.getDocumentElement().normalize();
            System.out.println("root element:");

            NodeList lists = document.getElementsByTagName("list");
            for(int i = 0; i < lists.getLength(); i++){
                Node node = lists.item(i);
                System.out.println(node.getNodeName());
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element)node;

                    System.out.println("name: " + element.getAttribute("name"));
                    System.out.println("icon: " + element.getAttribute("icon"));
                    MusicList newList = new MusicList(element.getAttribute("name"), element.getAttribute("icon"));
                    NodeList characters = element.getChildNodes();
                    for(int j = 0; j < characters.getLength(); j++){
                        Node n = characters.item(j);
                        if(n.getNodeType() == Node.ELEMENT_NODE){
                            Element song = (Element)n;
                            System.out.println("Song name: " + song.getTextContent());
                            System.out.println("Song path: " + song.getAttribute("path"));
                            newList.addMusic(new Music(song.getTextContent(), song.getAttribute("path")));
                        }
                    }
                    this.lists.add(newList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void save(File file) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("lists");
        document.appendChild(root);

        int listNum = lists.size();
        for(int i = 0; i < listNum; i++){
            Element list = document.createElement("list");
            MusicList musicList = lists.get(i);
            root.appendChild(list);
            list.setAttribute("name", musicList.getName());
            list.setAttribute("icon", "default path");

            for(int j = 0; j < musicList.getLength(); j++){
                Music music = musicList.getMusic(j);
                Element song = document.createElement("song");
                song.setTextContent(music.getName());
                song.setAttribute("path", music.getPath());
                list.appendChild(song);
            }
        }
        /*
        //add blur
        Element blur = document.createElement("list");
        root.appendChild(blur);

        blur.setAttribute("name", "Blur");
        blur.setAttribute("icon", "default path");

        Element ongOng = document.createElement("song");
        ongOng.setTextContent("Ong Ong");
        ongOng.setAttribute("path", "D:\\music");
        blur.appendChild(ongOng);

        Element coffeeAndTV = document.createElement("song");
        coffeeAndTV.setTextContent("Coffee and TV");
        coffeeAndTV.setAttribute("path", "D:\\music");
        blur.appendChild(coffeeAndTV);

        //add oasis
        Element oasis = document.createElement("list");
        root.appendChild(oasis);

        oasis.setAttribute("name", "Oasis");
        oasis.setAttribute("icon", "default path");

        Element weDont = document.createElement("song");
        weDont.setTextContent("We don't look back in anger");
        weDont.setAttribute("path", "D:\\music");
        oasis.appendChild(weDont);

        Element wonderwall = document.createElement("song");
        wonderwall.setTextContent("Wonderwall");
        wonderwall.setAttribute("path", "D:\\music");
        oasis.appendChild(wonderwall);
        */
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("intermediaries/books.xml"));
        result = new StreamResult(getClass().getClassLoader().getResource("books.xml").toString());
        transformer.transform(source, result);
    }
    public MusicList getMusicList(String name){
        for(int i = 0; i < lists.size(); i++)
            if(lists.get(i).getName() == name)
                return lists.get(i);
        return null;
    }
    public int getLength(){
        return lists.size();
    }
    public MusicList getMusicList(int index){
        return lists.get(index);
    }
    public void addList(String name){
        MusicList list = new MusicList(name, "default path");
        lists.add(list);
    }
    public void addList(MusicList list){
        this.lists.add(list);
    }
    public void removeList(String name){
        for(int i = 0; i < lists.size(); i++)
            if(lists.get(i).getName() == name){
                lists.remove(i);
                return;
            }
    }
}
