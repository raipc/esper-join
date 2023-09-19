package raipc;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

import java.util.Objects;

public class EsperJoin {
    public static void main(String[] args) throws Exception {
        Configuration configuration = getConfiguration();
        String epl = """
            @hint('exclude_plan(true)')
            @name('out')
            select * from A#keepall as a
            left outer join B#keepall as b on a.primaryKey = b.primaryKey
            left outer join C#keepall as c on a.primaryKey = c.primaryKey
            ;
            """;
        EPCompiled epCompiled = EPCompilerProvider.getCompiler().compile(epl, new CompilerArguments(configuration));
        EPRuntime runtime = EPRuntimeProvider.getRuntime("EsperJoin", configuration);
        runtime.initialize();
        runtime.getDeploymentService().deploy(epCompiled, new DeploymentOptions().setDeploymentId("exclude_plan"));
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType("A", Row.class);
        configuration.getCommon().addEventType("B", Row.class);
        configuration.getCommon().addEventType("C", Row.class);
        configuration.getCommon().addEventType(Row.class);
        return configuration;
    }

    public static class Row {
        private final int primaryKey;
        private final Integer foreignKey;

        public Row(int primaryKey, Integer foreignKey) {
            this.primaryKey = primaryKey;
            this.foreignKey = foreignKey;
        }

        public int getPrimaryKey() {
            return primaryKey;
        }

        public Integer getForeignKey() {
            return foreignKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Row row = (Row) o;
            return primaryKey == row.primaryKey && Objects.equals(foreignKey, row.foreignKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(primaryKey, foreignKey);
        }

        @Override
        public String toString() {
            return "Row{" +
                    "primaryKey=" + primaryKey +
                    ", foreignKey=" + foreignKey +
                    '}';
        }
    }
}
