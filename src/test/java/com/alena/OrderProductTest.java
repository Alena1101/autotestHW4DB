package com.alena;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderProductTest extends AbstractTest{

    @Test
    @Order(1)

    void getOrderProduct_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM orders_products WHERE product_id=10";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM orders_products").addEntity(OrdersProductsEntity.class);
        //then
        Assertions.assertEquals(2, countTableSize);
        Assertions.assertEquals(23, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"4,7,2", "10,1,2", "10, 2, 1"})
    void getOrderProductById_whenValid_shouldReturn(String orderId, String productId, String quantity) throws SQLException {
        //given
        String sql = "SELECT * FROM orders_products WHERE order_id='" + orderId + "' AND product_id='"+ productId+"'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(3);
        }
        //then
        Assertions.assertEquals(quantity, nameString);
    }

}