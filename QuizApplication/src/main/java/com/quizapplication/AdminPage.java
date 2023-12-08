package com.quizapplication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Objects;

public class AdminPage extends VBox {
    Stage delStage;
    Button [] optionButtons = new Button[7];

    public AdminPage(){
        initializeOptionsButtons();
        initializeLogoutButton();
    }

    private void initializeOptionsButtons(){
        setSpacing(10);
        setPadding(new Insets(20, 15, 20, 15));
        String [] opt = {"Add Student", "Delete Student", "Edit Student", "Add Category", "Add Questions",
        "Delete Category", "Delete Questions"};
        for(int i = 0; i < 7; i++){
            optionButtons[i] = new Button(opt[i]);
            optionButtons[i].setFont(new Font(24));
            optionButtons[i].setPrefWidth(300);
            getChildren().add(optionButtons[i]);
        }
        optionButtons[0].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Main.stage.setScene(new Scene(new SignUpPage(false)));
            }
        });
        optionButtons[1].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(deleteBox()));
                delStage.show();
            }
        });
        optionButtons[2].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(editStudent()));
                delStage.show();
            }
        });
        optionButtons[3].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(addCategory()));
                delStage.show();
            }
        });
        optionButtons[4].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(addQuizPage()));
                delStage.show();
            }
        });
        optionButtons[5].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(deleteCategory()));
                delStage.show();
            }
        });
        optionButtons[6].setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                delStage = new Stage();
                delStage.setScene(new Scene(deleteQuestions()));
                delStage.initModality(Modality.APPLICATION_MODAL);
                delStage.show();
            }
        });
    }

    private void initializeLogoutButton(){
        Button logout = new Button("LOGOUT");
        logout.setFont(new Font(26));
        logout.setPrefWidth(300);
        getChildren().add(logout);

        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-page.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load());
                    Main.stage.setScene(scene);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public VBox deleteBox(){
        VBox vBox = new VBox();

        Label idLabel = new Label("Student ID");
        idLabel.setFont(new Font(20));

        TextField idBox = new TextField();
        idBox.setFont(new Font(20));

        Label name = new Label("");
        name.setFont(new Font(20));

        Label email = new Label("");
        email.setFont(new Font(20));

        Button delete = new Button("DELETE STUDENT");
        delete.setFont(new Font(28));
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!name.getText().isEmpty()){
                    String query = "DELETE FROM student WHERE student_id = " + Integer.parseInt(idBox.getText()) + ";";
                    Main.myConnection.runQuery(query);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Success!");
                    alert.setContentText("Student with ID '" + idBox.getText() + "' deleted");
                    alert.show();
                    alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent dialogEvent) {
                            delStage.close();
                        }
                    });
                }
            }
        });

        idBox.textProperty().addListener((observable, oldValue, newValue) -> {;
            Platform.runLater(()->{
                if(!Objects.equals(oldValue, newValue)){
                    if(idBox.getText().isEmpty()) {
                            name.setText("");
                            email.setText("");
                    }
                    else {
                        String query = "SELECT first_name, last_name, email FROM student WHERE student_id = " + idBox.getText() + ";";
                        ResultSet resultSet = Main.myConnection.getResult(query);
                        try {
                            int i = 0;
                            while (resultSet.next()){
                                name.setText(resultSet.getString(1) + " " + resultSet.getString(2));
                                email.setText(resultSet.getString(3));
                                i++;
                            }
                            if(i == 0){
                                name.setText("");
                                email.setText("");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        });

        vBox.getChildren().addAll(idLabel, idBox, name, email, delete);
        vBox.setSpacing(15);
        vBox.setPadding(new Insets(20, 15, 20,15));
        return vBox;
    }

    public VBox addQuizPage(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20,15,20,15));
        vBox.setSpacing(20);
        addQuestions(vBox);

        return vBox;
    }

    public VBox addCategory(){
        VBox vBox = new VBox();
        vBox.setSpacing(25);

        Label label = new Label("Category Name");
        label.setFont(new Font(18));
        TextField textField = new TextField();
        textField.setFont(new Font(18));
        VBox vBox1 = new VBox(label, textField);

        Button add = new Button("ADD");
        add.setFont(new Font(18));

        vBox.getChildren().addAll(vBox1, add);

        add.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(textField.getText().matches("^[a-zA-Z\\s]+$") && !textField.getText().isEmpty()){
                    Main.myConnection.runQuery("INSERT INTO quiz_category (category_name) VALUES ('" + textField.getText() + "');");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Success!");
                    alert.setContentText("Category " + textField.getText() + " added successfully");
                    alert.show();
                    alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent dialogEvent) {
                            delStage.close();
                        }
                    });
                }
            }
        });

        return vBox;
    }

    public VBox deleteCategory(){
        VBox vBox = new VBox();
        vBox.setSpacing(25);
        vBox.setPadding(new Insets(20, 25, 20, 25));
        Label label = new Label("Category Name");
        label.setFont(new Font(18));
        ComboBox<String> comboBox = new ComboBox<>();
        ResultSet resultSet = null;
        try {
            resultSet = Main.myConnection.getResult("SELECT category_name FROM quiz_category;");
            while (resultSet.next()){
                comboBox.getItems().add(resultSet.getString(1));
                comboBox.setValue(resultSet.getString(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        VBox vBox1 = new VBox(label, comboBox);

        Button del = new Button("DELETE");
        del.setFont(new Font(18));
        del.setPrefWidth(100);

        del.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ResultSet resultSet1 = Main.myConnection.getResult("SELECT category_id FROM quiz_category WHERE category_name = '" + comboBox.getValue() + "';");
                try {
                    resultSet1.next();
                    String query = "DELETE FROM quiz_category WHERE category_id = " + resultSet1.getInt(1) + ";";
                    Main.myConnection.runQuery(query);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Success!");
                    alert.setContentText("Category " + comboBox.getValue() + " Deleted Successfully");
                    alert.show();
                    alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent dialogEvent) {
                            delStage.close();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        vBox.getChildren().addAll(vBox1, del);

        return vBox;
    }

    public void addQuestions(VBox vBox){
        ComboBox<String> comboBox = new ComboBox<>();
        ResultSet resultSet = Main.myConnection.getResult("SELECT category_name FROM quiz_category;");
        try {
            while (resultSet.next())
                comboBox.getItems().add(resultSet.getString(1));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        comboBox.setValue(comboBox.getItems().get(1));

        Label question = new Label("Write Question:");
        TextArea textArea = new TextArea();
        textArea.setPrefSize(300, 150);
        VBox vBox1 = new VBox(question, textArea);

        VBox vBox2 = new VBox();
        vBox2.setSpacing(3);
        Label optLabel = new Label("Options");
        vBox2.getChildren().add(optLabel);
        TextField [] options = new TextField[4];
        for(int i = 0; i < 4; i++){
            options[i] = new TextField();
            vBox2.getChildren().add(options[i]);
        }

        Label ans = new Label("Right Answer: ");
        ComboBox<Integer> answer = new ComboBox<>();
        answer.setValue(0);
        answer.getItems().addAll(0, 1, 2, 3);
        HBox hBox = new HBox(ans, answer);

        Button add = new Button("Add Question");
        add.setFont(new Font(15));
        add.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ResultSet resultSet = Main.myConnection.getResult("SELECT category_name FROM quiz_category;");
                int type = 0;
                try {
                    while (resultSet.next()){
                        type++;
                        if(Objects.equals(comboBox.getValue(), resultSet.getString(1)))
                            break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                String query = "INSERT INTO quiz_questions (statement, option1, option2, option3, option4, answer, quiz_type) VALUES " +
                        "('" + textArea.getText() +"', '" + options[0].getText() + "', '" + options[1].getText() + "', '" + options[2].getText() + "', '" + options[3].getText() + "', '"+ options[answer.getValue()].getText() +"',"+type+" );";
                Main.myConnection.runQuery(query);
                System.out.println(query);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success!");
                alert.setContentText("Question added successfuly to " + comboBox.getValue() + " Portion");
                alert.show();
                alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent dialogEvent) {
                        delStage.close();
                    }
                });
            }
        });

        vBox.getChildren().addAll(comboBox, vBox1, vBox2, hBox, add);
    }

    public VBox deleteQuestions(){
        VBox vBox = new VBox();
        vBox.setPrefWidth(400);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20, 25, 20, 25));

        ComboBox<String> comboBox = new ComboBox<>();
        ResultSet resultSet = Main.myConnection.getResult("SELECT category_name FROM quiz_category;");
        try {
            while (resultSet.next())
                comboBox.getItems().add(resultSet.getString(1));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        comboBox.setValue(comboBox.getItems().get(1));

        TableView<Questions> tableView = new TableView<>();

        TableColumn<Questions, String> tableColumn = new TableColumn<>("QUESTIONS");
        tableColumn.setCellValueFactory(new PropertyValueFactory<Questions, String>("question"));

        tableView.getColumns().add(tableColumn);

        ObservableList<Questions> observableList = FXCollections.observableArrayList();
        String query = "SELECT category_id FROM quiz_category WHERE category_name = '" + comboBox.getValue() + "';";
        resultSet = Main.myConnection.getResult(query);
        try {
            resultSet.next();
            query = "SELECT statement FROM quiz_questions WHERE quiz_type = " + resultSet.getInt(1) + ";";
            resultSet = Main.myConnection.getResult(query);
            while (resultSet.next()){
                observableList.add(new Questions(resultSet.getString(1)));
            }
            tableView.setItems(observableList);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fillTable(comboBox, tableView);
            }
        });

        tableView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCombination keyCombination = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
                if(keyCombination.match(keyEvent)){
                    String qst = tableView.getSelectionModel().getSelectedItem().getQuestion();
                    String query = "DELETE FROM quiz_questions WHERE statement = '" + qst + "';";
                    Main.myConnection.runQuery(query);
                    fillTable(comboBox, tableView);
                }
            }
        });

        vBox.getChildren().addAll(comboBox, tableView);
        return vBox;
    }

    public void fillTable(ComboBox<String> comboBox, TableView<Questions> tableView){
        tableView.getItems().clear();
        ObservableList<Questions> observableList = FXCollections.observableArrayList();
        String query = "SELECT category_id FROM quiz_category WHERE category_name = '" + comboBox.getValue() + "';";
        ResultSet resultSet = Main.myConnection.getResult(query);
        try {
            resultSet.next();
            query = "SELECT statement FROM quiz_questions WHERE quiz_type = " + resultSet.getInt(1) + ";";
            resultSet = Main.myConnection.getResult(query);
            while (resultSet.next()){
                observableList.add(new Questions(resultSet.getString(1)));
            }
            tableView.setItems(observableList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public VBox editStudent(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15, 10, 15, 10));
        vBox.setSpacing(10);

        TextField [] signUpFields = new TextField[4];
        Label[] signUpLabels = new Label[4];
        String [] str = {"ID","First Name","Last Name", "Email", "Password"};
        for(int i = 0, y = 30; i < 4; i++, y += 120){
            signUpFields[i] = new TextField();
            signUpFields[i].setLayoutY(y + 50);
            signUpFields[i].setLayoutX(25);
            signUpFields[i].setFont(new Font(20));
            signUpLabels[i] = new Label(str[i]);
            signUpLabels[i].setLayoutY(y);
            signUpLabels[i].setLayoutX(25);
            signUpLabels[i].setFont(new Font(19));
            vBox.getChildren().add(signUpLabels[i]);
            vBox.getChildren().add(signUpFields[i]);
        }

        signUpFields[0].textProperty().addListener((observable, oldValue, newValue) -> {;
            Platform.runLater(()->{
                if(!Objects.equals(oldValue, newValue)){
                    if(signUpFields[0].getText().isEmpty()) {
                        for(int i = 1; i < 4; i++)
                            signUpFields[i].setText("");
                    }
                    else {
                        String query = "SELECT first_name, last_name, email FROM student WHERE student_id = " + signUpFields[0].getText() + ";";
                        ResultSet resultSet = Main.myConnection.getResult(query);
                        try {
                            int count = 0;
                            while (resultSet.next()){
                                for(int i = 1; i < 4; i++){
                                    signUpFields[i].setText(resultSet.getString(i));
                                }
                                count++;
                            }
                            if(count == 0){
                                for(int i = 1; i < 4; i++)
                                    signUpFields[i].setText("");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        });

        Button signUp = new Button("EDIT");
        signUp.setPrefSize(125, 50);
        signUp.setLayoutX(50);
        signUp.setLayoutY(525);
        signUp.setFont(new Font("Calibri", 15));

        signUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(int i = 0; i < 4; i++){
                    if(signUpFields[i].getText().isEmpty())
                        return;
                }
                try {
                    String query = "UPDATE student " +
                            "SET" +
                            "    email = '"+signUpFields[3].getText() + "', " +
                            "    first_name = '" + signUpFields[2].getText() + "', " +
                            "    last_name = '" + signUpFields[1].getText() + "' " +
                            "WHERE student_id = " + signUpFields[0].getText() + ";";
                    System.out.println(query);
                    Main.myConnection.runQuery(query);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Success!");
                    alert.setContentText("Student Edited Successfully");
                    alert.show();
                    alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent dialogEvent) {
                            delStage.close();
                        }
                    });
                }
                catch (Exception e){

                }
            }
        });

        vBox.getChildren().add(signUp);

        return vBox;
    }
}
