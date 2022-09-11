package io.github.gavinray97.service;

import us.fatehi.chinook_database.ChinookDatabaseUtils;
import us.fatehi.chinook_database.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public final class ChinookDatabaseService {
    private static final MessageFormat H2_DB_URL_FORMAT = new MessageFormat("jdbc:h2:mem:{0};DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH");

    private ChinookDatabaseService() {
    }

    public static DataSource createChinookDatabase(String id) {
        String jdbcUrl = H2_DB_URL_FORMAT.format(new Object[]{id});
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            ChinookDatabaseUtils.createChinookDatabase(DatabaseType.postgresql, conn);
            ResultSet rs = conn.getMetaData().getTables(null, "public", null, null);
            boolean hasTables = rs.first();
            assert hasTables : "No tables found in the database, failed to initialize the DB";
            org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
            ds.setURL(jdbcUrl);
            return ds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
