package com;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {


    @FXML
    private TextField textField_host;

    @FXML
    private TextField textField_port;

    @FXML
    private TextField textField_user;

    @FXML
    private TextField textField_password;

    @FXML
    private Button btn_logOn;

    @FXML
    private Button btn_update;

    @FXML
    private CheckBox cb_showPass;

    @FXML
    private Button testConn;

    @FXML
    private TableColumn<ResultTask, String> tableCol_sql;

    @FXML
    private TableColumn<ResultTask, String> tableCol_date;
    @FXML
    private TableColumn<ResultTask, Integer> tableCol_id;
    @FXML
    private TextField textField_filter;
    @FXML
    private TableView tableView;


    private String date;


    private String dbpass;

    private ObservableList<ResultTask> list = FXCollections.observableArrayList();
    private Connection conn;

    @FXML
    private Button btn_clear;

    @FXML
    private Label label_date;

    @FXML
    private Label label_state;
    @FXML
    public void initialize() {
        initConfigComponents();
    }

    static {
        try {
            Tooltip obj = new Tooltip();

            Class<?> clazz = obj.getClass().getDeclaredClasses()[1];
            if(!clazz.getName().contains("TooltipBehavior")){
                clazz = obj.getClass().getDeclaredClasses()[0];
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor(
                    Duration.class,
                    Duration.class,
                    Duration.class,
                    boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(
                    new Duration(250),  //open
                    new Duration(500000), //visible
                    new Duration(200),  //close
                    false);
            Field fieldBehavior = obj.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(obj, tooltipBehavior);
        }
        catch (Exception e) {
            System.out.println("error:"+e);
        }
    }
    private int index;
    private void initConfigComponents() {



        testConn.setOnAction(event -> conn = conn());

        btn_clear.setDisable(true);
        btn_update.setDisable(true);
        btn_logOn.setDisable(true);
        tableCol_id.setCellValueFactory(new PropertyValueFactory("index"));
        tableCol_id.setStyle( "-fx-alignment: CENTER;");



        tableCol_sql.setCellValueFactory(new PropertyValueFactory("sql"));
        tableCol_date.setCellValueFactory(new PropertyValueFactory("date"));


        cb_showPass.setSelected(true);
        dbpass = textField_password.textProperty().get().trim();


        textField_password.textProperty().addListener((observable, oldValue, newValue) -> {

            if (cb_showPass.isSelected()) {
                dbpass = newValue;
            } else {
                dbpass = oldValue;
            }
        });

        cb_showPass.setOnAction(event -> {
            textField_password.setEditable(true);
            if (cb_showPass.isSelected()) {
                textField_password.setEditable(true);
                textField_password.setText(dbpass);
            } else {
                textField_password.setEditable(false);
                textField_password.setText(Util.getEcho(dbpass));
            }
        });

        btn_logOn.setOnAction(event -> {
            if (conn != null) {
                date = Util.ftime();
                label_date.setText("下断时间："+date);
                label_state.setText((String.format("[%s]：%s", Util.ftime(), "断点成功")));
                try {
                    conn.prepareStatement("SET global general_log=on").executeUpdate();
                    conn.prepareStatement("SET GLOBAL log_output='table'").executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                index = 0;
                btn_clear.setDisable(false);
                btn_update.setDisable(false);
            } else {
                label_state.setText((String.format("[%s]：%s", Util.ftime(), "数据库未连接或连接超时")));
                showAlert(Alert.AlertType.ERROR, "错误", "数据库未连接或连接超时");
                btn_clear.setDisable(true);
                btn_update.setDisable(true);
            }

        });
        FilteredList<ResultTask> filteredList = new FilteredList<>(list, p -> true);


        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            filteredList.setPredicate(resultTask -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (resultTask.getSql().toLowerCase().contains(newValue)) {
                    return true;
                }
                return false;
            });
            tableView.setItems(filteredList);
        };
        btn_update.setOnAction(event -> {
            if(tableView.getItems().size()>0){
                clear(changeListener);
            }
            label_state.setText((String.format("[%s]：%s", Util.ftime(), "查询成功")));
            try {
                Statement stmt = conn.createStatement();

                String logSql = "select * from mysql.general_log where command_type =\"Query\" OR command_type =\"Execute\" order by event_time desc limit 2";

                logSql = "select date_format(event_time,'%Y-%m-%d %H:%i:%S') as event_date ,argument from general_log where command_type='Query' and argument not like '/* mysql-conne%%' and argument not like 'SET auto%%'and argument not like 'SET sql_mo%%'and argument not like 'select event_time,argument from%%' and event_time>'" + date + "'";

                ResultSet log = stmt.executeQuery(logSql);

                ResultTask resultTask;
                while (log.next()) {
                    String sql = log.getString("argument");
                    String event_time = log.getString("event_date");
                    if (!sql.equals(logSql)) {
                        index ++;
                        resultTask = new ResultTask();
                        resultTask.setIndex(index);
                        resultTask.setDate(event_time);
                        resultTask.setSql(sql);
                        list.add(resultTask);
                    }
                }
            } catch (SQLException e) {
                label_state.setText((String.format("[%s]：%s", Util.ftime(), "查询出错，"+e.getMessage())));
            }

            tableView.setItems(list);
        });

        btn_clear.setOnAction(event -> {
            clear(changeListener);
            label_state.setText((String.format("[%s]：%s", Util.ftime(), "清除成功")));
        });
        textField_filter.textProperty().addListener(changeListener);


        tableView.setRowFactory(param -> {
            TableRow<ResultTask> row = new TableRow();

            row.setOnMouseClicked(event -> {
                if(!row.isEmpty()){
                    if( event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){

                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(row.getItem().getSql());
                        clipboard.setContent(content);

                    } else if ( event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {

                        row.setEditable(true);
                    }else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {

                        Tooltip tt = new Tooltip();
                        tt.setStyle("-fx-font: normal bold 13 Langdon; "
                                + "-fx-base: #AE3522; "
                                + "-fx-text-fill: orange;");

                        tt.setText(row.getItem().getSql());
                        tt.setWrapText(true);
                        tt.setMaxWidth(300);

                        tableView.setTooltip(tt);

                    }
                }

            });
            return row;
        });
    }

    private void clear(ChangeListener<String> changeListener){
        index = 0;
        tableView.setItems(list);
        textField_filter.textProperty().removeListener(changeListener);
        textField_filter.clear();
        tableView.getItems().clear();
        textField_filter.textProperty().addListener(changeListener);
    }
    public Connection conn() {
        String dbHost;
        int dbPort;
        String dbUser;
        dbHost = textField_host.textProperty().get().trim();
        dbPort = Integer.parseInt(textField_port.textProperty().get().trim());
        dbUser = textField_user.textProperty().get().trim();

        Connection conn = Util.getConn(dbHost, dbPort, dbUser, dbpass);
        if (conn != null) {
            label_state.setText((String.format("[%s]：%s", Util.ftime(), "数据库连接成功")));
            showAlert(Alert.AlertType.INFORMATION, "提示", "数据库连接成功");

            btn_logOn.setDisable(false);
        } else {
            label_state.setText((String.format("[%s]：%s", Util.ftime(), "数据库连接失败")));
            showAlert(Alert.AlertType.ERROR, "错误", "数据库连接失败");
            btn_clear.setDisable(true);
            btn_update.setDisable(true);
            btn_logOn.setDisable(true);
        }

        return conn;
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}