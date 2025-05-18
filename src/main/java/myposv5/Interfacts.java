package myposv5;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Interfacts extends Application {

    private ObservableList<Product> productList;
    private final ObservableList<Product> cartList = FXCollections.observableArrayList();
    private Label totalLabel;
    private TilePane productPane;

    @Override
    public void start(Stage primaryStage) {
        productList = DBHelper.getAllProducts();

        // 建立購物車表格
        TableView<Product> cartTable = new TableView<>(cartList);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.getStyleClass().add("table"); // Bootstrap table style
        TableColumn<Product, String> nameCol = new TableColumn<>("品名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Integer> priceCol = new TableColumn<>("價格");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // 數量增減
        TableColumn<Product, Void> qtyChangeCol = new TableColumn<>("增減");
        qtyChangeCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnAdd = new Button("+");
            private final Button btnSub = new Button("-");
            private final Label qtyLabel = new Label();
            private final HBox box = new HBox(5);

            {
                btnAdd.getStyleClass().addAll("btn", "btn-success", "btn-sm");
                btnSub.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
                btnAdd.setStyle("-fx-background-color: #198754; -fx-text-fill: white;");
                btnSub.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btnAdd.setMinWidth(30);
                btnSub.setMinWidth(30);
                btnAdd.setMaxHeight(25);
                btnSub.setMaxHeight(25);
                btnAdd.setFocusTraversable(false);
                btnSub.setFocusTraversable(false);
                btnAdd.setPadding(new Insets(2, 8, 2, 8));
                btnSub.setPadding(new Insets(2, 8, 2, 8));
                qtyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(btnSub, qtyLabel, btnAdd);
                btnAdd.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    p.setQuantity(p.getQuantity() + 1);
                    refreshTotal();
                    updateItem(null, false);
                });
                btnSub.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    if (p.getQuantity() > 1) {
                        p.setQuantity(p.getQuantity() - 1);
                    } else {
                        getTableView().getItems().remove(p);
                    }
                    refreshTotal();
                    updateItem(null, false);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product p = getTableView().getItems().get(getIndex());
                    qtyLabel.setText(String.valueOf(p.getQuantity()));
                    setGraphic(box);
                }
            }
        });

        // 刪除按鈕
        TableColumn<Product, Void> delCol = new TableColumn<>("");
        delCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("刪除");

            {
                btn.getStyleClass().addAll("btn", "btn-outline-danger", "btn-sm");
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-border-color: #dc3545;");
                btn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    cartList.remove(p);
                    refreshTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cartTable.getColumns().addAll(nameCol, priceCol, qtyChangeCol, delCol);
        totalLabel = new Label("總金額：0 元");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; color: #0d6efd;");

        Button checkoutBtn = new Button("結帳");
        checkoutBtn.getStyleClass().addAll("btn", "btn-primary", "btn-lg");
        checkoutBtn.setStyle(
                "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        checkoutBtn.setOnAction(e -> handleCheckout());

        Label cartTitle = new Label("🛒購物車");
        cartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; color: #212529;");

        VBox leftBox = new VBox(10, cartTitle, cartTable, totalLabel, checkoutBtn);
        leftBox.setPadding(new Insets(10));
        leftBox.setPrefWidth(450);
        leftBox.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // --- 商品清單區域 ---
        // 商品分類按鈕區
        HBox categoryBox = new HBox(10);
        categoryBox.setPadding(new Insets(10));
        categoryBox.setAlignment(Pos.CENTER_LEFT);

        // 建立商品顯示區
        productPane = new TilePane();
        productPane.setPadding(new Insets(10));
        productPane.setHgap(10);
        productPane.setVgap(10);
        productPane.setPrefColumns(2);

        ScrollPane rightScroll = new ScrollPane(new VBox(categoryBox, productPane));
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background: #fff;");

        // 建立分類按鈕（不重複）
        Set<String> categorySet = productList.stream()
                .map(Product::getCategory)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Button allBtn = new Button("全部");
        allBtn.getStyleClass().addAll("btn", "btn-outline-secondary", "btn-sm");
        allBtn.setStyle(
                "-fx-background-color: #0d6efd;"
                + // 按鈕背景色（藍色）
                "-fx-border-color: #0d6efd;"
                + // 邊框顏色
                "-fx-text-fill: white;"
                + // 文字顏色白色
                "-fx-font-weight: bold;"
                + // 文字粗體
                "-fx-border-radius: 4;"
                + // 邊框圓角
                "-fx-background-radius: 4;" // 背景圓角
        );
        allBtn.setOnAction(e -> showProductsByCategory(null));
        categoryBox.getChildren().add(allBtn);

        for (String category : categorySet) {
            Button btn = new Button(category);
            btn.getStyleClass().addAll("btn", "btn-outline-primary", "btn-sm");
            btn.setStyle("-fx-background-color: #fff; -fx-border-color: #0d6efd; -fx-text-fill: #0d6efd;");
            btn.setOnAction(e -> showProductsByCategory(category));
            categoryBox.getChildren().add(btn);
        }

        // 預設顯示全部商品
        showProductsByCategory(null);

        // 主畫面
        SplitPane root = new SplitPane(leftBox, rightScroll);
        root.setDividerPositions(0.4);
        root.setStyle("-fx-background-color: #e9ecef;");

        Scene scene = new Scene(root, 900, 600);
        // 可選：載入自訂CSS檔案來加強Bootstrap風格
        // scene.getStylesheets().add(getClass().getResource("/bootstrapfx.css").toExternalForm());
        primaryStage.setTitle("POS 介面（分類功能）");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 根據分類顯示商品
    private void showProductsByCategory(String category) {
        productPane.getChildren().clear();
        for (Product p : productList) {
            if (category == null || p.getCategory().equals(category)) {
                String imagePath = "/imgs/" + p.getImage();
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);

                Button btn = new Button(p.getName() + "\n$" + p.getPrice());
                btn.setGraphic(imageView);
                btn.setContentDisplay(ContentDisplay.TOP);
                btn.setPrefSize(140, 120);
                btn.setWrapText(true);
                btn.getStyleClass().addAll("btn", "btn-light", "shadow-sm");
                btn.setStyle(
                        "-fx-background-color: #fff; -fx-border-color: #dee2e6; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #212529;");
                btn.setOnAction(e -> addToCart(p));
                productPane.getChildren().add(btn);
            }
        }
    }

    private void addToCart(Product product) {
        for (int i = 0; i < cartList.size(); i++) {
            Product p = cartList.get(i);
            if (p.getName().equals(product.getName())) {
                p.setQuantity(p.getQuantity() + 1);
                refreshTotal();
                // 觸發 ObservableList 更新，讓 TableView 重新渲染
                cartList.set(i, p);
                return;
            }
        }
        cartList.add(new Product(product.getName(), product.getPrice(), 1));
        refreshTotal();
    }

    private void refreshTotal() {
        int total = cartList.stream().mapToInt(p -> p.getPrice() * p.getQuantity()).sum();
        totalLabel.setText("總金額：" + total + " 元");
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "購物車是空的！");
            alert.showAndWait();
            return;
        }

        StringBuilder details = new StringBuilder();
        int total = 0;

        for (Product p : cartList) {
            int subtotal = p.getPrice() * p.getQuantity();
            details.append(p.getName())
                    .append(" x ")
                    .append(p.getQuantity())
                    .append(" = ")
                    .append(subtotal)
                    .append(" 元\n");
            total += subtotal;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("結帳明細");
        alert.setHeaderText("總金額：" + total + " 元");
        alert.setContentText(details.toString());
        alert.showAndWait();

        cartList.clear();
        refreshTotal();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
