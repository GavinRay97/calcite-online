package io.github.gavinray97.service;

import org.apache.calcite.adapter.jdbc.JdbcCatalogSchema;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.schema.SchemaPlus;

import javax.enterprise.context.SessionScoped;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.UUID;

@SessionScoped
public class SessionScopedCalciteSchema implements java.io.Serializable {

    private UUID id;
    private DataSource h2DataSource;
    private CalciteConnection calciteConnection;

    public SessionScopedCalciteSchema() {
        this.id = UUID.randomUUID();
        this.h2DataSource = ChinookDatabaseService.createChinookDatabase(id.toString());
        this.calciteConnection = initCalciteConnection();

        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        JdbcCatalogSchema catalog = JdbcCatalogSchema.create(rootSchema, "chinook", h2DataSource, null);
        rootSchema.add("chinook", catalog);
    }

    private static CalciteConnection initCalciteConnection() {
        try {
            Class.forName(Driver.class.getName());
            DriverManager.registerDriver(new Driver());
            Properties properties = new Properties();
            properties.setProperty(CalciteConnectionProperty.FUN.camelName(), "standard,postgresql");
            properties.setProperty(CalciteConnectionProperty.LEX.camelName(), Lex.JAVA.name());
            return DriverManager.getConnection("jdbc:calcite:", properties).unwrap(CalciteConnection.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public CalciteConnection getCalciteConnection() {
        return calciteConnection;
    }

    public void setCalciteConnection(CalciteConnection calciteConnection) {
        this.calciteConnection = calciteConnection;
    }


    public DataSource getH2DataSource() {
        return h2DataSource;
    }

    public void setH2DataSource(DataSource h2DataSource) {
        this.h2DataSource = h2DataSource;
    }

}
