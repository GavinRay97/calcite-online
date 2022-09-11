package io.github.gavinray97;

import io.github.gavinray97.service.SessionScopedCalciteSchema;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jooq.Result;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.sql.Connection;

@ApplicationScoped
@Path("/")
public class Application {

    @Inject
    SessionScopedCalciteSchema calciteSchema;

    @GET
    public TemplateInstance index() {
        SchemaPlus rootSchema = calciteSchema.getCalciteConnection().getRootSchema();
        return Templates.index(rootSchema);
    }

    @GET
    @Path("/schema")
    public String schema() {
        SchemaPlus rootSchema = calciteSchema.getCalciteConnection().getRootSchema();
        SchemaPlus chinookSchema = rootSchema.getSubSchema("chinook").getSubSchema("public");

        StringBuilder sb = new StringBuilder();
        for (String tableName : chinookSchema.getTableNames()) {
            sb.append("\t").append(tableName).append(":");
            sb.append("\n");

            Table table = chinookSchema.getTable(tableName);
            RelDataType rowType = table.getRowType(calciteSchema.getCalciteConnection().getTypeFactory());
            for (var field : rowType.getFieldList()) {
                sb.append("\t\t").append(field.getName()).append(": ").append(field.getType().getFullTypeString());
                sb.append("\n");
            }
        }

        return """
                 <table class="ui celled table">
                    <thead>
                        <tr>
                            <th>SCHEMA</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><pre>$SCHEMA</pre></td>
                        </tr>
                    </tbody>
                </table>
                """.replace("$SCHEMA", sb);
    }

    @POST
    @Path("/query")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/html")
    public String handleQuery(@FormParam String query) {
        try {
            Connection conn = calciteSchema.getCalciteConnection();
            Result result = DSL.using(conn).fetch(query);
            if (query.startsWith("EXPLAIN")) {
                String plan = result.getValues(0).get(0).toString();
                return """
                         <table class="ui celled table">
                            <thead>
                                <tr>
                                    <th>PLAN</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><pre>$PLAN</pre></td>
                                </tr>
                            </tbody>
                        </table>
                        """.replace("$PLAN", plan);
            }
            return result.formatHTML();
        } catch (Exception e) {
            return """
                     <table class="ui celled table">
                        <thead>
                            <tr>
                                <th>ERROR</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><pre>$ERROR</pre></td>
                            </tr>
                        </tbody>
                    </table>
                    """.replace("$ERROR", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {
        public static native TemplateInstance index(SchemaPlus rootSchema);
    }
}
