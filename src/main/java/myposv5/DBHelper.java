/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myposv5;

import java.sql.*;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import myposv5.Product;

/**
 *
 * @author yench
 */
public class DBHelper {

    private static final String DB_URL = "jdbc:sqlite:db.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // 取得所有商品資料
    public static ObservableList<Product> getAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        String sql = "SELECT * FROM products";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                String img = rs.getString("img");
                String category = rs.getString("category");
                products.add(new Product(name, price, img, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
