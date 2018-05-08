package it.polimi.ingsw.gridContainer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class GridConcreteFactory implements GridFactory{

    private static final String pathToGetData = "./src/jsonFiles/GridsPaths.json";
    private static final int gridsToPlayer =4;

    private ArrayList<String> dataPaths;

    public GridConcreteFactory(){
        Gson gson= new Gson();
        File file = new File(pathToGetData);
        Scanner scanner=null;
        try {
            scanner= new Scanner(file);
            while (scanner.hasNextLine()){
                String toParse = new String("");
                toParse= toParse + scanner.nextLine();
                Type dataPathsType = new TypeToken<ArrayList<String>>(){}.getType();
                dataPaths=gson.fromJson(toParse, dataPathsType);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }






    }
}
