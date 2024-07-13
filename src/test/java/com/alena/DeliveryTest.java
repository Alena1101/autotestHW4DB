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
public class DeliveryTest extends AbstractTest{

    @Test
    @Order(1)

    void getDelivery_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM delivery WHERE payment_method='Card'";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM delivery").addEntity(DeliveryEntity.class);
        //then
        Assertions.assertEquals(6, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"2,Yes", "7,No", "15, Yes"})
    void getDeliveryById_whenValid_shouldReturn(String deliveryId, String taken) throws SQLException {
        //given
        String sql = "SELECT * FROM delivery WHERE delivery_id='" + deliveryId + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(5);
        }
        //then
        Assertions.assertEquals(taken, nameString);
    }
    @Test
    @Order(3)
    void deleteDelivery_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id=" + 3).addEntity(DeliveryEntity.class);
        Optional<DeliveryEntity> entity = (Optional<DeliveryEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(entity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(entity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id=" + 3).addEntity(DeliveryEntity.class);
        Optional<DeliveryEntity> entityAfterDelete = (Optional<DeliveryEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(entityAfterDelete.isPresent());
    }


    @Test
    @Order(4)
    void addDelivery_whenValid_shouldSave() {
        //given
        DeliveryEntity entity = new DeliveryEntity();
        entity.setDeliveryId((short) 3);
        entity.setOrderId((short) 3);
        entity.setCourierId((short) 1);
        entity.setTaken("Yes");
        entity.setPaymentMethod("Cash");
        entity.setDateArrived("2023-02-26 18:04:36");

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id="+3).addEntity(DeliveryEntity.class);
        DeliveryEntity assertEntity = (DeliveryEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(assertEntity);
        Assertions.assertEquals("2023-02-26 18:04:36", assertEntity.getDateArrived());
    }



    @Test
    @Order(5)
    void addDelivery_whenNotValid_shouldThrow() {
        //given
        DeliveryEntity entity = new DeliveryEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
        ;
    }

}