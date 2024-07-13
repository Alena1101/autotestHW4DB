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
public class ProductTest extends AbstractTest{

    @Test
    @Order(1)

    void getProduct_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM products WHERE price>500";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM products").addEntity(ProductsEntity.class);
        //then
        Assertions.assertEquals(3, countTableSize);
        Assertions.assertEquals(10, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"GOJIRA ROLL, 300.0", "FUTOMAKI, 700.0", "SUNNY TEA, 456.0"})
    void getProductById_whenValid_shouldReturn(String menuName, Float price) throws SQLException {
        //given
        String sql = "SELECT * FROM products WHERE menu_name='" + menuName + "'";
        Statement stmt  = getConnection().createStatement();
        Float nameString = 0f;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getFloat(3);
        }
        //then
        Assertions.assertEquals(price, nameString);
    }
    @Test
    @Order(3)
    void deleteProduct_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 9).addEntity(ProductsEntity.class);
        Optional<ProductsEntity> entity = (Optional<ProductsEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(entity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(entity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id=" + 9).addEntity(ProductsEntity.class);
        Optional<ProductsEntity> entityAfterDelete = (Optional<ProductsEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(entityAfterDelete.isPresent());
    }


    @Test
    @Order(4)
    void addProduct_whenValid_shouldSave() {
        //given
        ProductsEntity entity = new ProductsEntity();
        entity.setProductId((short) 9);
        entity.setMenuName("COFFEE");
        entity.setPrice("79.0");

        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM products WHERE product_id="+9).addEntity(ProductsEntity.class);
        ProductsEntity creditEntity = (ProductsEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(creditEntity);
        Assertions.assertEquals("79.0", creditEntity.getPrice());
    }



    @Test
    @Order(5)
    void addProduct_whenNotValid_shouldThrow() {
        //given
        ProductsEntity entity = new ProductsEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
        ;
    }

}