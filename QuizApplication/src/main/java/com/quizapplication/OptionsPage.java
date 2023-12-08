package com.quizapplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Vector;

public class OptionsPage extends VBox {

    Button infoButton;

    Button[] portionButtons;

    Button history;

    public OptionsPage(){
        setSpacing(20);
        setPadding(new Insets(20, 25, 20, 25));
        initializeInfoButton();
        initializePortionButtons();
        initializeHistoryButton();
    }

    public void initializeInfoButton(){
        infoButton = new Button("Profile");
        infoButton.setPrefSize(50, 50);
        infoButton.setFont(new Font(12));

        infoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Main.stage.setScene(new Scene(infoBox()));
            }
        });
        HBox hBox = new HBox(infoButton);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        getChildren().add(hBox);
    }

    public void initializePortionButtons(){
        ResultSet resultSet = Main.myConnection.getResult("SELECT category_name FROM quiz_category");
        Vector<String> opt = new Vector<>();

        int count = 0;
        try {
            while (resultSet.next()){
                count++;
                opt.add(resultSet.getString(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        portionButtons = new Button[count];
        for(int i = 0, y = 100; i < count; i++, y += 100){
            portionButtons[i] = new Button(opt.elementAt(i));
            portionButtons[i].setPrefSize(250, 50);
            portionButtons[i].setFont(new Font(25));
            portionButtons[i].setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
            portionButtons[i].setTextFill(Color.WHITE);
            portionButtons[i].setCursor(Cursor.HAND);
            int finalI = i;
            portionButtons[i].setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Main.stage.setScene(new Scene(new QuizController(finalI + 1)));
                }
            });
            getChildren().add(portionButtons[i]);
        }
    }

    public void initializeHistoryButton(){
        history = new Button("VIEW HISTORY");
        history.setLayoutX(25);
        history.setLayoutY(420);
        history.setPrefSize(250,50);
        history.setFont(new Font(25));
        history.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        history.setCursor(Cursor.HAND);
        history.setTextFill(Color.WHITE);

        history.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new QuizHistory().getHistoryTable();
            }
        });

        getChildren().add(history);
    }

    private VBox infoBox(){
        VBox vBox = new VBox();

        HBox [] hBoxes = new HBox[4];
        Label [] title = new Label[4];
        Label [] value = new Label[4];
        String [] tstr = {"Name: ", "ID: ", "Email: ", "Password: "};
        String [] vstr = {LoginController.name, "" + LoginController.id, LoginController.email, LoginController.password};
        for(int i = 0; i < 4; i++){
            hBoxes[i] = new HBox();
            title[i] = new Label(tstr[i]);
            title[i].setFont(new Font(25));
            hBoxes[i].getChildren().add(title[i]);
            value[i] = new Label(vstr[i]);
            value[i].setFont(new Font(25));
            value[i].setTextFill(Color.GRAY);
            hBoxes[i].setPadding(new Insets(5, 10, 5, 10));
            hBoxes[i].getChildren().add(value[i]);
            vBox.getChildren().add(hBoxes[i]);
        }
        vBox.setSpacing(20);

        Button logout = new Button("LOG OUT");
        logout.setFont(new Font(20));
        logout.setCursor(Cursor.HAND);
        Button goBack = new Button("Go Back");
        goBack.setCursor(Cursor.HAND);
        goBack.setFont(new Font("Calibri", 20));
        HBox hBox = new HBox(logout, goBack);
        hBox.setPadding(new Insets(10, 20, 20, 10));
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);
        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-page.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load());
                    Main.stage.setScene(scene);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        goBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                    Scene scene = new Scene(new OptionsPage());
                    Main.stage.setScene(scene);
            }
        });

        return vBox;
    }
}
