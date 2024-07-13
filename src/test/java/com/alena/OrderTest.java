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
public class OrderTest extends AbstractTest{

    @Test
    @Order(1)

    void getOrder_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM orders WHERE customer_id=5";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM orders").addEntity(OrdersEntity.class);
        //then
        Assertions.assertEquals(1, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"1,1", "5, 5", "9, 9"})
    void getOrderById_whenValid_shouldReturn(String orderId, String customerId) throws SQLException {
        //given
        String sql = "SELECT * FROM orders WHERE order_id='" + orderId + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(2);
        }
        //then
        Assertions.assertEquals(customerId, nameString);
    }
    @Test
    @Order(3)
    void deleteOrder_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM orders WHERE order_id=" + 7).addEntity(OrdersEntity.class);
        Optional<OrdersEntity> entity = (Optional<OrdersEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(entity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(entity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM orders WHERE order_id=" + 7).addEntity(OrdersEntity.class);
        Optional<OrdersEntity> entityAfterDelete = (Optional<OrdersEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(entityAfterDelete.isPresent());
    }


    @Test
    @Order(4)
    void addOrder_whenValid_shouldSave() {
        //given
        OrdersEntity entity = new OrdersEntity();
        entity.setOrderId((short) 7);
        entity.setDateGet("2024-07-14");
        entity.setCustomerId((short)7);

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM orders WHERE order_id="+7).addEntity(OrdersEntity.class);
        OrdersEntity creditEntity = (OrdersEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(creditEntity);
        Assertions.assertEquals("2024-07-14", creditEntity.getDateGet());
    }



    @Test
    @Order(5)
    void addOrder_whenNotValid_shouldThrow() {
        //given
        OrdersEntity entity = new OrdersEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
        ;
    }

}