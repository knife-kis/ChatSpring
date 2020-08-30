//package ru.geekbrains.product;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Repository
//public class ProductRepository {
//
//    private final Connection conn;
//
//    @Autowired
//    public ProductRepository(DataSource dataSource) throws SQLException {
//        this(dataSource.getConnection());
//    }
//
//    public ProductRepository(Connection conn) throws SQLException {
//        this.conn = conn;
//        createTableIfNotExists(conn);
//    }
//
//    public void insert(Product product) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(
//                "insert into products(title, cost) values (?, ?);")) {
//            stmt.setString(1, product.getTitle());
//            stmt.setDouble(2, product.getCost());
//            stmt.execute();
//        }
//    }
//
//    public Product findByTitle(String title) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(
//                "select id, title, cost from products where title = ?")) {
//            stmt.setString(1, title);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                return new Product(rs.getInt(1), rs.getString(2), rs.getDouble(3));
//            }
//        }
//        return new Product(-1, "", 0);
//    }
//
//    public Product findById(Long id) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(
//                "select id, title, cost from products where id = ?")) {
//            stmt.setLong(1, id);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                return new Product(rs.getInt(1), rs.getString(2), rs.getDouble(3));
//            }
//        }
//        return new Product(-1, "", 0);
//    }
//
//    public List<Product> getallProducts() throws SQLException {
//        List<Product> res = new ArrayList<>();
//        try (Statement stmt = conn.createStatement()) {
//            ResultSet rs = stmt.executeQuery("select id, title, cost from products");
//
//            while (rs.next()) {
//                res.add(new Product(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
//            }
//        }
//        return res;
//    }
//
//    private void createTableIfNotExists(Connection conn) throws SQLException {
//        try (Statement stmt = conn.createStatement()) {
//            stmt.execute("create table if not exists products (\n" +
//                    "\tid int auto_increment primary key,\n" +
//                    "    title varchar(25),\n" +
//                    "    cost double (25),\n" +
//                    "    unique index title_unique(title)\n" +
//                    ");");
//        }
//    }
//
//    public Product update(Product product) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(
//                "update products SET title = (?) where id = (?);")) {
//            stmt.setString(1, product.getTitle());
//            stmt.setString(2, String.valueOf(product.getId()));
//            stmt.execute();
//        }
//        return product;
//    }
//}
