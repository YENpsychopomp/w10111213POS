/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myposv5;
import javafx.beans.property.*;

/**
 *
 * @author yench
 */
public class Product {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty(0);
    private final StringProperty img = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();

    public Product(String name, int price, String img, String category) {
        this.name.set(name);
        this.price.set(price);
        this.img.set(img);
        this.category.set(category);
    }

    public Product(String name, int price, int quantity) {
        this.name.set(name);
        this.price.set(price);
        this.quantity.set(quantity);
    }

    public String getName() {
        return name.get();
    }

    public int getPrice() {
        return price.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int q) {
        this.quantity.set(q);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
    
    public String getImage(){
        return img.get();
    }
    
    public String getCategory(){
        return category.get();
    }
}

